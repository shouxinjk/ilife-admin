package com.pcitech.iLife.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.pcitech.iLife.modules.mod.entity.Broker;
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
public class PublisherPerformanceRankTask{
    private static Logger logger = LoggerFactory.getLogger(PublisherPerformanceRankTask.class);
    
    @Autowired
    BrokerService brokerService;

    public PublisherPerformanceRankTask() {
    }

    /**
	 * 查询所有今天产生关注、阅读的达人账号，并查询排行数据，然后逐个发送通知
	 * 注意，当前未作任何状态记录，查询后即完成发送
	 * 
     */
    public void execute() throws JobExecutionException {
	   	    //准备发起HTTP请求：设置data server Authorization
		    Map<String,String> header = new HashMap<String,String>();
		    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    		
    		SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		
    		int days = 1;//查询当天的效益。执行时间减去24小时
    		List<String> pendingPublisherIds = brokerService.findNotifyCandidatePublisherIdList(days);
    		
    		//查询得到排行数据
    		Map<String,Object> params = new HashMap<String,Object>();
    		params.put("days", days);//设置时间跨度
    		params.put("total", 100);//设置返回条数：默认取100条，需要同时完成额外奖励
    		List<Map<String,Object>> rankList = brokerService.countPublisherReads(params);
    		
    		boolean test=true;
    		//先完成奖励：注意此处不严谨，仅根据排行前100进行奖励
    		if(!test) {
				for(Map<String,Object> rank:rankList) {
					int reads = Integer.parseInt(rank.get("total").toString());
					Broker broker = brokerService.get(rank.get("id").toString());
					if(reads>100) {
						broker.setPoints(broker.getPoints()+30);	
					}else if(reads>80) {
						broker.setPoints(broker.getPoints()+20);
					}else if(reads>50) {
						broker.setPoints(broker.getPoints()+10);
					}else {
						break;//因为是按倒序排列，遇到低于50的就可以直接收工了
					}
					brokerService.save(broker);
				}
    		}
    		
			//发送通知
			logger.debug("Try to send publisher stat.[total]"+pendingPublisherIds.size());
    		for(String publisherId:pendingPublisherIds) {
    			Broker broker = brokerService.get(publisherId);
    			//仅测试使用
    			if(test) {
    				if(!"system".equalsIgnoreCase(publisherId) 
    					&& !"77276df7ae5c4058b3dfee29f43a3d3b".equalsIgnoreCase(publisherId))
    				continue;
    			}
    			
    			//循环装载排行数据：如果数据量过低则跳过，总共显示前三名
    			String remark = "昨日阅读达人：";
    			int rankNumber  = 1;
    			for(Map<String,Object> rank:rankList) {
    				if(rank.get("id").toString().equalsIgnoreCase(publisherId) && Integer.parseInt(rank.get("total").toString())<50) {//以奖励的最低线过滤
    					continue;
    				}
    				remark += "\nTop "+rankNumber+"："+rank.get("nickname")+" "+rank.get("total");
    				rankNumber++;
    				if(rankNumber>10)//仅显示3条
    					break;
    			}
    			remark+="\n\n每天阅读额外奖励："
    					+ "\n超过100：+30阅豆"
    					+ "\n超过80：+20阅豆"
    					+ "\n超过50：+10阅豆"
    					+ "\n\n阅豆越多文章排名越靠前，阅豆少于0文章或公众号将不在列表中展示哦。点击进入获取阅豆~~";
    			
    			//组装模板消息
    			JSONObject json = new JSONObject();
    			json.put("openid", broker.getOpenid());
    			json.put("nickname", broker.getNickname());
    			json.put("title", "昨日阅读排行榜");
    			json.put("timestamp", fmt2.format(new Date()));
    			json.put("points", broker.getPoints()+" 阅豆");
    			json.put("remark", remark);
    			if(broker.getPoints()<20) {
    				json.put("color", "#ff0000");//低于20则高亮显示
    			}else {
    				json.put("color", "#000000");
    			}
    			//发送
    			HttpClientHelper.getInstance().post(
    					Global.getConfig("wechat.templateMessenge")+"/notify-mp-publisher-rank", 
    					json,header);
    		}
    }

}
