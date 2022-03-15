package com.pcitech.iLife.cps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
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
import com.pcitech.iLife.util.Util;

//通过API接口完成商品查询获得类目信息
//直接使用HTTP client完成
@Service
public class MeituanHelper {
	private static Logger logger = LoggerFactory.getLogger(MeituanHelper.class);
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	//参见：https://union.meituan.com/v2/apiDetail?id=21
	public JSONObject search(String type,int pageSize, int pageNo,String longitude, String latitude) {
		String url = Global.getConfig("meituan.api.search");
		TreeMap<String,String> map = Maps.newTreeMap();
		map.put("businessType", type);//当前只支持优选，编码为 6
		map.put("ts", ""+System.currentTimeMillis()/1000);//请求时刻10位时间戳(秒级)，有效期60s
		map.put("appkey", Global.getConfig("meituan.appKey"));//appkey
		map.put("sid", Global.getConfig("meituan.sid"));//推广位sid
		map.put("pageSize", ""+pageSize);//页大小，默认20，1~100
		map.put("pageNO", ""+pageNo);//第几页，默认：１
		map.put("longitude", longitude);//TODO   本地化业务入参-LBS信息-经度：当前默认设置，需要能够调整
		map.put("latitude", latitude);//TODO 本地化业务入参-LBS信息-纬度：当前默认设置，需要能够调整
		map.put("deviceType", "");//ios、android，非必填，留空
		map.put("deviceId", "");//iOS设备传明文IDFA Android设备版本10以上发MD5的OAID，版本10以下发MD5的IMEI号,非必填，留空
		return  get(url,map);
	}
	
	//参见：https://union.meituan.com/v2/apiDetail?id=1
	public JSONObject getOrders(String type, int pageSize, int pageNo) {
		Calendar cal = Calendar.getInstance();
		Date end = cal.getTime();
		cal.add(Calendar.MINUTE, -30);//每半小时查询一次
		Date from = cal.getTime();
		
		String url = Global.getConfig("meituan.api.order");
		TreeMap<String,String> map = Maps.newTreeMap();
		map.put("key", Global.getConfig("meituan.appKey"));//appkey
		map.put("ts", ""+System.currentTimeMillis()/1000);//请求时刻10位时间戳(秒级)，有效期60s
		map.put("type",type);//订单类型：查询订单类型，0 团购订单，2 酒店订单，4 外卖订单，5 话费&团好货订单，6 闪购订单，8 优选订单
		map.put("startTime", ""+from.getTime()/1000);//查询起始时间10位时间戳，以下单时间为准
		map.put("endTime", ""+end.getTime()/1000);//查询结束时间10位时间戳，以下单时间为准
		map.put("page", ""+pageNo);//TODO   当前仅处理一页，未作翻页处理
		map.put("limit", ""+pageSize);//每次返回100条
		map.put("queryTimeType", "1");//查询时间类型，枚举值：1 按订单支付时间查询；2 按订单发生修改时间查询
		return  get(url,map);
	}
	
	//参见：https://union.meituan.com/v2/apiDetail?id=8
	//TODO :当前接口仅接受 根据活动ID转链，不能根据URL直接转链。不能工作
	public JSONObject convert() {
		String url = Global.getConfig("meituan.api.convert");
		TreeMap<String,String> map = Maps.newTreeMap();
		map.put("businessType", "6");//当前只支持优选，编码为 6
		map.put("ts", ""+System.currentTimeMillis()/1000);//请求时刻10位时间戳(秒级)，有效期60s
		map.put("appkey", Global.getConfig("meituan.appKey"));//appkey
		map.put("sid", Global.getConfig("meituan.sid"));//推广位sid
		map.put("pageSize", "100");//页大小，默认20，1~100
		map.put("pageNO", "1");//第几页，默认：１
		map.put("longitude", "114.42916810517");//TODO   本地化业务入参-LBS信息-经度：当前默认设置，需要能够调整
		map.put("latitude", "30.4561282109");//TODO 本地化业务入参-LBS信息-纬度：当前默认设置，需要能够调整
		map.put("deviceType", "");//ios、android，非必填，留空
		map.put("deviceId", "");//iOS设备传明文IDFA Android设备版本10以上发MD5的OAID，版本10以下发MD5的IMEI号,非必填，留空
		return  get(url,map);
	}
	
	/**
	 * 调用API方法。具体调用方法通过参数传递，在Request内置封装
	 * 当前都是GET方法
	 * @param map
	 * @return
	 */
	private JSONObject get(String url, TreeMap<String,String> map) {
		//参数处理
		String params = getParamString(map);//将参数转换为键值对，包含有sign信息
		logger.error(url + params);
		
		// 创建Get请求
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url + params);
		httpGet.setHeader("Accept", "application/json;charset=UTF-8");
		httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
 
		// 响应模型
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
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
	
	public String getParamString(TreeMap<String,String> params) {
		StringBuffer buf = new StringBuffer();
		int i=0;
		//将所有参数组织为 key=value 字符串
		for (String key : params.keySet()) {
			if(i==0) 
				buf.append("?");
			else
				buf.append("&");
			buf.append(key);
			buf.append("=");
		    buf.append(params.get(key));
		    i++;
		}
		//添加签名
		buf.append("&sign=");
		buf.append(getSign(params));
		return buf.toString();
	}
	
	//生成签名，参见：https://union.meituan.com/v2/apiDetail?id=2
	private String getSign(TreeMap<String,String> params) {
		if (params.containsKey("sign")) { 
		    params.remove("sign");
		}
		String str = "";
		str += Global.getConfig("meituan.appSecret");
		for (String key : params.keySet()) {
		    str += key + params.get(key);
		}
		str += Global.getConfig("meituan.appSecret");
		String sign = Util.md5(str);
		return sign;
	}

	/**
	 * 这是美团的示例代码，已经废弃了，直接使用系统md5函数
	public String md5(String source) {
		String md5Result = null;
		try {
		        byte[] hash = org.apache.commons.codec.binary.StringUtils.getBytesUtf8(source);
		        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		        messageDigest.update(hash);
		        hash = messageDigest.digest();
		        md5Result = Hex.encodeHexString(hash);
		} catch (NoSuchAlgorithmException e) {
		        e.printStackTrace();
		}
		return md5Result;
	}
	//**/
}
