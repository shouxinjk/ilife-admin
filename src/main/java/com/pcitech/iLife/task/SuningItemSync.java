package com.pcitech.iLife.task;

import java.net.URLDecoder;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.SuningHelper;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 通过API接口，补充商品详情 。
 * 通过自动任务触发，每3分钟触发，每次处理30条（接口最大支持40条）。每天处理14400条。
 * 
 * 查询条件：
 * status.sync==null
 * 
 * 如果没有待处理内容则等待
 */
@Service
public class SuningItemSync {
    private static Logger logger = LoggerFactory.getLogger(SuningItemSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    SuningHelper suningHelper;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public SuningItemSync() {
    }
    
    private void syncCpsLinks(BaseDocument item) {
		String itemKey = item.getProperties().get("itemKey").toString();
		logger.error("got Sunning item.[key]"+itemKey);
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		Map<String,Object> syncStatus = new HashMap<String,Object>();
		syncStatus.put("sync", true);
		Map<String,Object> syncTimestamp = new HashMap<String,Object>();
		syncTimestamp.put("sync", new Date());	
		doc.setKey(itemKey);
		doc.getProperties().put("status", syncStatus);
		doc.getProperties().put("timestamp", syncTimestamp);
		String  webUrl = item.getProperties().get("web").toString();
		String  wapUrl = item.getProperties().get("wap").toString();
		String brokerId = "default";//默认都认为是平台自己生成的
		
		try {
			//更新CPS链接：直接覆盖
			Map<String,String> links = new HashMap<String,String>();
			JSONObject result = suningHelper.generateCpsLink("default", webUrl);
			if(result != null) //部分商品可能获取失败：会导致链接不会被更新
				links.put("web2", URLDecoder.decode(result.getString("extendUrl"))+"&sub_user="+brokerId);
			result = suningHelper.generateCpsLink("default", wapUrl);
			if(result != null) //部分商品可能获取失败：会导致链接不会被更新
				links.put("wap2", URLDecoder.decode(result.getString("extendUrl"))+"&sub_user="+brokerId);
			doc.getProperties().put("link", links);
			//获取详情更新类目
			Pattern p=Pattern.compile("(\\d+)/(\\d+)"); 
			Matcher m=p.matcher(webUrl); 
			if(m.find() && m.groupCount()>=2 ) { //https://product.suning.com/0000000000/12208306208.html
			    String skuId = m.group(2)+"-"+m.group(1); //组装skuId
				result = suningHelper.getDetail(skuId);
				if(result != null) { //部分商品可能获取失败：不更新数据，直接跳过
					JSONObject commodityInfo = result.getJSONObject("commodityInfo");
					
					//更新标题
					doc.getProperties().put("title", commodityInfo.getString("commodityName"));
	
					//如果summary
					doc.getProperties().put("summary", commodityInfo.getString("sellingPoint"));
					
					//更新图片列表：注意脚本中已经有采集，此处使用自带的内容
					JSONArray imgList =  commodityInfo.getJSONArray("pictureUrl");
					doc.getProperties().put("logo", imgList.getJSONObject(0).getString("picUrl"));//将第一张展示图片作为logo
					/**
					//不更新图片列表
					List<String> images = new ArrayList<String>();
					for(int i=0;i<imgList.size();i++) {//增加展示图片
						images.add(imgList.getJSONObject(i).getString("picUrl"));
					}
					doc.getProperties().put("images", images);
					//**/
					
					//props信息脚本端已经采集，不再更新
					/*
					Map<String,String> props = new HashMap<String,String>();
					//如果原来已经有属性，需要继续保留
					if(doc.getProperties().get("props") != null) {
						Map<String,String> oldProps = (Map<String,String>)doc.getProperties().get("props");
						props = oldProps;
					}
					props.put("品牌", good.getBaseInfo().getBrandName());//增加品牌属性
					props.put("品牌国家", good.getBaseInfo().getBrandCountryName());//增加品牌国家属性
					doc.getProperties().put("props", props);
					//**/
					
					//增加类目:直接替换原有类目
					List<String> categories = new ArrayList<String>();
					JSONObject categoryObj = result.getJSONObject("categoryInfo");
					if(categoryObj.getString("firstSaleCategoryName")!=null)categories.add(categoryObj.getString("firstSaleCategoryName"));
					if(categoryObj.getString("secondSaleCategoryName")!=null)categories.add(categoryObj.getString("secondSaleCategoryName"));
					if(categoryObj.getString("thirdSaleCategoryName")!=null)categories.add(categoryObj.getString("thirdSaleCategoryName"));
					doc.getProperties().put("category", categories);//更新类目，包含多级分类
		
					//更新佣金信息：默认为2-party
					String rateStr = commodityInfo.getString("rate");
					String priceStr = commodityInfo.getString("commodityPrice");
					double rate = 0;
					double amount = 0;
					double price = 0;
					try {
						rate = Double.parseDouble(rateStr);
						price = Double.parseDouble(priceStr);
						amount = price*rate/100;//注意rate是百分比
					}catch(Exception ex) {
						logger.warn("cannot parse rate to double.[rate str]",rateStr);
					}
					if(rate>0) {
						Map<String,String> profit = new HashMap<String,String>();
						profit.put("type", "2-party");
						profit.put("rate", ""+rate);
						profit.put("amount", ""+amount);
						doc.getProperties().put("profit", profit);
					}else {
						logger.warn("cannot set profit.amount or profit.rate.");
					}
				}
			}
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
	 * 1，查询Arangodb中(status==null || status.sync==null) and (source=="suning")的记录，限制30条。
	 * 2，通过API接口查询生成商品导购链接
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		logger.info("Pinduoduo item sync job start. " + new Date());
    		
    		//1，查询待处理商品记录 返回itemKey、商品ID、商品链接
    		//for doc in my_stuff filter (doc.source == "jd") and (doc.status==null or doc.status.sync==null) limit 30 return {itemKey:doc._key,id:split(doc.link.web,"id=")[1],link:doc.link.web}    		
        String query = "for doc in my_stuff filter "
        		+ "(doc.source == \"suning\") and "
//        		+ "(doc.status==null or doc.status.sync==null) "
        		+ "doc.status.crawl==\"pending\" "
        		+ "update doc with {status:{crawl:\"ready\"}} in my_stuff "//查询时即更新状态
        		+ "limit 1000 "//一个批次处理30条
        		+ "return {itemKey:doc._key,web:doc.link.web,wap:doc.link.wap}";
        
        try {
            arangoClient = new ArangoDbClient(host,port,username,password,database);
            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
            totalAmount = items.size();
            if(totalAmount ==0) {//如果没有了就提前收工
	            	logger.debug("没有待同步苏宁商品条目");
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
		msg.put("task", "苏宁CPS链接 已同步");
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
