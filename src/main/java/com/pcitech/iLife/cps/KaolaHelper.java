package com.pcitech.iLife.cps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.haitao.thirdpart.sdk.APIUtil;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;

//通过API接口完成商品查询获得类目信息
@Service
public class KaolaHelper {
	private static Logger logger = LoggerFactory.getLogger(KaolaHelper.class);
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private CloseableHttpClient client = null;
	
	private HttpClient getClient() {
		if(client == null)
			client = HttpClientBuilder.create().build();
		return client;
	}
	
	public String createSign(TreeMap<String,String> map ) {
		map.put("timestamp", sdf.format(new Date()));
		map.put("v", "1.0");
		map.put("unionId", Global.getConfig("kaola.unionId"));
		map.put("signMethod", "md5");
		String sign = APIUtil.createSign(Global.getConfig("kaola.appSecret"), map);
		return sign;
	}
	
	public GoodsInfoResponse getItemDetail(String brokerId,String ids) {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		//参数
		TreeMap<String,String> map = new TreeMap<String,String>();
		map.put("method", "kaola.zhuanke.api.queryGoodsInfo");
		map.put("goodsIds", ids);
		map.put("trackingCode1", brokerId);
		map.put("sign", createSign(map));
		//组装成链接参数，注意需要进行URIEncode
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
			try {
				params.append(URLEncoder.encode(entry.getValue(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				params.append(entry.getValue());
			}
			index++;
		}

		// 创建Post请求
		System.err.println(Global.getConfig("kaola.api.https") + params);
		HttpPost httpPost = new HttpPost(Global.getConfig("kaola.api.https") + params);
		httpPost.setHeader("Accept", "application/json;charset=UTF-8");
//		httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
 
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
			GoodsInfoResponse goodsInfoResponse = JSON.parseObject(result.toString(),GoodsInfoResponse.class);
			return goodsInfoResponse;
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
	
}
