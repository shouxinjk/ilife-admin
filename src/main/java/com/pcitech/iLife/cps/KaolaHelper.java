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
import com.haitao.thirdpart.sdk.APIUtil;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.kaola.GoodsInfoRequest;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.cps.kaola.OrderInfoRequest;
import com.pcitech.iLife.cps.kaola.OrderInfoResponse;
import com.pcitech.iLife.cps.kaola.ShareLinkRequest;
import com.pcitech.iLife.cps.kaola.ShareLinkResponse;

//通过API接口完成商品查询获得类目信息
@Service
public class KaolaHelper {
	private static Logger logger = LoggerFactory.getLogger(KaolaHelper.class);
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//方法调用签名。对参数按字母排序后MD5得到，需要过滤掉系统参数，直接调用API完成
	private String createSign(TreeMap<String,String> map ) {
		map.put("timestamp", sdf.format(new Date()));
		map.put("v", "1.0");
		map.put("unionId", Global.getConfig("kaola.unionId"));
		map.put("signMethod", "md5");
		String sign = APIUtil.createSign(Global.getConfig("kaola.appSecret"), map);
		return sign;
	}
	
	/**
	 * 根据商品列表查询商品详情。商品ID以逗号分隔，最多10个
	 * @param brokerId
	 * @param ids
	 * @return
	 */
	public GoodsInfoResponse getItemDetail(String brokerId,String ids) {
		//参数
		GoodsInfoRequest req = new GoodsInfoRequest();
		req.setGoodsIds(ids);
		req.setTrackingCode1(brokerId);
		TreeMap<String,String> map = req.getMap();
		map.put("sign", createSign(map));
		//请求并返回解析结果
		return JSON.parseObject(post(map),GoodsInfoResponse.class);
	}
	
	/**
	 * TODO 订单查询，需要分解为多个具体查询接口
	 * 查询类型：1:根据下单时间段查询 2:根据订单号查询 3:根据更新时间查询
	 * @return
	 */
	public OrderInfoResponse getOrder() {
		//参数:按照时间段查询
		Calendar cal = Calendar.getInstance();
		Date end = cal.getTime();
		cal.add(Calendar.MINUTE, -30);//每半小时查询一次
		Date from = cal.getTime();
		
		OrderInfoRequest req = new OrderInfoRequest();
		req.setType(1);//1:根据下单时间段查询 2:根据订单号查询 3:根据更新时间查询
		req.setStartDate(from.getTime());
		req.setEndDate(end.getTime());
		TreeMap<String,String> map = req.getMap();
		map.put("sign", createSign(map));
		//请求并返回解析结果
		return JSON.parseObject(post(map),OrderInfoResponse.class);
	}
	
	/**
	 * 链接转化。将其他推广链接转换为自己的推广链接。
	 * @param brokerId：所属达人
	 * @param links：需要转换的链接，最多10个
	 * @return
	 */
	public ShareLinkResponse convertLinks(String brokerId,List<String> links) {
		ShareLinkRequest req = new ShareLinkRequest();
		req.setTrackingCode1(brokerId);
		req.setLinkList(links);
		TreeMap<String,String> map = req.getMap();
		map.put("sign", createSign(map));
		//请求并返回解析结果
		return JSON.parseObject(post(map),ShareLinkResponse.class);
	}
	
	/**
	 * 调用API方法。具体调用方法通过参数传递，在Request内置封装
	 * @param map
	 * @return
	 */
	private String post(TreeMap<String,String> map) {
		//参数处理
		map.put("sign", createSign(map));//自动对参数进行签名
		String params = getParams(map);//将参数转换为键值对
		logger.error(Global.getConfig("kaola.api.https") + params);
		
		// 创建Post请求
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
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
			return URLDecoder.decode(result.toString(),"utf-8");//网易同学太挫了，竟然还需要手动转码
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
	
	//组装成链接参数，注意需要进行URIEncode
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
			try {
				params.append(URLEncoder.encode(entry.getValue(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				params.append(entry.getValue());
			}
			index++;
		}
		return params.toString();
	}
}
