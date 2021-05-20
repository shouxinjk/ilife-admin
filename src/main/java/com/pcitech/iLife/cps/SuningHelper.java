package com.pcitech.iLife.cps;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.common.config.Global;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.netalliance.BacthcustomlinkQueryRequest;
import com.suning.api.entity.netalliance.BacthcustomlinkQueryResponse;
import com.suning.api.entity.netalliance.CommoditycategoryQueryRequest;
import com.suning.api.entity.netalliance.CommoditycategoryQueryRequest.CommoditycategoryList;
import com.suning.api.entity.netalliance.CommoditycategoryQueryResponse;
import com.suning.api.entity.netalliance.CommoditydetailQueryRequest;
import com.suning.api.entity.netalliance.CommoditydetailQueryResponse;
import com.suning.api.entity.netalliance.CustompromotionurlQueryRequest;
import com.suning.api.entity.netalliance.CustompromotionurlQueryResponse;
import com.suning.api.entity.netalliance.OrderinfoQueryRequest;
import com.suning.api.entity.netalliance.OrderinfoQueryResponse;
import com.suning.api.exception.SuningApiException;

//通过API接口完成商品查询获得类目信息
@Service
public class SuningHelper {
	private static Logger logger = LoggerFactory.getLogger(SuningHelper.class);
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DefaultSuningClient client  = null;
	
	private DefaultSuningClient getClient() {
		if(client  == null) {
			client = new DefaultSuningClient(Global.getConfig("suning.api.https"), 
					Global.getConfig("suning.appKey"), 
					Global.getConfig("suning.appSecret"), 
					"json");
		}
	    return client;
	}
	
	public void getCategories() throws Exception {
		CommoditycategoryQueryRequest request = new CommoditycategoryQueryRequest();
		CommoditycategoryList commoditycategoryList= new CommoditycategoryList();
		commoditycategoryList.setGrade("1");
		commoditycategoryList.setParentId("R0103");
		commoditycategoryList.setSaleCategoryId("R0103");
		commoditycategoryList.setSaleGrade("1");
		List<CommoditycategoryList> commoditycategoryListList =new ArrayList<CommoditycategoryList>();
		commoditycategoryListList.add(commoditycategoryList);
		request.setCommoditycategoryList(commoditycategoryListList);
//		request.setCheckParam(true);//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
		try {
			CommoditycategoryQueryResponse response = getClient().excute(request);
			System.out.println(response.getBody());
		} catch (SuningApiException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param skuIds 是需要查询的商品列表，组装规则：skuId-supplierId_skuId-supplierId
	 * @return
	 */
	public JSONObject getDetail(String skuIds) {
		CommoditydetailQueryRequest request = new CommoditydetailQueryRequest();
//		request.setCityCode("025");
		request.setCommodityStr(skuIds);
//		request.setCouponMark("1");
		request.setPicHeight("600");
		request.setPicWidth("600");
		
//		request.setCheckParam(true);//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
		try {
			CommoditydetailQueryResponse response = getClient().excute(request);
			logger.error(response.getBody());
			JSONObject jsonobj = JSON.parseObject(response.getBody());
			return jsonobj.getJSONObject("sn_responseContent").getJSONObject("sn_body").getJSONArray("queryCommoditydetail").getJSONObject(0);
		} catch (SuningApiException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject generateCpsLink(String brokerId,String url) {
		CustompromotionurlQueryRequest request = new CustompromotionurlQueryRequest();
		request.setAdBookId(Global.getConfig("suning.adBookId"));
		request.setSubUser(brokerId);
		request.setVisitUrl(url);
//		request.setCheckParam(true);//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
		try {
		 CustompromotionurlQueryResponse response = getClient().excute(request);
		 logger.debug(response.getBody());
		 JSONObject jsonObj = JSON.parseObject(response.getBody());
		 return jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_body").getJSONObject("queryCustompromotionurl");
		} catch (SuningApiException e) {
		 e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 批量转链。能够将多个URL转换为指定达人的推广链接
	 * @param brokerId 达人ID
	 * @param urls 需要转换的URL，多个使用逗号分隔
	 * @return 返回转链结果，是一个JSONArray，包含短连接
	 */
	public String[] convertLinks(String brokerId,String urls) {
		BacthcustomlinkQueryRequest request = new BacthcustomlinkQueryRequest();
		request.setExtend(urls);
		request.setPromotionId(Global.getConfig("suning.adBookId"));
		request.setSubUser(brokerId);
//		request.setCheckParam(true);//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行

		try {
		 BacthcustomlinkQueryResponse response = getClient().excute(request);
		 JSONObject jsonObj = JSON.parseObject(response.getBody());
		 logger.debug(response.getBody());
		 //注意：返回值有错误，未能正确封装为JSON数组，只能通过字符串手动处理，这帮傻缺~~~
		 String linkStr = jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_body").getJSONObject("queryBacthcustomlink").getString("shortLink");
		 String[] links = linkStr.split("\\[|,|\\]");
		 return links;
		} catch (SuningApiException e) {
		 e.printStackTrace();
		}
		return null;
	}
	
	public void getOrder() {
		//参数:按照时间段查询
		Calendar cal = Calendar.getInstance();
		Date end = cal.getTime();
		cal.add(Calendar.MINUTE, -30);//每半小时查询一次
		Date from = cal.getTime();
		
		OrderinfoQueryRequest request = new OrderinfoQueryRequest();
		request.setStartTime(""+from.getTime());
		request.setEndTime(""+end.getTime());
//		request.setOrderId("35499999921");
		request.setPageNo(1);//查询页码
		request.setPageSize(200);//默认查询条数
//		request.setCheckParam(true);//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行

		try {
		 OrderinfoQueryResponse response = getClient().excute(request);
		 //TODO 待完善。当前还没有订单的么
		 System.out.println("返回json/xml格式数据 :" + response.getBody());
		} catch (SuningApiException e) {
		 e.printStackTrace();
		}
	}
}
