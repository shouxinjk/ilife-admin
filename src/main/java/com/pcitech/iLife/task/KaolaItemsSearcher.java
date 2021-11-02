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
import com.google.gson.Gson;
import com.jd.open.api.sdk.internal.JSON.JSON;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.utils.IdGen;
import com.pcitech.iLife.cps.KaolaHelper;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.cps.kaola.CategoryInfo;
import com.pcitech.iLife.cps.kaola.GoodInfo;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsRequest;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsResponse;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
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
public class KaolaItemsSearcher {
    private static Logger logger = LoggerFactory.getLogger(KaolaItemsSearcher.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    KaolaHelper kaolaHelper;
    
    //默认设置
    int pageSize = 200;//最大单页200条
    String urlPrefix = "https://jinbao.pinduoduo.com/goods-detail?s=";//https://jinbao.pinduoduo.com/goods-detail?s=Y9X2m1wSlN1U8LcVwvfZHeaZwozbWFjf_JQvP59gKL7
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    Map<String,Integer> processedMap = null;
    Map<String,Integer> totalMap = null;
    Map<String,String> poolNameMap = null;
    
    String brokerId = "system";//默认设置为default
    
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;

    public KaolaItemsSearcher() {
    		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
    }
    
    private void upsertItem(String poolName,GoodInfo item) {
		String  url = item.getLinkInfo().getGoodsPCUrl();//得到唯一URL
		String itemKey = Util.md5(url);//根据URL生成唯一key
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		doc.getProperties().put("url", url);
		Map<String,Object> distributor = new HashMap<String,Object>();
		distributor.put("name", "网易考拉");
		doc.getProperties().put("distributor", distributor);	
		Map<String,Object> link = new HashMap<String,Object>();
		link.put("web", url);
		link.put("wap", item.getLinkInfo().getGoodsDetailUrl());//移动端URL
		doc.getProperties().put("link", link);		
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-kaolaItemsSearcher");
		task.put("executor", "robot-kaolaItemsSearcher-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		doc.getProperties().put("task", task);
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "kaola");

		doc.getProperties().put("title", item.getBaseInfo().getGoodsTitle());//更新title
		
		//更新品牌信息到prop列表
		Map<String,String> props = new HashMap<String,String>();
		props.put("品牌", item.getBaseInfo().getBrandName());//增加品牌属性
		props.put("品牌国家", item.getBaseInfo().getBrandCountryName());//增加品牌国家属性
		doc.getProperties().put("props", props);
		
		//更新图片列表：注意脚本中已经有采集，此处使用自带的内容
		List<String> images = new ArrayList<String>();
		for(String img:item.getBaseInfo().getImageList())//增加展示图片
			images.add(img);
		for(String img:item.getBaseInfo().getDetailImgList())//增加详情图片
			images.add(img);
		doc.getProperties().put("images", images);
		
		//将第一张展示图片作为logo
		doc.getProperties().put("logo", item.getBaseInfo().getImageList().get(0));
		
		//如果有subtitle，则作为summary
		if(item.getBaseInfo().getGoodsSubTitle()!=null && item.getBaseInfo().getGoodsSubTitle().trim().length()>0)
			doc.getProperties().put("summary", item.getBaseInfo().getGoodsSubTitle().replaceAll("\\s+"," "));
		
		//增加类目
		List<String> categories = new ArrayList<String>();
		for(CategoryInfo category:item.getCategoryInfo()){//增加类目
			logger.debug("[category name]"+category.getCategoryName());
			categories.add(category.getCategoryName());
		}
		doc.getProperties().put("category", categories);//更新类目，包含多级分类

		//更新CPS链接：在link基础上补充
		link.put("wap2", item.getLinkInfo().getShortShareUrl());
		link.put("web2", item.getLinkInfo().getShortShareUrl());
		link.put("miniprog", item.getLinkInfo().getMiniShareUrl());
		doc.getProperties().put("link", link);

		//更新价格：直接覆盖
		Map<String,Object> price = new HashMap<String,Object>();
		price.put("currency", "￥");
		price.put("bid", parseNumber(item.getPriceInfo().getMarketPrice().doubleValue()));
		price.put("sale", parseNumber(item.getPriceInfo().getCurrentPrice().doubleValue()));
		doc.getProperties().put("price", price);
		
		//更新佣金：直接覆盖
		Map<String,Object> profit = new HashMap<String,Object>();
		profit.put("rate", parseNumber(item.getCommissionInfo().getCommissionRate().doubleValue()));//返回的是百分比，直接使用即可
		profit.put("amount", parseNumber(item.getPriceInfo().getCurrentPrice().multiply(item.getCommissionInfo().getCommissionRate()).doubleValue()));
		profit.put("type", "2-party");
		doc.getProperties().put("profit", profit);
		
		//设置状态。注意，需要设置sync=pending 等待计算CPS链接
		//状态更新
		Map<String,Object> status = new HashMap<String,Object>();
		status.put("crawl", "ready");
		status.put("sync", "ready");//CPS链接已经包含，直接使用
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
		logger.debug("try to upsert kaola item.[itemKey]"+itemKey,JSON.toString(doc));
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
    
    private double parseNumber(double d) {
//    		return Double.valueOf(String.format("%.2f", d ));//会四舍五入，丢弃
    		String numStr = nf.format(d);
    		try {
    			return Double.parseDouble(numStr);
    		}catch(Exception ex) {
    			return d;
    		}
    }

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，查询Arangodb中(status==null || status.sync==null) and (source=="pdd")的记录，限制30条。
	 * 2，通过拼多多API接口查询生成商品导购链接
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		logger.info("Kaola cps item search job start. " + new Date());
    		processedMap = new HashMap<String,Integer>();
    		totalMap = new HashMap<String,Integer>();
    		poolNameMap = new HashMap<String,String>();

        //准备查询条件，根据推广活动或选品规则准备查询条件。按照1-19总共19种
    		String[] poolNames = {
			"每日平价商品",
			"高佣必推商品",
			"新人专享商品",
			"会员专属商品",
			"低价包邮商品",
			"考拉自营爆款",
			"考拉商家爆款",
			"黑卡用户最爱买商品",
			"美妆个护热销品",
			"食品保健热销品",
			"母婴热销品",
			"时尚热销品",
			"家居宠物热销品",
			"每日秒杀商品",
			"黑卡好价商品",
			"高转化好物商品",
			"开卡一单省回商品",
			"会员专属促销选品池",
			"好券商品"
    		};
    		
    		//准备连接
    		arangoClient = new ArangoDbClient(host,port,username,password,database);
        int poolNameIndex = 1;
    		for(String poolName:poolNames) {//逐个分类查询，每个分类均进行遍历
    			logger.debug("start search by poolName.[poolName]"+poolName);
    			processedMap.put(poolName, 0);//默认设置相应分类的条数为0
    			QuerySelectedGoodsRequest request = new QuerySelectedGoodsRequest();
    			request.setPoolName(""+poolNameIndex);
    			poolNameIndex++;
    			request.setPageNo(1);//默认从第一页开始:下标从1开始
    			request.setPageSize(pageSize);//默认每页20条，是该接口的最小值，兼顾查询详情接口数量限制
    			
    			//Step1:搜索得到推荐商品列表
    			logger.debug("try to search items.[request]"+JsonUtil.transferToJson(request));
    			QuerySelectedGoodsResponse resp = kaolaHelper.search(request);
    			List<Long> ids = resp.getData().getGoodsIdList();
    			//检查分页，如果有分页则先全部取回来放到列表里，秋后一起算账
			int totalAmountPerPool = resp.getData().getTotalRecord();
			totalMap.put(poolName,totalAmountPerPool );//默认设置相应分类的总条数
	    		totalAmount += totalAmountPerPool;
	    		int totalPages = (totalAmountPerPool + pageSize -1)/pageSize;
	    		for(int i=2;i<totalPages+1;i++) {//如果有分页则逐页获取
	    			logger.debug("start process search result by page.[pageNo]"+i+"/"+totalPages);
	    			request.setPageNo(i);
	    			resp = kaolaHelper.search(request);
	    			ids.addAll(resp.getData().getGoodsIdList());
	    		}
    			totalMap.put(poolName, ids.size());//理论上由于已经返回了总条数，不需要重新设置。不知道分页做得对不对啊，还是掰手指头数一数，哎，多么不自信~~
    			//由于itemDetail每次限制最大查询20条，这里需要对返回的Id列表进行切割，分别处理
    			int totalIds = ids.size();
    			int subListSize = 20;//每次取20条，批量查询详情
    			int subListNumbers = (totalIds+subListSize-1)/subListSize;//总共分割得到的subList数量
    			for(int k=0;k<subListNumbers;k++) {//针对返回的list进行翻页，每次处理20条，直到结束
        			int subListIndexFrom = k*subListSize;//默认第一个开始取
        			int subListAmount = (totalIds-subListIndexFrom)>subListSize?subListSize:(totalIds-subListIndexFrom);
        			int subListIndexTo = subListIndexFrom+subListAmount-1;
        			List<Long> subIds = ids.subList(subListIndexFrom, subListIndexTo);
	    			//取得对应的20条Id
	    			String skuIds = StringUtils.join(subIds,",");
		    		//查询对应的商品详情
	    			logger.debug("try to get item detail.[subList]"+k+"[from]"+subListIndexFrom+"[to]"+subListIndexTo+"[skuIds]"+skuIds);
	    			GoodsInfoResponse goods = new GoodsInfoResponse();
	    			try{
	    				goods = kaolaHelper.getItemDetail(brokerId, skuIds);
	    			}catch(Exception ex) {
	    				logger.error("failed to get item detail.",ex);
	    			}
					for(GoodInfo item:goods.getData()) {//逐个插入
						logger.debug(JsonUtil.transferToJson(item));
						upsertItem(poolName,item);
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
		msg.put("task", "考拉商品入库 已同步");
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
