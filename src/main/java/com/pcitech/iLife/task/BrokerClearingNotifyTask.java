package com.pcitech.iLife.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.HttpClientHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 查询新成交的订单，并发送通知给指定达人。
 */
public class BrokerClearingNotifyTask {
    private static Logger logger = LoggerFactory.getLogger(BrokerClearingNotifyTask.class);

    @Autowired
    ClearingService clearingService;
    @Autowired
    DictService dictService;

    public BrokerClearingNotifyTask() {
    }

    /**
	 * 查询新成交的订单的清分记录，并发送通知给指定达人，包括parent、grandpa、broker等。操作步骤：
	 * 1，查询notification=false的任务，包含达人openid、商品名称、佣金金额、订单时间、佣金状态
	 * 2，通过httpclient发送通知给达人
	 * 3，更新notification状态为true
     */
    public void execute() throws JobExecutionException {
    		logger.info("Clearing Notification job start. " + new Date());
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    		//0，加载所有平台信息
    		Map<String,String> platforms = loadPlatforms();
    		//1，查询所有待通知清分记录
    		List<Map<String,Object>> items = clearingService.findPendingNotifyList();
    		//2，逐条发送通知：不处理结果，仅发送即可
    	    //准备发起HTTP请求：设置data server Authorization
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
    		for(Map<String,Object> item:items) {
    			if(item.get("broker_openid")==null || item.get("broker_openid").toString().length()==0
    					 || item.get("broker_openid").toString().length()<20) { //注意特殊用户如system其openid为虚拟
    				logger.error("Cannot send clearing notification to broker without openid.[json]"+item);
    				
    				//直接更新通知状态，更新清分记录的通知状态，避免重复发送
    				Clearing clearing = clearingService.get(item.get("id").toString());
    				clearing.setStatusNotify("done");
    				clearing.setUpdateDate(new Date());
    				clearingService.save(clearing);
    				
    				continue;
    			}
    			//Date orderTime = new Date(Long.parseLong(item.get("order_time").toString()));
    			
    			JSONObject msg = new JSONObject();
    			msg.put("item", item.get("item"));
    			try {
    				Date orderTime = fmt.parse(item.get("order_time").toString());
    				msg.put("orderTime",fmt.format(orderTime) );
    			}catch(Exception ex) {
    				msg.put("orderTime", fmt.format(new Date()));
    				logger.error("Cannot parse order time.[msg]"+item,ex);
    			}
    			msg.put("amountOrder", item.get("amount_order"));
    			msg.put("amountProfit", item.get("amount_profit"));
    			msg.put("status", item.get("status_clear"));
    			msg.put("brokerName", item.get("broker_name"));
    			msg.put("brokerOpenid", item.get("broker_openid"));
    			msg.put("beneficiary", item.get("beneficiary"));
    			msg.put("platform", platforms.get(item.get("platform")));//TODO 从字典加载平台名称
    			msg.put("seller", item.get("seller"));
    			result = HttpClientHelper.getInstance().post(
    					Global.getConfig("wechat.templateMessenge")+"/clearing-notify", 
    					msg,header);
    			//3，更新通知状态
    			if(result.getBooleanValue("status")) {
    				logger.info("clearing notification msg sent.[msgId] " + result.getString("msgId"));
    				//更新清分记录的通知状态
    				Clearing clearing = clearingService.get(item.get("id").toString());
    				clearing.setStatusNotify("done");
    				clearing.setUpdateDate(new Date());
    				clearingService.save(clearing);
    			}
    	        logger.debug("Clearing Notification job executed.[msg]" + msg);
    		}
    }
    
    private Map<String,String> loadPlatforms() {
		//从字典加载电商平台信息
    	Map<String,String> platformMap = Maps.newHashMap();
		Dict dict = new Dict();
		dict.setType("platform");
		List<Dict> platforms = dictService.findList(dict);
		for(Dict platform:platforms) {
			platformMap.put(platform.getValue(), platform.getLabel());
		}
		return platformMap;
    }

}
