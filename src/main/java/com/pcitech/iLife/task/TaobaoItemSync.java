package com.pcitech.iLife.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.taobao.TaobaoHelper;
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
 * 通过淘宝导购API接口，补充一级类目、叶子类目、首图。
 * 通过自动任务触发，每3分钟触发，每次处理30条（接口最大支持40条）。每天处理14400条。
 * 
 * 查询条件：
 * status.sync==null
 * 
 * 如果没有待处理内容则等待
 */
@Service
public class TaobaoItemSync {
    private static Logger logger = LoggerFactory.getLogger(TaobaoItemSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    TaobaoHelper taobaoHelper;

    public TaobaoItemSync() {
    }

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，查询Arangodb中(status==null || status.sync==null) and (source=="taobao" || source=="tmall" || source=="fliggy")的记录，限制30条。
	 * 2，通过淘宝导购接口查询商品信息
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		logger.info("Taobao item sync job start. " + new Date());
    		
    		//1，查询待处理商品记录 返回itemKey、商品ID、商品链接
    		//for doc in my_stuff filter (doc.source == "tmall" or doc.source=="taobao" or doc.source=="fliggy") and (doc.status==null or doc.status.sync==null) limit 30 return {itemKey:doc._key,id:split(doc.link.web,"id=")[1],link:doc.link.web}    		
        String query = "for doc in my_stuff filter "
        		+ "(doc.source == \"tmall\" or doc.source==\"taobao\" or doc.source==\"fliggy\") and "
        		+ "(doc.status==null or doc.status.sync==null) "
        		+ "limit 30 "//限定为30条
        		+ "return {itemKey:doc._key,id:split(doc.link.web,\"id=\")[1],link:doc.link.web}";

        // execute AQL queries
        Map<String,String> itemMap = new HashMap<String,String>();//预先准备一个本地队列，用于记录商品ID和链接
        List<String> itemList = new ArrayList<String>();//存放所有商品的淘宝id
        
        try {
            arangoClient = new ArangoDbClient(host,port,username,password,database);
            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
            for (BaseDocument item:items) {
                //itemMap.put(item.getProperties().get("link").toString(), item.getProperties().get("itemKey").toString());
            		itemMap.put(item.getProperties().get("id").toString(), item.getProperties().get("itemKey").toString());
                itemList.add(item.getProperties().get("id").toString());
            }
            logger.debug("listed item map.",itemMap);
            logger.debug("listed item ids.",itemList);
        } catch (Exception e) {
            logger.error("Failed to execute query.",e);
        }
        
    		//将所有商品ID组织逗号分隔的字符串
    		String ids = StringUtils.join(itemList.toArray(),",");
    		List<NTbkItem> results = new ArrayList<NTbkItem>();
    		try {
    			results = taobaoHelper.getItemDetail(ids);
		} catch (ApiException ex) {
			logger.error("failed query item info from taobao.",ex);
		}//查询结果
    		int count = 0;
    		for(NTbkItem item:results) {
    			//String itemKey = itemMap.get(item.getItemUrl());
    			String itemKey = itemMap.get(""+item.getNumIid());
    			if(itemKey != null && itemKey.trim().length()>1) {//避免出现地址不匹配更新错误
    				logger.info("try to update item.[itemKey]"+itemKey+"[url]"+item.getItemUrl());
    				BaseDocument doc = new BaseDocument();
    				Map<String,Object> syncStatus = new HashMap<String,Object>();
    				syncStatus.put("sync", true);
    				List<String> categories = new ArrayList<String>();
    				categories.add(item.getCatLeafName());
    				categories.add(item.getCatName());
    				
    				doc.setKey(itemKey);
    				doc.getProperties().put("status", syncStatus);
    				doc.getProperties().put("category", categories);
    				doc.getProperties().put("logo", item.getPictUrl());
    				
    				arangoClient.update("my_stuff", itemKey, doc);
    				count ++;
    			}else {
    				logger.warn("Cannot update arangodb item while url is wrong.[id]"+item.getNumIid()+"[url]"+item.getItemUrl());
    			}
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
		msg.put("title", "导购数据同步结果");
		msg.put("task", "淘宝类目数据同步");
		msg.put("time", fmt.format(new Date()));
		msg.put("remark", "期望同步数量："+itemList.size()
				+ "\n实际同步数量："+results.size()
				+ "\n差异数："+(itemList.size()-results.size()));
		msg.put("color", itemList.size()-results.size()==0?"#00FF00":"#FF0000");

		result = HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/data-sync-notify", 
				msg,header);
		//3，更新通知状态
		if(result.getBooleanValue("status")) {
			logger.info("clearing notification msg sent.[msgId] " + result.getString("msgId"));
		}
        logger.info("Clearing Notification job executed.[msg]" + msg);
    		
    }

}
