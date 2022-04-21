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

import com.alibaba.fastjson.JSONObject;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jd.open.api.sdk.internal.JSON.JSON;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.utils.IdGen;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsSearchRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkOauthGoodsSearchRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkOauthGoodsSearchRequest.RangeListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsSearchResponse.GoodsSearchResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsSearchResponse.GoodsSearchResponseGoodsListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.suning.api.entity.advertise.UnitlistQueryRequest;
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 通过拼多多API接口，搜索得到在推广商品。
 * 通过自动任务触发，每3分钟触发，每次处理30条（接口最大支持40条）。每天处理14400条。
 * 
 */
@Service
public class PddItemsSearcher {
    private static Logger logger = LoggerFactory.getLogger(PddItemsSearcher.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    PddHelper pddHelper;
    @Autowired
	private PlatformCategoryService platformCategoryService;
    
    //默认设置
    int pageSize = 100;
    String urlPrefix = "https://jinbao.pinduoduo.com/goods-detail?s=";//https://jinbao.pinduoduo.com/goods-detail?s=Y9X2m1wSlN1U8LcVwvfZHeaZwozbWFjf_JQvP59gKL7
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;

    public PddItemsSearcher() {
    		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
    }
    
    private void upsertItem(GoodsSearchResponseGoodsListItem item) {
		String  url = urlPrefix + item.getGoodsSign();//组装URL
//		String itemKey = Util.md5(url);//根据URL生成唯一key
		String itemKey = Util.md5(urlPrefix+item.getGoodsName());//生成唯一key：注意根据商品名称做唯一性校验，避免重复
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		doc.getProperties().put("url", url);
		Map<String,Object> distributor = new HashMap<String,Object>();
		distributor.put("name", "拼多多");
		doc.getProperties().put("distributor", distributor);	
		Map<String,Object> seller = new HashMap<String,Object>();
		seller.put("name", item.getMallName());
		doc.getProperties().put("seller", seller);	
		Map<String,Object> link = new HashMap<String,Object>();
		link.put("web", url);
		link.put("wap", url);
		doc.getProperties().put("link", link);		
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-pddItemsSearcher");
		task.put("executor", "robot-pddItemsSearcher-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		doc.getProperties().put("task", task);
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "pdd");
		doc.getProperties().put("title", item.getGoodsName());//更新title
		doc.getProperties().put("catIds", item.getCatIds());//更新category TODO 当前为ID，需要在同步替换
		doc.getProperties().put("tags", item.getUnifiedTags());//更新tag
		doc.getProperties().put("logo", item.getGoodsImageUrl());//更新首图 img
		doc.getProperties().put("summary", item.getGoodsDesc());//更新简介
		doc.getProperties().put("searchId", item.getSearchId());//更新searchId，生成CPS链接 需要
		List<String> images = new ArrayList<String>();
		images.add(item.getGoodsImageUrl());
		doc.getProperties().put("images", images);//更新图片
		Map<String,Object> props = new HashMap<String,Object>();
		props.put("品牌", item.getBrandName());
		doc.getProperties().put("props", props);	
		Map<String,Object> price = new HashMap<String,Object>();
		price.put("currency", "￥");
		price.put("coupon", parseNumber(item.getCouponDiscount()*0.01));//单位为分，换算为元
		price.put("bid", parseNumber(item.getMinNormalPrice()*0.01));//单位为分，换算为元
		price.put("sale", parseNumber(item.getMinGroupPrice()*0.01));//单位为分，换算为元
		doc.getProperties().put("price", price);	
		Map<String,Object> profit = new HashMap<String,Object>();
		profit.put("rate", parseNumber(item.getPromotionRate()*0.1));//是千分比，转换为百分比
		profit.put("type", "2-party");//待自动任务转换为3-party
		double salePrice = item.getMinGroupPrice()*0.01;//默认按卖价计算佣金
		if(item.getHasCoupon()) //如果有coupon则扣除coupon后计算
			salePrice = (item.getMinGroupPrice()-item.getCouponDiscount())*0.01;//实际卖价：转换为元
		double commssion = salePrice*item.getPromotionRate()*0.001;//千分比，转换为元
		profit.put("amount",parseNumber(commssion));//单位为元
		doc.getProperties().put("profit", profit);	
		//检查类目映射
		PlatformCategory platformCategoryMapping = platformCategoryService.get("pdd"+item.getCatIds().get(item.getCatIds().size()-1));
		if(platformCategoryMapping!=null) {//有则更新
			doc.getProperties().put("category", platformCategoryMapping.getName());	//补充原始类目名称
			if(platformCategoryMapping.getCategory()!=null) {//有则更新
				Map<String,Object> meta = new HashMap<String,Object>();
				meta.put("category", platformCategoryMapping.getCategory().getId());
				meta.put("categoryName", platformCategoryMapping.getCategory().getName());
				doc.getProperties().put("meta", meta);	
			}
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
		logger.debug("try to upsert pdd item.[itemKey]"+itemKey,JSON.toString(doc));
		arangoClient.upsert("my_stuff", itemKey, doc);   
		/**
		//需要区分是否已经存在，如果已经存在则直接更新，但要避免更新profit信息，以免出发3-party分润任务
		//存在同名但URL不同的情况，需要先排除同名商品：根据source及title排重
		Map<String,Object> bindVars = Maps.newHashMap();
		bindVars.put("source", "pdd");
		bindVars.put("title", item.getGoodsName());
		List<BaseDocument> sameItems = arangoClient.query("for doc in my_stuff filter doc.source==@source and doc.title==@title limit 1 return doc", bindVars, BaseDocument.class);
		if(sameItems!=null && sameItems.size()>0) {//存在同名商品则跳过
			//do nothing
		}else {//否则根据ID排重入库
			BaseDocument old = arangoClient.find("my_stuff", itemKey);
			if(old!=null && itemKey.equals(old.getKey())) {//已经存在，则直接跳过
				//do nothing
			}else {//否则写入
				arangoClient.insert("my_stuff", doc);   
				processedAmount++;
			}	
		}
		//**/
    }
    
    private double parseNumber(double d) {
//    		return Double.valueOf(String.format("%.2f", d ));//会四舍五入，丢弃
    		String numStr = nf.format(d);
    		try {
    			return Double.parseDouble(numStr);
    		}catch(Exception ex) {
    			return d;
    		}
    }
    
    private PddDdkGoodsSearchRequest getSearchRequest() {
    		PddDdkGoodsSearchRequest request = new PddDdkGoodsSearchRequest();
    		String listId = IdGen.uuid();//设置一个列表ID，如果有翻页需要根据该ID进行
    		request.setListId(listId);
		request.setPage(1);//默认取第一页
		request.setPageSize(pageSize);//默认每页100条
//        List<Integer> activityTags = new ArrayList<Integer>();
//        activityTags.add(0);
//        request.setActivityTags(activityTags);
//        List<Integer> blockCats = new ArrayList<Integer>();
//        blockCats.add(0);
//        request.setBlockCats(blockCats);
//        List<Integer> blockCatPackages = new ArrayList<Integer>();
//        blockCatPackages.add(0);
//        request.setBlockCatPackages(blockCatPackages);
//        request.setCatId(0L);
//        request.setCustomParameters("str");
//        request.setForceAuthDuoId(false);
//        List<String> goodsSignList = new ArrayList<String>();
//        goodsSignList.add("str");
//        request.setGoodsSignList(goodsSignList);
//        request.setIsBrandGoods(false);
//        request.setKeyword("str");
//        request.setMerchantType(0);
//        List<Integer> merchantTypeList = new ArrayList<Integer>();
//        merchantTypeList.add(0);
//        request.setMerchantTypeList(merchantTypeList);
//        request.setOptId(0L);
//        request.setPid("str");
//        List<RangeListItem> rangeList = new ArrayList<RangeListItem>();
//
//        RangeListItem item = new RangeListItem();
//        item.setRangeFrom(0L);
//        item.setRangeId(0);
//        item.setRangeTo(0L);
//        rangeList.add(item);
//        request.setRangeList(rangeList);
//        request.setSortType(0);
//        request.setWithCoupon(false);
        
        return request;
    }

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，查询Arangodb中(status==null || status.sync==null) and (source=="pdd")的记录，限制30条。
	 * 2，通过拼多多API接口查询生成商品导购链接
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		logger.info("Pinduoduo cps item search job start. " + new Date());

        //TODO 准备查询条件，根据推广活动或选品规则准备查询条件。每次可以准备一个或多个搜索条件
    		PddDdkGoodsSearchRequest request = getSearchRequest();
    		
    		//准备连接
    		arangoClient = new ArangoDbClient(host,port,username,password,database);
        
    		//执行搜索并更新CPS链接
    		try {
    			//Step1:搜索得到推荐商品列表
	    		GoodsSearchResponse resp = pddHelper.searchCpsItems(request);
	    		for(GoodsSearchResponseGoodsListItem item:resp.getGoodsList()) {
	    			upsertItem(item);//逐个组织写入ArangoDB
	    		}
	    		//判断有无分页，如果有则逐页获取
	    		totalAmount = resp.getTotalCount();
	    		int totalPages = (totalAmount + pageSize -1)/pageSize;

	    		for(int i=2;i<totalPages+1;i++) {
	    			System.err.println("start process search result by page.[pageNo]"+i+"/"+totalPages);
	    			request.setPage(i);
	    			resp = pddHelper.searchCpsItems(request);
	        		for(GoodsSearchResponseGoodsListItem item:resp.getGoodsList()) {
	        			upsertItem(item);//逐个组织写入ArangoDB
	        		}
	    		}
	    		
	    		//Step2：获取商品链接 
	    		//注意，这一步不需要另外处理，通过PddItemSync任务自动完成
    		}catch(Exception ex) {
    			logger.error("failed search pdd cps items.",ex);
    		}

		//完成后关闭arangoDbClient
		arangoClient.close();
		if(totalAmount == 0)//啥活都没干，发啥消息
			return;
		//发送处理结果到管理员
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", "o8HmJ1EdIUR8iZRwaq1T7D_nPIYc");//固定发送
		msg.put("title", "导购数据同步任务结果");
		msg.put("task", "拼多多商品入库 已同步");
		msg.put("time", fmt.format(new Date()));
		msg.put("remark", "预期数量："+totalAmount
				+ "\n实际数量："+processedAmount
				+ "\n数量差异："+(totalAmount-processedAmount));
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
