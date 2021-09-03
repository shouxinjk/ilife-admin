package com.pcitech.iLife.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.google.gson.Gson;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.PromotionGoodsResp;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.response.get.PromotionCodeResp;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.JdHelper;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 通过拼多多API接口，补充导购链接。
 * 通过自动任务触发，每3分钟触发，每次处理30条（接口最大支持40条）。每天处理14400条。
 * 
 * 查询条件：
 * status.sync==null
 * 
 * 如果没有待处理内容则等待
 */
@Service
public class JdItemSync {
    private static Logger logger = LoggerFactory.getLogger(JdItemSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    JdHelper jdHelper;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public JdItemSync() {
    }
    
    private void syncCpsLinks(BaseDocument item) {
		String itemKey = item.getProperties().get("itemKey").toString();

		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		//设置状态。注意，需要设置index=pending 等待重新索引。只要有CPS链接，就可以推广了
		//状态更新
		Map<String,Object> status = new HashMap<String,Object>();
		status.put("sync", "ready");
		status.put("index", "pending");//等待重新索引
		doc.getProperties().put("status", status);
		//时间戳更新
		Map<String,Object> timestamp = new HashMap<String,Object>();
		timestamp.put("sync", new Date());//CPS链接生成时间
		doc.getProperties().put("timestamp", timestamp);
		
		try {
			//调用API接口生成CPS链接
			PromotionCodeResp result=null;
			String  url = item.getProperties().get("link").toString();
			result = jdHelper.getCpsLink(url,"system");
			if(result!=null) {
				Map<String,String> links = new HashMap<String,String>();
				links.put("wap2", result.getClickURL());
				links.put("web2", result.getClickURL());
				doc.getProperties().put("link", links);
			}else {
				logger.warn("获取CPS链接失败："+url);
			}
			
			//获取详情更新类目
			Pattern p=Pattern.compile("\\d+"); 
			Matcher m=p.matcher(url); 
			while(m.find()) { //仅处理第一个即可
			    String skuId = m.group(); 
				PromotionGoodsResp[] goods = jdHelper.getDetail(skuId);
				if(goods != null && goods.length>0) {
					PromotionGoodsResp category = goods[0];
					List<String> categories = new ArrayList<String>();
					categories.add(category.getCidName());
					categories.add(category.getCid2Name());
					categories.add(category.getCid3Name());
					doc.getProperties().put("category", categories);//更新类目，包含3级别分类
    					doc.getProperties().put("logo", category.getImgUrl());//更新首图 img
				}else {
					logger.warn("查询详情失败。【SKU】"+skuId+"【URL】"+url);
				}
				break;
			} 
			
			//TODO：获取佣金信息
			
			
			//更新doc
			arangoClient.update("my_stuff", itemKey, doc);    	
			processedAmount++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，查询Arangodb中(status==null || status.sync==null) and (source=="jd")的记录，限制30条。
	 * 2，通过拼多多API接口查询生成商品导购链接
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		logger.info("item sync job start. " + new Date());
    		
    		//1，查询待处理商品记录 返回itemKey、商品ID、商品链接
    		//for doc in my_stuff filter (doc.source == "jd") and (doc.status==null or doc.status.sync==null) limit 30 return {itemKey:doc._key,id:split(doc.link.web,"id=")[1],link:doc.link.web}    		
        String query = "for doc in my_stuff filter "
        		+ "(doc.source == \"jd\") and "
//        		+ "(doc.status==null or doc.status.sync==null) "
        		+ "doc.status.sync==\"pending\" "
        		+ "update doc with {status:{sync:\"ready\"}} in my_stuff "//查询时即更新状态：仅更新记录状态，index状态需要根据计算结果设置
        		+ "limit 1000 "//一个批次处理30条
        		+ "return {itemKey:doc._key,link:doc.link.web}";
        
        try {
            arangoClient = new ArangoDbClient(host,port,username,password,database);
            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
            totalAmount = items.size();
            if(totalAmount ==0) {//如果没有了就提前收工
	            	logger.debug("没有待同步京东商品条目");
	            	arangoClient.close();//链接还是要关闭的
	            	return;
            }
            for (BaseDocument item:items) {
            		syncCpsLinks(item);//逐条查询CPS链接并更新ArangoDB doc
            }
        } catch (Exception e) {
            logger.error("Failed to execute query.",e);
        }

		//完成后关闭arangoDbClient
		arangoClient.close();
		if(totalAmount == 0)//啥活都没干，发啥消息
			return;
		//发送处理结果到管理员
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", "o8HmJ1EdIUR8iZRwaq1T7D_nPIYc");//固定发送
		msg.put("title", "导购数据同步任务结果");
		msg.put("task", "京东CPS链接 已同步");
		msg.put("time", fmt.format(new Date()));
		msg.put("remark", "预期数量："+totalAmount
				+ "\n实际数量："+processedAmount
				+ "\n数量差异："+(totalAmount-processedAmount));
		msg.put("color", totalAmount-processedAmount==0?"#FF0000":"#000000");

		result = HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/data-sync-notify", 
				msg,header);
		//3，更新通知状态
		if(result.getBooleanValue("status")) {
			logger.info("clearing notification msg sent.[msgId] " + result.getString("msgId"));
		}
        logger.info("Clearing Notification job executed.[msg]" + msg);
		//处理数量归零
        processedAmount = 0;
    }

}
