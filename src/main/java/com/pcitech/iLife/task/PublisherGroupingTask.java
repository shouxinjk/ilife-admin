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
 * 流量主互阅、互关开车任务自动生成
 * 如：生成整点互阅开车。每到整点根据配置的微信群生成开车互阅链接，并发送到企业微信群。由运营人员转发后发起互阅互关
 * 
 */
@Service
public class PublisherGroupingTask{
    private static Logger logger = LoggerFactory.getLogger(PublisherGroupingTask.class);
    
    @Autowired
    DictService dictService;

    public PublisherGroupingTask() {
    }

    /**
	 * 自动生成任务，并自动发送企业微信
	 * 
	 * {
            "msgtype": "news",
            "news": {
               "articles" : [
                   {
                       "title" : "xxx群 9点班车",
                       "description" : "9点发车，发文章并完成阅读，9点15发报告",
                       "url" : "https://www.biglistoflittlethings.com/ilife-web-wx/publisher/articles-grouping.html?code="+code+"&timeTo="+timeTo,
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
    		
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");//("yyyy-MM-dd HH:mm");//默认每天发一班
    		
    		//当前固定为整点发车，发报告时间为15分，截止时间为1小时
    		Calendar cal = Calendar.getInstance();
    		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
    		cal.set(Calendar.MINUTE, 0);//整点开始
    		cal.set(Calendar.SECOND, 0);
    		Date timeFrom = cal.getTime();
    		cal.add(Calendar.HOUR, 24);//截止时间为一小时后；每天一班为24小时
    		Date timeTo = cal.getTime();
    		
    		String seed = fmt.format(timeFrom);
    		String code = Util.get6bitCodeRandom(seed);//需要固定seed，在生成报告时能够获取
    		
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
//    			jsonArticle.put("title" , currentHour+"点班车，15分钟发报告");
//    			jsonArticle.put("description" , wxGroup.getLabel()+"专属，发文上车，并保持10秒有效阅读");
//    			jsonArticle.put("url" , "https://www.biglistoflittlethings.com/ilife-web-wx/publisher/articles-grouping.html?code="+code+"&timeFrom="+timeFrom.getTime()+"&timeTo="+timeTo.getTime());
//    			jsonArticle.put("picurl" , "https://www.biglistoflittlethings.com/static/logo/grouping/default.png");
//    			
    			if(wxGroup.getValue().indexOf("subscribe")>=0) {
        			jsonArticle.put("title" , seed+"互关班车，每日发车，整点发报告");
        			jsonArticle.put("description" , wxGroup.getLabel()+"专属，发公众号上车，关注确认");
        			jsonArticle.put("url" , "https://www.biglistoflittlethings.com/ilife-web-wx/publisher/accounts-grouping.html?code="+code+"&timeFrom="+timeFrom.getTime()+"&timeTo="+timeTo.getTime());
        			jsonArticle.put("picurl" , "https://www.biglistoflittlethings.com/static/logo/grouping/subscribe.png");
    			}else if(wxGroup.getValue().indexOf("read")>=0) {
        			jsonArticle.put("title" , seed+"互阅班车，每天发车，整点发报告");
        			jsonArticle.put("description" , wxGroup.getLabel()+"专属，发文上车，需要保持10秒有效阅读");
        			jsonArticle.put("url" , "https://www.biglistoflittlethings.com/ilife-web-wx/publisher/articles-grouping.html?code="+code+"&timeFrom="+timeFrom.getTime()+"&timeTo="+timeTo.getTime());
        			jsonArticle.put("picurl" , "https://www.biglistoflittlethings.com/static/logo/grouping/default.png");
    			}else {
    				logger.debug("wrong wechat group key. must be one of subscribe or read "+wxGroup.getId());
    			}
    			
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
