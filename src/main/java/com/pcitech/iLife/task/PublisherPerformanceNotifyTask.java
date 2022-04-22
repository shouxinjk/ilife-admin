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
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.entity.BrokerPerformance;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.mod.service.BrokerPerformanceService;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.util.HttpClientHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 查询流量主当日、当周、当月绩效汇总，并发送通知给指定达人。
 */
@Service
public class PublisherPerformanceNotifyTask{
    private static Logger logger = LoggerFactory.getLogger(PublisherPerformanceNotifyTask.class);
    
    @Autowired
    BrokerService brokerService;

    public PublisherPerformanceNotifyTask() {
    }

    /**
	 * 查询所有今天产生关注、阅读的达人账号，逐个发送通知
	 * 注意，当前未作任何状态记录，查询后即完成发送
	 * 
     */
    public void execute() throws JobExecutionException {
	   	    //准备发起HTTP请求：设置data server Authorization
		    Map<String,String> header = new HashMap<String,String>();
		    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    		
    		SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    		
    		int days = 1;//查询当天的效益。执行时间减去24小时
    		List<String> pendingPublisherIds = brokerService.findNotifyCandidatePublisherIdList(days);
    		
			logger.debug("Try to send publisher stat.[total]"+pendingPublisherIds.size());
    		Map<String,Object> params = new HashMap<String,Object>();
    		params.put("days", days);//设置时间跨度
    		for(String publisherId:pendingPublisherIds) {
    			params.put("brokerId", publisherId);//设置指定达人
    			Map<String,Object> stat = brokerService.findNotifyPublisherStat(params);
    			//仅测试使用
    			if(!"o8HmJ1EdIUR8iZRwaq1T7D_nPIYc".equalsIgnoreCase(stat.get("openid").toString()) 
    					&& !"o8HmJ1ItjXilTlFtJNO25-CAQbbg".equalsIgnoreCase(stat.get("openid").toString()))
    				continue;
    			//组装模板消息
    			JSONObject json = new JSONObject();
    			json.put("openid", stat.get("openid").toString());
    			json.put("title", "流量主收益日报");
    			json.put("timestamp", fmt2.format(new Date()));
    			json.put("points", stat.get("points")+" 阅豆");
    			String remark = "";
    			remark+="今日增阅："+stat.get("readsCount");
    			remark+="\n今日增粉："+stat.get("subscribesCount");
    			remark+="\n文章总计："+stat.get("articlesCount");
    			remark+="\n公号总计："+stat.get("accountsCount");
    			remark+="\n\n阅读和关注都将奖励阅豆，阅豆越多文章排名越靠前，点击进入获取阅豆~~";
    			json.put("remark", remark);
    			//发送
    			HttpClientHelper.getInstance().post(
    					Global.getConfig("wechat.templateMessenge")+"/notify-mp-publisher", 
    					json,header);
    		}
    }

}
