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
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.BoardService;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.HttpClientHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 查询新增加的Board并发送给所有达人
 * 查询条件：最后修改时间为昨天
 */
public class NewBoardBroadcast {
    private static Logger logger = LoggerFactory.getLogger(NewBoardBroadcast.class);
    
    @Autowired
    BrokerService brokerService;
    
    @Autowired
    BoardService boardService;
    
    public NewBoardBroadcast() {
    }

    /**
	 * 操作步骤：
	 * 1，查询所有最后更新时间为昨天的board
	 * 2，查询所有达人
	 * 3，发送清单通知给达人
     */
    public void execute() throws JobExecutionException {
    		logger.info("New board broadcast job start. " + new Date());
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    		
    		Calendar  cal  =   Calendar.getInstance();
    		cal.add(Calendar.DATE,  -1);
    		
    		Date startDate = new Date(cal.getTimeInMillis());
    		Date endDate = new Date();
    		
    		Map<String,Object> params = new HashMap<String,Object>();
    		params.put("startDate", startDate);
    		params.put("endDate", endDate);
    		params.put("offset", 0);
    		params.put("size", 5);//限制为5条
    		
    		//1，查询所有昨日更新board
    		List<Board> items = boardService.findListByDate(params);
    		if(items == null || items.size()==0) {//如果没有新的清单则跳过
    			sendNoBoardWaring();
    			return;
    		}
    		
    		String titleStr = "";
    		int i=1;
    		for(Board item:items) {
    			titleStr += "\n"+i+" "+item.getTitle();
    			i++;
    		}
    		//2，查询所有达人
    		List<Broker> brokers = brokerService.findList(new Broker());
    		
    	    //准备发起HTTP请求：设置data server Authorization
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
    		for(Broker broker:brokers) {
    			if(broker.getOpenid()==null || broker.getOpenid().toString().length()==0) {
    				logger.error("Cannot send board list to broker without openid.[json]"+broker);
    				continue;
    			}

    			JSONObject msg = new JSONObject();
    			msg.put("openid", broker.getOpenid());
    			msg.put("title", "发现新的商品清单，图文并茂，非常适合批量推荐。");
    			msg.put("task", "点击查看详情");
    			msg.put("time", fmt.format(new Date()));
    			msg.put("remark", titleStr);
    	
    			result = HttpClientHelper.getInstance().post(
    					Global.getConfig("wechat.templateMessenge")+"/data-sync-notify", 
    					msg,header);
    			//3，更新通知状态
    			if(result.getBooleanValue("status")) {
    				logger.info("new board list msg sent.[msgId] " + result.getString("msgId"));
    			}
    	        logger.info("board list broadcast job executed.[msg]" + msg);
    		}
    }
    
    private void sendNoBoardWaring() {
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", "o8HmJ1EdIUR8iZRwaq1T7D_nPIYc");//固定发送
		msg.put("title", "清单推送任务结果");
		msg.put("task", "无清单");
		msg.put("time", fmt.format(new Date()));
		msg.put("remark", "请确保每天有新增清单");
		msg.put("color", "#FF0000");
	
		result = HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/data-sync-notify", 
				msg,header);    	
		
		logger.info("board list broadcast job executed.[msg]" + msg);
    }

}
