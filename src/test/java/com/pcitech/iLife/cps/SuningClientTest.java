package com.pcitech.iLife.cps;

import static org.junit.Assert.*;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.task.PddItemSync;
import com.pcitech.iLife.task.SuningItemSync;
import com.pcitech.iLife.task.SuningItemsSearcher;
import com.pcitech.iLife.task.SuningOrderSync;
import com.pcitech.iLife.task.TaobaoItemSync;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse.OrderListGetResponse;
import com.suning.api.entity.netalliance.SelectrecommendcommodityQueryRequest;
import com.taobao.api.ApiException;

@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class SuningClientTest {
	
	@Autowired
	SuningHelper suningHelper;
	
	@Autowired
	SuningItemSync suningItemSync;
	@Autowired
	SuningOrderSync suningOrderSync;
	
	@Autowired
	SuningItemsSearcher suningItemsSearcher;
	
	@Test
	public void getCategory() {
		try {
			suningHelper.getCategories();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void search() {
		SelectrecommendcommodityQueryRequest request = new SelectrecommendcommodityQueryRequest();
		request.setEliteId("1");
		request.setPageIndex("0");//默认从第一页开始:下标从0开始
		request.setSize("40");//最大40
		request.setPicHeight("600");
		request.setPicWidth("600");
		try {
			JSONArray result = suningHelper.search(request);
			System.err.println(JsonUtil.transferToJson(result));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void getDetail() {
		String brokerId = "system";//默认都认为是平台自己生成的
		String url = "https://product.suning.com/0000000000/12208306208.html";
		Pattern p=Pattern.compile("(\\d+)/(\\d+)"); 
		Matcher m=p.matcher(url); 
		System.err.println("start query item detail .... ");
		if(m.find() && m.groupCount()>=2 ) { //https://product.suning.com/0000000000/12208306208.html
			System.err.println("matched item .... [group]"+m.groupCount());
		    String skuId = m.group(2)+"-"+m.group(1); //组装skuId
		    System.err.println("matched item .... [skuId]"+skuId);
			JSONObject  result = suningHelper.getDetail(skuId);
			System.err.println(result);
			System.out.println("[URL]"+URLDecoder.decode(result.getString("extendUrl"))+"&sub_user="+brokerId);
			System.out.println("[URL]"+URLDecoder.decode(result.getString("shortUrl"))+"&sub_user="+brokerId);
		}
	}
	
	@Test
	public void generateCpsLink() {
		String brokerId = "system";
		String url = "https://product.suning.com/0000000000/12208306208.html";
		try {
			JSONObject result = suningHelper.generateCpsLink(brokerId,url);
			System.err.println(URLDecoder.decode(result.getString("extendUrl")));
			System.err.println(URLDecoder.decode(result.getString("shortUrl")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	
	@Test
	public void getOrders() {
		try {
			JSONArray result = suningHelper.getOrders();
			System.err.println(JSONArray.toJSONString(result));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void convertLinks() {
		String brokerId = "system";
		String urls = "https://product.suning.com/0000000000/12208306208.html,https://product.suning.com/0000000000/000000011908462305.html,https://product.suning.com/0000000000/12208306208.html,https://product.suning.com/0000000000/000000011908462305.html";
		try {
			String[] links = suningHelper.convertLinks(brokerId, urls);
			for(int i=0;i<links.length;i++)
				System.err.println(links[i]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void sunningItemSync() {
		try {
			suningItemSync.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void sunningOrderSync() {
		try {
			suningOrderSync.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void sunningSearchTask() {
		try {
			suningItemsSearcher.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
