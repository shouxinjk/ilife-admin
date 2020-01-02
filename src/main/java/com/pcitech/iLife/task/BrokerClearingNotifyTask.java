package com.pcitech.iLife.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.HttpClientHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 查询新成交的订单，并发送通知给指定达人。
 */
public class BrokerClearingNotifyTask implements Job {
    private static Logger logger = LoggerFactory.getLogger(BrokerClearingNotifyTask.class);
    
    @Autowired
    ClearingService clearingService;

    public BrokerClearingNotifyTask() {
    }

    /**
	 * 查询新成交的订单的清分记录，并发送通知给指定达人，包括parent、grandpa、broker等。操作步骤：
	 * 1，查询notification=false的任务，包含达人openid、商品名称、佣金金额、订单时间、佣金状态
	 * 2，通过httpclient发送通知给达人
	 * 3，更新notification状态为true
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
    		logger.info("Clearing Notification job start. " + new Date());
    		//1，查询所有待通知清分记录
    		List<Map<String,Object>> items = clearingService.findPendingNotifyList();
    		//2，逐条发送通知：不处理结果，仅发送即可
    	    //准备发起HTTP请求：设置data server Authorization
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
    		for(Map<String,Object> item:items) {
    			if(item.get("broker_openid")==null || item.get("broker_openid").toString().length()==0) {
    				logger.error("Cannot send clearing notification to broker without openid.[json]"+item);
    				continue;
    			}
    			JSONObject msg = new JSONObject();
    			msg.put("item", item.get("item"));
    			msg.put("orderTime", item.get("order_time"));
    			msg.put("amountProfit", item.get("order_time"));
    			msg.put("status", item.get("status_clear"));
    			msg.put("brokerName", item.get("broker_name"));
    			msg.put("brokerOpenid", item.get("broker_openid"));
    			msg.put("beneficiary", item.get("beneficiary"));
    			msg.put("platform", item.get("platform"));
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
    	        logger.info("Clearing Notification job executed.[msg]" + msg+"\n[next fire]"+context.getNextFireTime());
    		}
    }

}
