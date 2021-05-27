package com.pcitech.iLife.cps;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSONObject;
import com.jd.open.api.sdk.internal.util.JsonUtil;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.cps.kaola.OrderInfoResponse;
import com.pcitech.iLife.cps.kaola.QueryRecommendGoodsListRequest;
import com.pcitech.iLife.cps.kaola.QueryRecommendGoodsListResponse;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsRequest;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsResponse;
import com.pcitech.iLife.cps.kaola.ShareLinkResponse;
import com.pcitech.iLife.task.GomeItemSearcher;
import com.pcitech.iLife.task.KaolaItemSync;
import com.pcitech.iLife.task.KaolaItemsSearcher;
import com.pcitech.iLife.task.KaolaRecommendItemsSearcher;

import net.minidev.json.JSONUtil;


@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class GomeClientTest {
	
	@Autowired
	GomeHelper gomeHelper;
	
	@Autowired
	GomeItemSearcher gomeItemSearcher;
	
	@Test
	public void getCategories() {
		TreeMap<String,String> request = gomeHelper.getParamMap();
		try {
			System.out.println("try to get categories.[request]"+JsonUtil.toJson(request));
			JSONObject resp = gomeHelper.getCategory(request);
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getAllItems() {
		TreeMap<String,String> request = gomeHelper.getParamMap();
		request.put("page_no", "1");//分页：默认第1页
		request.put("page_size", "10");//每页条数：最大10000
		try {
			System.out.println("try to get all items.[request]"+JsonUtil.toJson(request));
			JSONObject resp = gomeHelper.getAllItems(request);
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getIncreasedItems() {
		TreeMap<String,String> request = gomeHelper.getParamMap();
		request.put("page_no", "1");//分页：默认第1页
		request.put("page_size", "10");//每页条数：最大10000
		try {
			System.out.println("try to get increased items.[request]"+JsonUtil.toJson(request));
			JSONObject resp = gomeHelper.getIncreasedItems(request);
			//{"total_count":0,"items":[]}
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getOrders() {
		TreeMap<String,String> request = gomeHelper.getParamMap();
		request.put("page_no", "1");//分页：默认第1页
		request.put("page_size", "10");//每页条数：最大100
		try {
			System.out.println("try to get occured orders.[request]"+JsonUtil.toJson(request));
			JSONObject resp = gomeHelper.getOrders(request);
			//{"total_count":0,"commission_sum":0.0000,"orders":[],"productnum_sum":0,"productprice_sum":0.0000}
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getValidOrders() {
		TreeMap<String,String> request = gomeHelper.getParamMap();
		request.put("page_no", "1");//分页：默认第1页
		request.put("page_size", "10");//每页条数：最大100
		try {
			System.out.println("try to get valid orders.[request]"+JsonUtil.toJson(request));
			JSONObject resp = gomeHelper.getValidOrders(request);
			//{"total_count":0,"commission_sum":0.0000,"orders":[],"productnum_sum":0,"productprice_sum":0.0000}
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getAllLimitBuyItems() {
		TreeMap<String,String> request = gomeHelper.getParamMap();
		request.put("page_no", "1");//分页：默认第1页
		request.put("page_size", "10");//每页条数：最大1000
		try {
			System.out.println("try to get all limit buy items.[request]"+JsonUtil.toJson(request));
			JSONObject resp = gomeHelper.getAllLimitBuyItems(request);
			//{"total_count":0,"promo_items":[]}
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getIncreasedLimitBuyItems() {
		TreeMap<String,String> request = gomeHelper.getParamMap();
		request.put("page_no", "1");//分页：默认第1页
		request.put("page_size", "10");//每页条数：最大10000
		try {
			System.out.println("try to get increased limit buy items.[request]"+JsonUtil.toJson(request));
			JSONObject resp = gomeHelper.getIncreasedLimitBuyItems(request);
			//{"total_count":0,"promo_items":[]}
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getHighCommissionRateItems() {
		TreeMap<String,String> request = gomeHelper.getParamMap();
		request.put("page_no", "1");//分页：默认第1页
		request.put("page_size", "10");//每页条数：最大100
		try {
			System.out.println("try to get high commission rate items.[request]"+JsonUtil.toJson(request));
			JSONObject resp = gomeHelper.getHighCommissionRateItems(request);
			//{"Products":[]}
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void searchItemTaskIncreament() {
		try {
			gomeItemSearcher.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
