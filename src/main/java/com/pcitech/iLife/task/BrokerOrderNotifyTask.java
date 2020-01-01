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
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.HttpClientHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 查询新成交的订单，并发送通知给指定达人。
 */
public class BrokerOrderNotifyTask implements Job {
    private static Logger logger = LoggerFactory.getLogger(BrokerOrderNotifyTask.class);
    
    @Autowired
    OrderService orderService;

    public BrokerOrderNotifyTask() {
    }

    /**
	 * 查询新成交的订单，并发送通知给指定达人。操作步骤：
	 * 1，查询notification=false的任务，包含达人openid、商品名称、佣金金额、订单时间、佣金状态
	 * 2，通过httpclient发送通知给达人
	 * 3，更新订单的notification状态为true
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
    		logger.warn("Order Notification job start. " + new Date());
    		//1，查询所有待通知订单
    		List<Order> orders = orderService.findPendingNofityList();
    		//2，逐条发送通知：不处理结果，仅发送即可
    	    //准备发起HTTP请求：设置data server Authorization
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
    		for(Order order:orders) {
    			JSONObject msg = new JSONObject();
    			msg.put("item", order.getItem());
    			msg.put("orderTime", order.getOrderTime());
    			msg.put("commissionEstimate", order.getCommissionEstimate());
    			msg.put("status", order.getStatus());
    			msg.put("brokerName", order.getBroker().getName());
    			msg.put("brokerOpenid", order.getBroker().getOpenid());
    			result = HttpClientHelper.getInstance().post(
    					Global.getConfig("wechat.templateMessenge")+"/order-notify", 
    					msg,header);
    			//3，更新通知状态
    			order.setNotification("1");
    			order.setUpdateDate(new Date());
    			orderService.save(order);
    	        logger.info("Order Notification job executed: " + msg);
    		}
    }

}
