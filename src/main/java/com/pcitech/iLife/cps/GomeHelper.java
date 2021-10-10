package com.pcitech.iLife.cps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haitao.thirdpart.sdk.APIUtil;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.kaola.GoodsInfoRequest;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.cps.kaola.OrderInfoRequest;
import com.pcitech.iLife.cps.kaola.OrderInfoResponse;
import com.pcitech.iLife.cps.kaola.QueryRecommendGoodsListRequest;
import com.pcitech.iLife.cps.kaola.QueryRecommendGoodsListResponse;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsRequest;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsResponse;
import com.pcitech.iLife.cps.kaola.ShareLinkRequest;
import com.pcitech.iLife.cps.kaola.ShareLinkResponse;

//通过API接口完成商品查询获得类目信息
//直接使用HTTP client完成
@Service
public class GomeHelper {
	private static Logger logger = LoggerFactory.getLogger(GomeHelper.class);
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	//添加默认参数，包括appKey、appSecret、format
	public TreeMap<String,String> getParamMap() {
		TreeMap<String,String> map = new TreeMap<String,String>();
//		map.put("timestamp", sdf.format(new Date()));
//		map.put("api_name", api);//由各个请求自行准备
		map.put("app_key", Global.getConfig("gome.appKey"));
		map.put("app_secret", Global.getConfig("gome.appSecret"));
		map.put("format", "json");//默认都以JSON格式返回数据
		return map;
	}
	
	//获取商品类目
	/**
{"categorys":
	[
		{"category_grade":1,"category_id":"cat10000000","category_name":"数码","parent_id":"0","WebSite":"GOME"},
		{"category_grade":1,"category_id":"cat10000001","category_name":"电脑办公打印文仪","parent_id":"0","WebSite":"GOME"}
	]
}
	 * @return
	 */
	public JSONObject getCategory(TreeMap<String,String> map) {
		//参数
		map.put("api_name", "gome.categorys.get");
		//请求并返回解析结果
		return post(map);
	}
	
	//查询全量商品：每天更新一次
	public JSONObject getAllItems(TreeMap<String,String> map) {
		map.put("api_name", "gome.items.page.get");
		//请求并返回解析结果
		return post(map);
	}
	
	//查询增量商品：每小时更新一次
	public JSONObject getIncreasedItems(TreeMap<String,String> map) {
		map.put("api_name", "gome.items.maintain.page.get");
		//请求并返回解析结果
		return post(map);
	}
	
	//分页查询秒杀商品（全量 ）：每小时更新一次。每天最多调用三次
	public JSONObject getAllLimitBuyItems(TreeMap<String,String> map) {
		map.put("api_name", "gome.limitbuy.page.get");
		//请求并返回解析结果
		return post(map);
	}
	
	//分页查询秒杀商品（增量）：每小时更新一次。每小时最多调用三次
	public JSONObject getIncreasedLimitBuyItems(TreeMap<String,String> map) {
		map.put("api_name", "gome.limitbuy.maintain.page.get");
		//请求并返回解析结果
		return post(map);
	}
	
	//查询高佣商品：每小时更新一次
	public JSONObject getHighCommissionRateItems(TreeMap<String,String> map) {
		map.put("api_name", "gome.commission.high.get");
		//请求并返回解析结果
		return post(map);
	}
	
	//分页查询订单：是已经发生的订单，但需要以valid订单为主
	public JSONObject getOrders(TreeMap<String,String> map) {
		map.put("api_name", "gome.orders.occur.get");
		//请求并返回解析结果
		return post(map);
	}
	
	//分页查询有效订单
	public JSONObject getValidOrders(TreeMap<String,String> map) {
		map.put("api_name", "gome.orders.valid.get");
		//请求并返回解析结果
		return post(map);
	}
	/**
	 * 调用API方法。具体调用方法通过参数传递，在Request内置封装
	 * @param map
	 * @return
	 */
	private JSONObject post(TreeMap<String,String> map) {
		//参数处理
		String params = getParams(map);//将参数转换为键值对
		logger.debug(Global.getConfig("gome.api.http") + params);
		
		// 创建Get请求
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(Global.getConfig("gome.api.http") + params);
		httpPost.setHeader("Accept", "application/json;charset=UTF-8");
		httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
 
		// 响应模型
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent())); 
			StringBuffer result = new StringBuffer();
			String line = ""; 
			while ((line = rd.readLine()) != null) { 
			     result.append(line);
			} 
			logger.debug(result.toString()); 
			return JSON.parseObject(result.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	//组装成链接参数，注意：不需要进行URIEncode
	private String getParams(TreeMap<String,String> map) {
		StringBuffer params = new StringBuffer();
		int index = 0;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if(index==0) {
				params.append("?");
			}else{
				params.append("&");
			}
			params.append(entry.getKey());
			params.append("=");
			params.append(entry.getValue());
//			try {
//				params.append(URLEncoder.encode(entry.getValue(), "utf-8"));
//			} catch (UnsupportedEncodingException e) {
//				params.append(entry.getValue());
//			}
			index++;
		}
		return params.toString();
	}
}
