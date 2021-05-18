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
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
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
public class PddItemSync {
    private static Logger logger = LoggerFactory.getLogger(PddItemSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    PddHelper pddHelper;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public PddItemSync() {
    }
    
	private List<String> getGoodsSignList(String goodsSign){
		List<String> goodsSignList = new ArrayList<String>();
		goodsSignList.add(goodsSign);
		return goodsSignList;
	}
	
    private void syncCpsLinks(BaseDocument item) {
		String itemKey = item.getProperties().get("itemKey").toString();
		String  url = item.getProperties().get("link").toString();
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		Map<String,Object> syncStatus = new HashMap<String,Object>();
		syncStatus.put("sync", true);
		Map<String,Object> syncTimestamp = new HashMap<String,Object>();
		syncTimestamp.put("sync", new Date());	
		doc.setKey(itemKey);
		doc.getProperties().put("status", syncStatus);
		doc.getProperties().put("timestamp", syncTimestamp);
		
		//匹配获取goodsSign
		Pattern p=Pattern.compile("s=([A-Za-z0-9_\\-]+$)"); 
		Matcher m=p.matcher(url); 
		while(m.find()) { //仅在发现后进行
			String goodsSign = m.group(m.groupCount()); //只处理最后一组
			logger.error("check goodsSign "+goodsSign);
			PddDdkGoodsPromotionUrlGenerateResponse result=null;
			String brokerId = "default";//默认只生成平台推广链接
			try {
				//调用API接口生成CPS链接
				result = pddHelper.generateCpsLinksByGoodsSign(brokerId,getGoodsSignList(goodsSign));
				Map<String,String> links = new HashMap<String,String>();
				links.put("wap2", result.getGoodsPromotionUrlGenerateResponse().getGoodsPromotionUrlList().get(0).getMobileUrl());
				links.put("web2", result.getGoodsPromotionUrlGenerateResponse().getGoodsPromotionUrlList().get(0).getUrl());
				doc.getProperties().put("link", links);
			
				//获取商品详情，填充tags等信息
				GoodsDetailResponse resp = pddHelper.getItemDetail(brokerId, goodsSign);
				List<String> tags = new ArrayList<String>();
				List<String> unifiedTags = resp.getGoodsDetails().get(0).getUnifiedTags();//商品unified tags
				tags.add(resp.getGoodsDetails().get(0).getOptName());//商品标签
				for(String tag:unifiedTags) {//商品unified tags里有null项 ，需要排除
					if(null != tag && tag.trim().length() > 0 && !"null".equalsIgnoreCase(tag))
						tags.add(tag);
				}
				doc.getProperties().put("tags", tags);//更新类目，包含3级别分类
				doc.getProperties().put("logo", resp.getGoodsDetails().get(0).getGoodsImageUrl());//更新首图 img
				doc.getProperties().put("summary", resp.getGoodsDetails().get(0).getGoodsDesc());//更新简介
				//更新品牌信息到prop列表
				Map<String,String> props = new HashMap<String,String>();
				//如果原来已经有属性，需要继续保留
				if(doc.getProperties().get("props") != null) {
					Map<String,String> oldProps = (Map<String,String>)doc.getProperties().get("props");
					props = oldProps;
				}
				props.put("品牌", resp.getGoodsDetails().get(0).getBrandName());//增加品牌属性
				doc.getProperties().put("props", props);
				//TODO 当前仅返回分类ID列表，尚未能获取分类明细。待完成
				
				//更新doc
				arangoClient.update("my_stuff", itemKey, doc);    	
				processedAmount++;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，查询Arangodb中(status==null || status.sync==null) and (source=="pdd")的记录，限制30条。
	 * 2，通过拼多多API接口查询生成商品导购链接
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		logger.info("Pinduoduo item sync job start. " + new Date());
    		
    		//1，查询待处理商品记录 返回itemKey、商品ID、商品链接
    		//for doc in my_stuff filter (doc.source == "pdd") and (doc.status==null or doc.status.sync==null) limit 30 return {itemKey:doc._key,id:split(doc.link.web,"id=")[1],link:doc.link.web}    		
        String query = "for doc in my_stuff filter "
        		+ "(doc.source == \"pdd\") and "
        		+ "(doc.status==null or doc.status.sync==null) "
        		+ "limit 30 "//一个批次处理30条
        		+ "return {itemKey:doc._key,link:doc.link.web}";
        
        try {
            arangoClient = new ArangoDbClient(host,port,username,password,database);
            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
            totalAmount = items.size();
            for (BaseDocument item:items) {
            		syncCpsLinks(item);//逐条查询CPS链接并更新ArangoDB doc
            }
        } catch (Exception e) {
            logger.error("Failed to execute query.",e);
        }

		//完成后关闭arangoDbClient
		arangoClient.close();
		
		//发送处理结果到管理员
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", "o8HmJ1EdIUR8iZRwaq1T7D_nPIYc");//固定发送
		msg.put("title", "导购数据同步任务结果");
		msg.put("task", "拼多多CPS链接 已同步");
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
