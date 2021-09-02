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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.entity.BaseDocument;
import com.jd.open.api.sdk.internal.JSON.JSON;
import com.jd.open.api.sdk.internal.util.JsonUtil;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.cps.kaola.OrderInfoResponse;
import com.pcitech.iLife.cps.kaola.QueryRecommendGoodsListRequest;
import com.pcitech.iLife.cps.kaola.QueryRecommendGoodsListResponse;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsRequest;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsResponse;
import com.pcitech.iLife.cps.kaola.ShareLinkResponse;
import com.pcitech.iLife.task.GomeItemSearcher;
import com.pcitech.iLife.task.GomeOrderSync;
import com.pcitech.iLife.task.KaolaItemSync;
import com.pcitech.iLife.task.KaolaItemsSearcher;
import com.pcitech.iLife.task.KaolaRecommendItemsSearcher;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.Util;

import net.minidev.json.JSONUtil;


@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class GomeClientTest {
	private static Logger logger = LoggerFactory.getLogger(GomeClientTest.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
	
	@Autowired
	GomeHelper gomeHelper;
	
	@Autowired
	GomeItemSearcher gomeItemSearcher;
	
	@Autowired
	GomeOrderSync gomeOrderSync;
	
	@Test
	public void getCategories() {
		String source = "gome";
		TreeMap<String,String> request = gomeHelper.getParamMap();
		try {
			System.out.println("try to get categories.[request]"+JsonUtil.toJson(request));
			JSONObject resp = gomeHelper.getCategory(request);
			logger.debug(JsonUtil.toJson(resp));
			  //准备连接
			arangoClient = new ArangoDbClient(host,port,username,password,database);
			
			JSONArray categories = resp.getJSONArray("categorys");
			for(int k=0;categories!=null&&k<categories.size();k++) {
				JSONObject item = categories.getJSONObject(k);
				String itemKey = Util.md5(source+item.getString("category_id"));//所有原始category保持 gome+CategoryId的形式唯一识别
				BaseDocument doc = new BaseDocument();
				doc.setKey(itemKey);
				doc.getProperties().put("source", source);
				doc.getProperties().put("pid", item.getString("parent_id"));//原始父id
				doc.getProperties().put("name", item.getString("category_name"));//名称
				doc.getProperties().put("id", item.getString("category_id"));//原始id
				doc.getProperties().put("level", item.getInteger("category_grade"));//层级 
				logger.debug("try to upsert gome category.[itemKey]"+itemKey,JsonUtil.toJson(item));
				arangoClient.upsert("platform_categories", itemKey, doc);  
			}
			//完成后关闭arangoDbClient
			arangoClient.close();
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
	public void searchItemByCategory() {
		try {
			gomeItemSearcher.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void syncOrder() {
		try {
			gomeOrderSync.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
