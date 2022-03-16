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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.CpsLinkHelper;
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
自动更新固定组装的CPS链接，通过查询原始链接拼接为导购链接。
查询条件：doc.link.web == doc.link.web2 
查询得到pending item后需要更新doc.link.web2,doc.link.wap2,同时设置doc.status.index = "pending"

DHC: 
let prefix = "?ad_cps=70130084&"
for doc in my_stuff filter doc.source=="dhc" update doc with {"link":{"web2":REGEX_REPLACE(doc.link.web,"\\?",prefix),"wap2":REGEX_REPLACE(doc.link.wap,"\\?",prefix)}} in my_stuff

同程：
let prefix = "?refid=29240070&"
for doc in my_stuff filter doc.source=="tongcheng" update doc with {"link":{"web2":REGEX_REPLACE(doc.link.web,"\\?",prefix),"wap2":REGEX_REPLACE(doc.link.wap,"\\?",prefix)}} in my_stuff

当当：
let prefix = "http://union.dangdang.com/transfer.php?from=P-316079&ad_type=10&sys_id=1&backurl="
for doc in my_stuff filter doc.source=="dangdang" update doc with {"link":{"web2":CONCAT(prefix,doc.link.web),"wap2":CONCAT(prefix,doc.link.wap)}} in my_stuff

考拉：
let prefix = "https://cps.kaola.com/cps/zhuankeLogin?unionId=zhuanke_701412896&tc1=default&tc2=default&showWapBanner=0&targetUrl="
for doc in my_stuff filter doc.source=="kaola" update doc with {"link":{"web2":CONCAT(prefix,doc.link.web),"wap2":CONCAT(prefix,doc.link.wap)}} in my_stuff

国美：
let prefix = "?cmpid=cps_2552_25169_default&sid=2552&wid=25169&feedback=default"
for doc in my_stuff filter doc.source=="gome" update doc with {"link":{"web2":CONCAT(doc.link.web,prefix),"wap2":CONCAT(doc.link.wap,prefix)}} in my_stuff

亚马逊中国：
let prefix = "?tag=ilife-23"
for doc in my_stuff filter doc.source=="amazon" update doc with {"link":{"web2":CONCAT(doc.link.web,prefix),"wap2":CONCAT(doc.link.wap,prefix)}} in my_stuff

携程：
let suffix = "?TypeID=2&AllianceID=8045&sid=1611278&ouid=&app=0101X00&"
for doc in my_stuff filter doc.source=="ctrip" update doc with {"link":{"web2":CONCAT(doc.link.web,suffix),"wap2":CONCAT(doc.link.wap,suffix)}} in my_stuff

 */
@Service
public class CpsLinkSync {
    private static Logger logger = LoggerFactory.getLogger(CpsLinkSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
	@Autowired
	CpsLinkHelper cpsLinkHelper;
    //待处理的来源列表
    String[] source = {"dhc","tongcheng","dangdang"/*,"kaola"*/,"gome","amazon","ctrip"};
    String[] sourceName = {"DHC","同程","当当"/*,"考拉"*/,"国美","亚马逊","携程"};
    int numberPerTask = 100;//每次任务同步处理条数： 每分钟100条
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    Map<String,Integer> processedMap = null;

    public CpsLinkSync() {
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
		
		//准备接收链接
		Map<String,String> links = new HashMap<String,String>();
		String  itemSource = item.getProperties().get("source").toString();
		String url = item.getProperties().get("web").toString();
		Map<String, Object> result = cpsLinkHelper.getCpsLink("system", itemSource, url, null, false);//通过SDK调用的URL不包括在内，如：pdd、jd、suning、kaola
		if(Boolean.parseBoolean(result.get("status").toString())){//获得CPS链接
			links.put("web2", result.get("link").toString());
			links.put("wap2", result.get("link").toString()//移动端链接采用完全相同的方法得到
					.replace(item.getProperties().get("web").toString(), item.getProperties().get("wap").toString()));
			doc.getProperties().put("link", links);
		}else {
			logger.error("failed convert cps link.[source]"+item.getProperties().get("source")+" [url]"+item.getProperties().get("web"));
		}
		//TODO：注意：管理端提供了CPS链接模板管理功能，但此处直接hard code了，导致CPS链接模板失效。需要调整为根据CPS链接模板生成
		//根据source类型分别处理链接
		/*
		
		if("dhc".equalsIgnoreCase(itemSource)) {
			String prefix = "?ad_cps=70130084&";
			
			if(item.getProperties().get("web").toString().indexOf("?")>0)
				links.put("web2", item.getProperties().get("web").toString().replace("?", prefix));
			else
				links.put("web2", item.getProperties().get("web")+ prefix);
			
			if(item.getProperties().get("wap").toString().indexOf("?")>0)
				links.put("wap2", item.getProperties().get("wap").toString().replace("?", prefix));
			else
				links.put("wap2", item.getProperties().get("wap")+ prefix);
			
			doc.getProperties().put("link", links);
		}else if("tongcheng".equalsIgnoreCase(itemSource)) {
			String prefix = "?refid=29240070&";
			Map<String,String> links = new HashMap<String,String>();
			if(item.getProperties().get("web").toString().indexOf("?")>0)
				links.put("web2", item.getProperties().get("web").toString().replace("?", prefix));
			else
				links.put("web2", item.getProperties().get("web")+ prefix);
			
			if(item.getProperties().get("wap").toString().indexOf("?")>0)
				links.put("wap2", item.getProperties().get("wap").toString().replace("?", prefix));
			else
				links.put("wap2", item.getProperties().get("wap")+ prefix);
			
			doc.getProperties().put("link", links);
		}else if("dangdang".equalsIgnoreCase(itemSource)) {
			String prefix = "http://union.dangdang.com/transfer.php?from=P-316079&ad_type=10&sys_id=1&backurl=";
			Map<String,String> links = new HashMap<String,String>();
			links.put("web2", prefix+item.getProperties().get("web"));
			links.put("wap2", prefix+item.getProperties().get("wap"));
			doc.getProperties().put("link", links);
		}else if("kaola".equalsIgnoreCase(itemSource)) {//考拉链接已经在sync过程中进行处理：此处需要去掉
			String prefix = "https://cps.kaola.com/cps/zhuankeLogin?unionId=zhuanke_701412896&tc1=system&tc2=system&showWapBanner=0&targetUrl=";
			Map<String,String> links = new HashMap<String,String>();
			links.put("web2", prefix+item.getProperties().get("web"));
			links.put("wap2", prefix+item.getProperties().get("wap"));
			doc.getProperties().put("link", links);
		}else if("gome".equalsIgnoreCase(itemSource)) {
			String prefix = "?cmpid=cps_2552_25169_default&sid=2552&wid=25169&feedback=default";
			Map<String,String> links = new HashMap<String,String>();
			links.put("web2", item.getProperties().get("web")+prefix);
			links.put("wap2", item.getProperties().get("wap")+prefix);
			doc.getProperties().put("link", links);
		}else if("amazon".equalsIgnoreCase(itemSource)) {
			String prefix = "?tag=ilife-23";
			Map<String,String> links = new HashMap<String,String>();
			links.put("web2", item.getProperties().get("web")+prefix);
			links.put("wap2", item.getProperties().get("wap")+prefix);
			doc.getProperties().put("link", links);
		}else if("ctrip".equalsIgnoreCase(itemSource)) {
			String prefix = "?TypeID=2&AllianceID=8045&sid=1611278&ouid=&app=0101X00&";
			Map<String,String> links = new HashMap<String,String>();
			links.put("web2", item.getProperties().get("web")+prefix);
			links.put("wap2", item.getProperties().get("wap")+prefix);
			doc.getProperties().put("link", links);
		}else {
			logger.error("Unknown source type.[source]"+item.getProperties().get("source"));
		}
		//**/
		processedMap.put(itemSource, processedMap.get(itemSource)==null?1:processedMap.get(itemSource)+1);
		//更新doc
		arangoClient.update("my_stuff", itemKey, doc);    	
		processedAmount++;
    }

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，查询Arangodb中(link.web2==null) || (link.web2 == link.web)的记录，限制30条。
	 * 2，通过拼多多API接口查询生成商品导购链接
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		//初始化处理结果记录器
    		processedMap = new HashMap<String,Integer>();
    		arangoClient = new ArangoDbClient(host,port,username,password,database);
        for(String s:source) {
        		processedMap.put(s, 0);
	    		logger.info("CpsLink sync job start. [source]" +s);
	    		//1，查询待处理商品记录 返回itemKey、商品ID、商品链接
	    		//for doc in my_stuff filter (doc.source == "pdd") and (doc.status==null or doc.status.sync==null) limit 30 return {itemKey:doc._key,id:split(doc.link.web,"id=")[1],link:doc.link.web}    		
	        String query = "for doc in my_stuff filter "
//	        		+ "doc.source in "+JSON.toJSONString(source)+" and " //slow query
	        		+ "doc.source == \""+s+"\" and "  
//	        		+ "(doc.link.web2==null or doc.link.web2 == doc.link.web) "
	        		+ "doc.status.sync==\"pending\"  "//根据状态查询待处理条目。注意：禁止使用null值查询，
//	        		+ "update doc with {status:{sync:\"ready\"}} in my_stuff "//查询时即更新状态
	        		+ "limit "+numberPerTask+" "//一个批次处理200条
	        		+ "return {itemKey:doc._key,source:doc.source,web:doc.link.web,wap:doc.link.wap}";
	        logger.error("try to query pending cpsLinkSync items.[query]"+query);
	        try {
	            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
	            totalAmount += items.size();
	            if(totalAmount ==0) {//如果没有了就提前收工
		            	logger.debug("没有待同步CPS链接的商品条目.[source]"+s);
//		            	arangoClient.close();//链接还是要关闭的
		            	continue;
		        }
	            for (BaseDocument item:items) {
	            		syncCpsLinks(item);//逐条查询CPS链接并更新ArangoDB doc
	            }
	        } catch (Exception e) {
	            logger.error("Failed to execute query.",e);
	        }
        }
		//完成后关闭arangoDbClient
		arangoClient.close();
		
		if(totalAmount == 0)//啥活都没干，发啥消息
			return;
		
		//组装通知信息
		StringBuffer remark = new StringBuffer();
		int index=0;
		remark.append("预期数量："+totalAmount);
		for(String s:source) {
			int numPerSource = processedMap.get(s);
			if(numPerSource>0)
				remark.append("\n"+sourceName[index++]+"："+numPerSource);
		}
		remark.append("\n数量差异："+(totalAmount-processedAmount));
		
		//发送处理结果到管理员
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", "o8HmJ1EdIUR8iZRwaq1T7D_nPIYc");//固定发送
		msg.put("title", "导购数据同步任务结果");
		msg.put("task", "CPS链接 已同步");
		msg.put("time", fmt.format(new Date()));
		msg.put("remark", remark);
		msg.put("color", totalAmount-processedAmount==0?"#FF0000":"#000000");

		logger.error("pending notification message.[body]",msg);
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
