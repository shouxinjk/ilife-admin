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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.entity.BrokerPerformance;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.modules.mod.service.BrokerPerformanceService;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 流量主互阅、互关开车任务报告自动生成
 * 
 * 每到15分钟自动汇总。其实就是发个链接而已
 * 
 */
@Service
public class PublisherGroupingReportTask{
    private static Logger logger = LoggerFactory.getLogger(PublisherGroupingReportTask.class);
    
    @Autowired
    DictService dictService;

    public PublisherGroupingReportTask() {
    }

    /**
	 * 自动生成任务，并自动发送企业微信
	 * 
	 * {
            "msgtype": "news",
            "news": {
               "articles" : [
                   {
                       "title" : "xxx群 9点班车 报告",
                       "description" : "点击查看收益",
                       "url" : "https://www.biglistoflittlethings.com/ilife-web-wx/publisher/report-grouping.html?code="+code,
                       "picurl" : "班车logo图片"
                   }
                ]
            }
        }
	 * 
     */
    public void execute() throws JobExecutionException {
	   	    //准备发起HTTP请求：设置data server Authorization
		    Map<String,String> header = new HashMap<String,String>();
		    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    		
		    //用于获取指定长度的字符串，生成固定的随机码
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		
    		//当前固定为整点发车，发报告时间为15分，截止时间为1小时
    		Calendar cal = Calendar.getInstance();
    		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
    		cal.set(Calendar.MINUTE, 0);//整点开始
    		cal.set(Calendar.SECOND, 0);
    		Date timeFrom = cal.getTime();
    		cal.add(Calendar.HOUR, 1);//截止时间为一小时后
    		Date timeTo = cal.getTime();
    		
    		String seed = fmt.format(timeFrom);
    		String code = Util.get6bitCodeRandom(seed);//需要固定seed，与发出开车通知时保持一致
    		
    		//查询得到所有微信群
    		Dict dict = new Dict();
    		dict.setType("wx_group");
    		List<Dict> wxGroups = dictService.findList(dict);
    		
			logger.debug("Try to generate grouping tasks.");
    		for(Dict wxGroup:wxGroups) {
    			//组装模板消息
    			JSONObject json = new JSONObject();
    			json.put("msgtype", "news");
    			JSONObject jsonArticle = new JSONObject();
    			jsonArticle.put("title" , currentHour+"点班车 报告");
    			jsonArticle.put("description" , wxGroup.getLabel()+"专属，点击查看明细，查缺补漏");
    			jsonArticle.put("url" , "https://www.biglistoflittlethings.com/ilife-web-wx/publisher/report-grouping.html?code="+code);
    			jsonArticle.put("picurl" , "https://www.biglistoflittlethings.com/static/logo/grouping/report.png");
    			JSONArray jsonArticles = new JSONArray();
    			jsonArticles.add(jsonArticle);
    			JSONObject jsonNews = new JSONObject();
    			jsonNews.put("articles", jsonArticles);
    			json.put("news", jsonNews);
    			
    			logger.debug("try to send cp msg. ",json);
    			
    			//发送到企业微信
    			HttpClientHelper.getInstance().post(
    					Global.getConfig("wework.templateMessenge")+"/notify-cp-company-broker", 
    					json,header);
    		}
    }

}
