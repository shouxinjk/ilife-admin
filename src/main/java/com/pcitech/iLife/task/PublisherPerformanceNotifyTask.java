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
    		
    		SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		
    		int days = 1;//当前数据较少，采用一年的数据汇总。1;//查询当天的效益。执行时间减去24小时
    		List<String> pendingPublisherIds = brokerService.findNotifyCandidatePublisherIdList(days);
    		
    		//查询得到排行数据
    		Map<String,Object> countParams = new HashMap<String,Object>();
    		countParams.put("days", days);//设置时间跨度
    		countParams.put("total", 100);//TODO 注意这里是固定设置。设置返回条数：默认取100条，需要同时完成额外奖励
    		List<Map<String,Object>> rankList = brokerService.countPublisherReads(countParams);
    		
    		boolean test=false;
    		//先完成奖励：注意此处不严谨，仅根据排行前100进行奖励
    		Map<String,Integer> rewardMap = new HashMap<String,Integer>();//记录当前接收通知达人是否有奖励
    		if(!test) {
				for(Map<String,Object> rank:rankList) {
					int reads = Integer.parseInt(rank.get("total").toString());
					Broker broker = brokerService.get(rank.get("id").toString());
					if(reads>=100) {
						broker.setPoints(broker.getPoints()+30);
						rewardMap.put(broker.getId(), 30);
					}else if(reads>=80) {
						broker.setPoints(broker.getPoints()+20);
						rewardMap.put(broker.getId(), 20);
					}else if(reads>=50) {
						broker.setPoints(broker.getPoints()+10);
						rewardMap.put(broker.getId(), 10);
					}else {
						break;//因为是按倒序排列，遇到低于50的就可以直接收工了
					}
					brokerService.save(broker);
				}
    		}
    		
			logger.debug("Try to send publisher stat.[total]"+pendingPublisherIds.size());
    		Map<String,Object> params = new HashMap<String,Object>();
    		params.put("days", days);//设置时间跨度
    		for(String publisherId:pendingPublisherIds) {
    			params.put("brokerId", publisherId);//设置指定达人
    			Map<String,Object> stat = brokerService.findNotifyPublisherStat(params);
    			//仅测试使用
    			//**
    			if(!"o8HmJ1EdIUR8iZRwaq1T7D_nPIYc".equalsIgnoreCase(stat.get("openid").toString()) 
    					&& !"o8HmJ1ItjXilTlFtJNO25-CAQbbg".equalsIgnoreCase(stat.get("openid").toString()))
    				continue;
    			//**/
    			//组装模板消息
    			JSONObject json = new JSONObject();
    			json.put("openid", stat.get("openid").toString());
    			json.put("title", "流量主收益报告");
    			json.put("timestamp", fmt2.format(new Date()));
    			json.put("points", stat.get("points")+" 阅豆");
    			String remark = "";
    			remark+="累计增阅："+stat.get("readsCount");
    			remark+="\n累计增粉："+stat.get("subscribesCount");
    			/**
    			try {
    				int subscriberCount = Integer.parseInt(""+stat.get("subscribesCount"));
    				if(subscriberCount>0) {
    					remark+="\n累计增粉："+stat.get("subscribesCount");
    				}
    			}catch(Exception ex) {
    				//do nothing
    			}
    			//**/
    			
    			remark+="\n文章总计："+stat.get("articlesCount"); 
    			remark+="\n公众号总计："+stat.get("accountsCount");
    			/**
    			try {
    				int accountsCount = Integer.parseInt(""+stat.get("accountsCount"));
    				if(accountsCount>0) {
    					remark+="\n公号总计："+stat.get("accountsCount");
    				}
    			}catch(Exception ex) {
    				//do nothing
    			}    			
    			//**/
    			
    			//循环装载排行数据：如果数据量过低则跳过，总共显示前三名
    			//TODO：当前阅读数据量太少，排行榜可能会打击积极参与的人，暂时不发布
    			/**
    			remark += "\n\n昨日阅读达人：";
    			int rankNumber  = 1;
    			for(Map<String,Object> rank:rankList) {
    				if(rank.get("id").toString().equalsIgnoreCase(publisherId) && Integer.parseInt(rank.get("total").toString())<50) {//以奖励的最低线过滤
    					continue;
    				}
    				remark += "\nTop "+rankNumber+"："+rank.get("nickname");
    				if(rank.get("id").toString().equalsIgnoreCase(publisherId) && rewardMap.get("publisherId")!=null && rewardMap.get("publisherId")>0) {//以奖励的最低线过滤
    					remark += "(+"+rewardMap.get("publisherId")+"豆)";
    				}
    				rankNumber++;
    				if(rankNumber>3)//仅显示3条
    					break;
    			}
    			//**/
    			
    			remark+="\n\n每天阅读额外奖励："
    					+ "\n超过100：+30豆"+(rewardMap.get("publisherId")!=null && rewardMap.get("publisherId")==30?" * 已奖励":"")
    					+ "\n超过80：+20豆"+(rewardMap.get("publisherId")!=null && rewardMap.get("publisherId")==20?" * 已奖励":"")
    					+ "\n超过50：+10豆"+(rewardMap.get("publisherId")!=null && rewardMap.get("publisherId")==10?" * 已奖励":"")
    					+ "\n\n阅豆越多文章排名越靠前，阅豆少于0文章或公众号将不在列表中展示哦。点击进入获取阅豆~~";
    			
    			
//    			remark+="\n\n阅读和关注都将奖励阅豆，阅豆越多文章排名越靠前，阅豆少于0文章或公众号将不在列表中展示哦~~\n\n点击进入获取阅豆~~";
    			json.put("remark", remark);
    			//发送
    			HttpClientHelper.getInstance().post(
    					Global.getConfig("wechat.templateMessenge")+"/notify-mp-publisher", 
    					json,header);
    		}
    }

}
