package com.pcitech.iLife.task;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.google.gson.Gson;
import com.jd.open.api.sdk.internal.JSON.JSON;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.utils.IdGen;
import com.pcitech.iLife.cps.KaolaHelper;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.cps.SuningHelper;
import com.pcitech.iLife.cps.kaola.CategoryInfo;
import com.pcitech.iLife.cps.kaola.GoodInfo;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsRequest;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsResponse;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsSearchRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkOauthGoodsSearchRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkOauthGoodsSearchRequest.RangeListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsSearchResponse.GoodsSearchResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsSearchResponse.GoodsSearchResponseGoodsListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.suning.api.entity.advertise.UnitlistQueryRequest;
import com.suning.api.entity.netalliance.SelectrecommendcommodityQueryRequest;
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 通过API接口，搜索得到在推广商品。
 * 通过自动任务触发，每天执行一次即可。遍历所有新增的商品
 * 
 */
@Service
public class SuningItemsSearcher {
    private static Logger logger = LoggerFactory.getLogger(SuningItemsSearcher.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    SuningHelper suningHelper;
    @Autowired
	private PlatformCategoryService platformCategoryService;
    
    //默认设置
    int pageSize = 40;//最大单页40条
    String urlPattern = "https://product.suning.com/supplierCode/commodityCode.html";//https://product.suning.com/0000000000/12286765453.html
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    Map<String,Integer> processedMap = null;
    Map<String,Integer> totalMap = null;
    Map<String,String> poolNameMap = null;
    
    String brokerId = "system";//默认设置为default
    
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;

    public SuningItemsSearcher() {
    		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
    }
    
    private void upsertItem(String poolName,JSONObject item) {
    		JSONObject commodityInfo = item.getJSONObject("commodityInfo");
    		JSONObject couponInfo = item.getJSONObject("couponInfo");
		String  url = null;
		if(commodityInfo.getString("productUrl")!=null ) {
			url = commodityInfo.getString("productUrl");//得到唯一URL
			logger.debug("got url.[url]"+url);
		}else if(commodityInfo.getString("supplierCode")!=null && commodityInfo.getString("commodityCode")!=null ) {//如果为空，则直接使用 supplierCode及commodityCode拼接
			url = urlPattern.replace("supplierCode", commodityInfo.getString("supplierCode"));
			url = url.replace("commodityCode", commodityInfo.getString("commodityCode"));
			logger.debug("compose url with supplierCode and commodityCode.[url]"+url);
		}else {
			logger.debug("cannot get url");
			return;//URL都没捞到，直接收工了
		}

		String itemKey = Util.md5(url);//根据URL生成唯一key
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		doc.getProperties().put("url", url);
		Map<String,Object> seller = new HashMap<String,Object>();
		seller.put("name", commodityInfo.getString("supplierName"));
		doc.getProperties().put("seller", seller);	
		Map<String,Object> distributor = new HashMap<String,Object>();
		distributor.put("name", "苏宁");
		doc.getProperties().put("distributor", distributor);	
		Map<String,Object> link = new HashMap<String,Object>();
		link.put("web", url);
		link.put("wap", url);//只有web地址，将就着用吧
		link.put("coupon", couponInfo.getString("couponUrl"));
		doc.getProperties().put("link", link);		
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-suningItemsSearcher");
		task.put("executor", "robot-suningItemsSearcher-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		doc.getProperties().put("task", task);
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "suning");

		doc.getProperties().put("title", commodityInfo.getString("commodityName"));//更新title
		
		//更新品牌信息到prop列表
		Map<String,String> props = new HashMap<String,String>();
		JSONArray parametersList = item.getJSONArray("parametersList");
		for(int i=0;i<parametersList.size();i++) {
			JSONObject paramObj = parametersList.getJSONObject(i);
			props.put(paramObj.getString("parameterDesc"), paramObj.getString("parameterVal"));
		}
		doc.getProperties().put("props", props);
		
		//更新图片列表：注意脚本中已经有采集，此处使用自带的内容
		List<String> images = new ArrayList<String>();
		JSONArray pictureUrlArray = commodityInfo.getJSONArray("pictureUrl");
		for(int i=0;i<pictureUrlArray.size();i++) {
			JSONObject pictureUrl = pictureUrlArray.getJSONObject(i);
			images.add(pictureUrl.getString("picUrl"));
			if("1".equalsIgnoreCase(pictureUrl.getString("locationId"))) {
				//将第一张展示图片作为logo
				doc.getProperties().put("logo", pictureUrl.getString("picUrl"));
			}
				
		}
		doc.getProperties().put("images", images);
		doc.getProperties().put("summary", commodityInfo.getString("sellingPoint"));//将卖点作为summary

		//增加类目
		List<String> categories = new ArrayList<String>();
		JSONObject categoryInfo = item.getJSONObject("categoryInfo");
		categories.add(categoryInfo.getString("firstPurchaseCategoryName"));//采购目录
		categories.add(categoryInfo.getString("secondPurchaseCategoryName"));
		categories.add(categoryInfo.getString("thirdPurchaseCategoryName"));
		categories.add(categoryInfo.getString("firstSaleCategoryName"));//销售目录
		categories.add(categoryInfo.getString("secondSaleCategoryName"));
		categories.add(categoryInfo.getString("thirdSaleCategoryName"));
		doc.getProperties().put("category", categories);//更新类目，包含多级分类

		String category = categoryInfo.getString("firstPurchaseCategoryName");//采购目录
		category += " "+categoryInfo.getString("secondPurchaseCategoryName");
		category += " "+categoryInfo.getString("thirdPurchaseCategoryName");
		doc.getProperties().put("category", category);//更新类目，包含多级分类
		//检查类目映射
		PlatformCategory query = new PlatformCategory();
		query.setName(category);
		query.setPlatform("suning");
		List<PlatformCategory> list = platformCategoryService.findMapping(query);
		if(list.size()>0) {//有则更新
			Map<String,Object> meta = new HashMap<String,Object>();
			meta.put("category", list.get(0).getCategory().getId());
			meta.put("categoryName", list.get(0).getCategory().getName());
			doc.getProperties().put("meta", meta);	
		}else {//否则写入等待标注
			query.setIsNewRecord(true);
			query.setId(Util.md5("suning"+category));//采用手动生成ID，避免多次查询生成多条记录
			query.setPlatform("suning");
			query.setCreateDate(new Date());
			query.setUpdateDate(new Date());
			platformCategoryService.save(query);
		}
		
		//更新CPS链接：在link基础上补充
//		link.put("wap2", item.getLinkInfo().getShortShareUrl());
//		link.put("web2", item.getLinkInfo().getShortShareUrl());
//		link.put("miniprog", item.getLinkInfo().getMiniShareUrl());
//		doc.getProperties().put("link", link);

		//更新价格：直接覆盖
		Map<String,Object> price = new HashMap<String,Object>();
		price.put("currency", "￥");
		if(couponInfo.getString("couponValue")!=null&&couponInfo.getString("couponValue").trim().length()>0)
			price.put("coupon", parseNumber(couponInfo.getString("couponValue")));//获取优惠券面额
		price.put("sale", parseNumber(commodityInfo.getString("commodityPrice")));//没错，这个是卖价
		price.put("bid", parseNumber(commodityInfo.getString("snPrice")));//没错，这个是原价
		doc.getProperties().put("price", price);
		
		//更新佣金：直接覆盖
		if(couponInfo.getString("rate")!=null&&couponInfo.getString("rate").trim().length()>0) {//仅在有佣金比例返回时才处理
			Map<String,Object> profit = new HashMap<String,Object>();
			profit.put("rate", parseNumber(commodityInfo.getString("rate")));//返回的是百分比，直接使用即可
			double amount = parseNumber(commodityInfo.getString("rate"))*parseNumber(commodityInfo.getString("commodityPrice"))*0.01;
			profit.put("amount", parseNumber(""+amount));
			profit.put("type", "2-party");
			doc.getProperties().put("profit", profit);
		}
		
		//设置状态。注意，需要设置sync=pending 等待计算CPS链接
		//状态更新
		Map<String,Object> status = new HashMap<String,Object>();
		status.put("crawl", "ready");
		status.put("sync", "pending");//等待生成CPS链接
		status.put("load", "pending");
		status.put("classify", "pending");
		status.put("satisify", "pending");//这个要在classify之后才执行
		status.put("measure", "pending");
		status.put("evaluate", "pending");
		status.put("monitize", "pending");//等待计算3-party分润
		status.put("poetize", "pending");//实际上这个要在classify之后才执行
		status.put("index", "pending");//先入库一次，能够立即看到：注意这时候没有CPS，不能推广
		doc.getProperties().put("status", status);
		//时间戳更新
		Map<String,Object> timestamp = new HashMap<String,Object>();
		timestamp.put("crawl", new Date());//入库时间
		doc.getProperties().put("timestamp", timestamp);

		//更新doc
		logger.debug("try to upsert suning item.[itemKey]"+itemKey,JSON.toString(doc));
//		需要区分是否已经存在，如果已经存在则直接更新，但要避免更新profit信息，以免出发3-party分润任务
//		arangoClient.upsert("my_stuff", itemKey, doc);   
		BaseDocument old = arangoClient.find("my_stuff", itemKey);
		if(old!=null && itemKey.equals(old.getKey())) {//已经存在，则直接跳过
			//do nothing
		}else {//否则写入
			arangoClient.insert("my_stuff", doc);   
			processedMap.put(poolName, processedMap.get(poolName)+1);
			processedAmount++;
		}
    }
    
    private double parseNumber(String str) {
    		logger.debug("parse number.[input]"+str);
    		Double d = Double.parseDouble(str);
    		String numStr = nf.format(d);
    		try {
    			return Double.parseDouble(numStr);
    		}catch(Exception ex) {
    			return 0;
    		}
    }

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，通过搜索API获取最新商品，并入库
	 * 2，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		logger.info("Suning cps item search job start. " + new Date());
    		processedMap = new HashMap<String,Integer>();
    		totalMap = new HashMap<String,Integer>();
    		poolNameMap = new HashMap<String,String>();

        //准备查询条件
    		//默认为1,,营销id。1-小编推荐；2-实时热销榜；3-当日热推榜；4-高佣爆款榜；5-团长招商榜；6-9块9专区
    		String poolNameStr = "1-小编推荐；2-实时热销榜；3-当日热推榜；4-高佣爆款榜；5-团长招商榜；6-9块9专区";
    		String[] poolNameArray = poolNameStr.split("；");
    		poolNameStr = poolNameStr.replaceAll("\\d+\\-", "");
    		String[] poolNames = poolNameStr.split("；");
    		int k=0;
    		for(String str:poolNames) {
    			logger.debug(poolNameArray[k]+"-->"+str);
    			k++;
    		}
    		
    		//准备连接
    		arangoClient = new ArangoDbClient(host,port,username,password,database);
        int poolNameIndex = 1;
    		for(String poolName:poolNames) {//逐个分类查询，每个分类均进行遍历
    			logger.debug("start search by poolName.[poolName]"+poolName);
    			totalMap.put(poolName, 0);//初始化各个分类的总数量，后面查询时累计
    			processedMap.put(poolName, 0);//默认设置相应分类的条数为0
    			SelectrecommendcommodityQueryRequest request = new SelectrecommendcommodityQueryRequest();
    			request.setEliteId(""+poolNameIndex);
    			poolNameIndex++;
    			request.setPageIndex("0");//默认从第一页开始:下标从0开始
    			request.setSize(""+pageSize);//最大40
    			request.setPicHeight("600");
    			request.setPicWidth("600");
    			
    			//Step1:搜索得到推荐商品列表
    			boolean hasNextPage = true;//这个返回结果比较奇葩，在每一个列表项下竟然都设置了一个isHaveData，如果为1则表示有下一页。默认认为至少有一页
    			int pageIndex = 0;
    			while(hasNextPage) {
    				request.setPageIndex(""+pageIndex);//默认从第一页开始:下标从0开始
        			logger.debug("try to search items.[page]"+pageIndex+"\t[request]"+JsonUtil.transferToJson(request));
        			JSONArray resp = suningHelper.search(request);//得到一个商品结果列表
        			totalMap.put(poolName,totalMap.get(poolName)+resp.size() );//默认设置相应分类的总条数
    	    			totalAmount += resp.size();
	    			for(int j=0;j<resp.size();j++) {//逐个遍历
	    				JSONObject item = resp.getJSONObject(j);
	    				logger.debug(JsonUtil.transferToJson(item));
					upsertItem(poolName,item);
					//处理奇葩的下一页标志
					if("1".equalsIgnoreCase(item.getString("isHaveData")))//
						hasNextPage = true;
					else
						hasNextPage = false;
    					pageIndex++;
	    			}
    			}	    		
    		}

		//完成后关闭arangoDbClient
		arangoClient.close();
		if(totalAmount == 0)//啥活都没干，发啥消息
			return;
		//组装通知信息
		StringBuffer remark = new StringBuffer();
		remark.append("预期数量："+totalAmount);
		for(Map.Entry<String, Integer> entry:processedMap.entrySet()) {
			if(entry.getValue()>0)
				remark.append("\n"+entry.getKey()+"："+entry.getValue());
		}
		remark.append("\n数量差异："+(totalAmount-processedAmount));
		
		
		//发送处理结果到管理员
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", "o8HmJ1EdIUR8iZRwaq1T7D_nPIYc");//固定发送
		msg.put("title", "导购数据同步任务结果");
		msg.put("task", "苏宁商品入库 已同步");
		msg.put("time", fmt.format(new Date()));
		msg.put("remark", remark);
		msg.put("color", totalAmount-processedAmount==0?"#FF0000":"#000000");

		result = HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/data-sync-notify", 
				msg,header);
		//3，更新通知状态
		if(result.getBooleanValue("status")) {
			logger.info("clearing notification msg sent.[msgId] " + result.getString("msgId"));
		}
        logger.info("Clearing Notification job executed.[msg]" + msg);
        
        //处理数量归零
        processedAmount = 0;
    }

}
