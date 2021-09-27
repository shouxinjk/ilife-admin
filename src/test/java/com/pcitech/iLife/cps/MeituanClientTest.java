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
import com.pcitech.iLife.task.MeituanItemSearcher;
import com.pcitech.iLife.task.MeituanOrderSync;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.Util;

import net.minidev.json.JSONUtil;


@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class MeituanClientTest {
	private static Logger logger = LoggerFactory.getLogger(MeituanClientTest.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
	
	@Autowired
	MeituanHelper meituanHelper;
	
	@Autowired
	MeituanItemSearcher meituanItemSearcher;
	
	@Autowired
	MeituanOrderSync meituanOrderSync;
	
	
	@Test
	public void getItems() {
		int pageSize = 100;//默认每页返回100条
		int pageNo = 1;//默认每页返回100条
		String longitude = "114.42916810517";
		String latitude = "30.4561282109";

		try {
			JSONObject resp = meituanHelper.search("6", pageSize, pageNo, longitude, latitude);
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getOrders() {
		int pageSize = 100;//默认每页返回100条
		int pageNo = 1;//默认每页返回100条

		try {
			JSONObject resp = meituanHelper.getOrders("8", pageSize, pageNo);
			System.out.println(JsonUtil.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void searchItem() {
		try {
			meituanItemSearcher.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void syncOrder() {
		try {
			meituanOrderSync.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
