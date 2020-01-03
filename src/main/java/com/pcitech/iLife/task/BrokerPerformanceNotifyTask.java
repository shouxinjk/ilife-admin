package com.pcitech.iLife.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import com.pcitech.iLife.modules.mod.entity.BrokerPerformance;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.mod.service.BrokerPerformanceService;
import com.pcitech.iLife.util.HttpClientHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 查询达人当日、当周、当月绩效汇总，并发送通知给指定达人。
 */
public class BrokerPerformanceNotifyTask{
    private static Logger logger = LoggerFactory.getLogger(BrokerPerformanceNotifyTask.class);
    
    @Autowired
    BrokerPerformanceService performanceService;

    public BrokerPerformanceNotifyTask() {
    }

    /**
	 * 查询新成交的订单，并发送通知给指定达人。操作步骤：
	 * 1，查询待计算绩效任务：达人ID、达人OpenId、任务类型（daily、weekly、monthly等）、任务状态及通知状态
	 * 2，循环根据所有达人查询：分享量、浏览量、成交量、订单数。并更新对应记录数据及任务状态为true
	 * 3，通过httpclient发送通知给达人并更新对应记录的notification状态为true
     */
    public void execute(String type) throws JobExecutionException {
    		//从配置中获取任务类型
    		logger.info("Performance Notification job start. [type]"+type + new Date());
    		
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd");
    		SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    		
    		Map<String,Object> params = new HashMap<String,Object>();
    		params.put("type", type);
    		params.put("startDate", new Date());
    		params.put("endDate", new Date());
    		
    		//1，按TaskType查询指定类型的待处理任务
    		List<BrokerPerformance> tasks = performanceService.findListByTaskType(type);
    		//2，逐条任务进行计算：根据开始时间、结束时间查询汇总效果数据
    	    //准备发起HTTP请求：设置data server Authorization
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
    		for(BrokerPerformance task:tasks) {
    			if(task.getBroker() == null || task.getBroker().getOpenid()==null || task.getBroker().getOpenid().trim().length()==0) {
    				logger.error("Cannot send performance notification to broker without openid.[json]"+task);
    				continue;
    			}
    			//计算起止时间：
    			if("daily".equalsIgnoreCase(task.getTaskType())) {
    				//如果是daily任务：每天晚上7点开始汇总前一天晚上7点到今天晚上7点的数据
    				String str = fmt1.format(task.getCreateDate());
    				str += " 19:00:00";
    				Date endDate = task.getCreateDate();
    				try {endDate = fmt2.parse(str);}catch(Exception ex) {logger.error("parse date error.",ex);}
	    			cal.setTime(endDate);
	        		params.put("endDate", cal.getTime());
	        		cal.add(Calendar.DATE, -1);
	        		params.put("startDate", cal.getTime());
    			}else if("weekly".equalsIgnoreCase(task.getTaskType())) {
    				//如果是weekly任务：每周六上午8点开始汇总上周六0点到本周六0点的数据
    				String str = fmt1.format(task.getCreateDate());
    				str += " 00:00:00";
    				Date endDate = task.getCreateDate();
    				try {endDate = fmt2.parse(str);}catch(Exception ex) {logger.error("parse date error.",ex);}
	    			cal.setTime(endDate);
	        		params.put("endDate", cal.getTime());
	        		cal.add(Calendar.DATE, -7);
	        		params.put("startDate", cal.getTime());
    			}else if("monthly".equalsIgnoreCase(task.getTaskType())) {
    				//如果是monthly任务：每月1日上午9点开始汇总上月1号0点到本月1号0点的数据
    				String str = fmt1.format(task.getCreateDate());
    				str += " 00:00:00";
    				Date endDate = task.getCreateDate();
    				try {endDate = fmt2.parse(str);}catch(Exception ex) {logger.error("parse date error.",ex);}
	    			cal.setTime(endDate);
	        		params.put("endDate", cal.getTime());
	        		cal.add(Calendar.MONTH, -1);
	        		params.put("startDate", cal.getTime());
    			}
    			if("pending".equalsIgnoreCase(task.getStatusCalc())) {//计算汇总数据
    				Map<String,Object> sum = performanceService.getPerformanceCalcResult(params);
    				task.setAmountBuy(Double.parseDouble(sum.get("sumBuy").toString()));
    				task.setAmountCredit(Double.parseDouble(sum.get("sumCredit").toString()));
    				task.setAmountOrder(Double.parseDouble(sum.get("sumOrder").toString()));
    				task.setAmountTeam(Double.parseDouble(sum.get("sumTeam").toString()));
    				task.setCountOrder(Integer.parseInt(sum.get("cntOrders").toString()));
    				task.setCountTeam(Integer.parseInt(sum.get("cntTeamMembers").toString()));
    				task.setCountShare(Integer.parseInt(sum.get("cntShares").toString()));
    				task.setCountView(Integer.parseInt(sum.get("cntViews").toString()));
    				task.setCountBuy(Integer.parseInt(sum.get("cntBuys").toString()));
    				task.setStatusCalc("done");
    				task.setDateCalc(new Date());
    				task.setUpdateDate(new Date());
    				performanceService.save(task);
    			}
    			if("pending".equalsIgnoreCase(task.getStatusNotify())) {//发送通知
        			JSONObject msg = new JSONObject();
        			msg.put("views", task.getCountView());
        			msg.put("buys", task.getCountBuy());
        			msg.put("shares", task.getCountShare());
        			msg.put("orders", task.getCountOrder());
        			msg.put("members", task.getCountTeam());
        			msg.put("amountBuy", task.getAmountBuy());
        			msg.put("amountCreidt", task.getAmountCredit());
        			msg.put("amountOrder", task.getAmountOrder());
        			msg.put("amountTeam", task.getAmountTeam());
        			msg.put("taskType", task.getTaskType());
        			msg.put("brokerOpenid", task.getBroker().getOpenid());
        			msg.put("brokerName", task.getBroker().getName());
        			result = HttpClientHelper.getInstance().post(
        					Global.getConfig("wechat.templateMessenge")+"/performance-notify", 
        					msg,header);
        			//3，更新通知状态
        			if(result.getBooleanValue("status")) {
        				logger.info("perfromance notification msg sent.[msgId] " + result.getString("msgId"));
	        			task.setStatusNotify("done");
	        			task.setDateNotify(new Date());
	        			task.setUpdateDate(new Date());
	        			performanceService.save(task);
        			}
    			}
    	        logger.info("Performace Calc & Notification job executed.[type]"+type+"[msg]" +task);
    		}
    }

}
