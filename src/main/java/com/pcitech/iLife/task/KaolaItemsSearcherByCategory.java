package com.pcitech.iLife.task;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.pcitech.iLife.cps.KaolaHelper;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.cps.kaola.CategoryInfo;
import com.pcitech.iLife.cps.kaola.GoodInfo;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.cps.kaola.QueryRecommendGoodsListRequest;
import com.pcitech.iLife.cps.kaola.QueryRecommendGoodsListResponse;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsRequest;
import com.pcitech.iLife.cps.kaola.QuerySelectedGoodsResponse;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
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
 * 通过自动任务触发，每天执行一次即可。按照类目遍历商品
 * 
 */
@Service
public class KaolaItemsSearcherByCategory extends ItemSearcherBase {
    private static Logger logger = LoggerFactory.getLogger(KaolaItemsSearcherByCategory.class);

    @Autowired
    KaolaHelper kaolaHelper;
    
    //默认设置
    int pageSize = 200;//最大单页200条
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    Map<String,Integer> processedMap = null;
    Map<String,Integer> totalMap = null;
    Map<String,String> poolNameMap = null;
    
    String brokerId = "system";//默认设置为default
    
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;

    public KaolaItemsSearcherByCategory() {
    		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
    }
    
    private void upsertItem(String categoryName,GoodInfo item) {
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
		doc.getProperties().put("category", categoryName);

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

		//检查类目映射
		boolean categoryMapped = false;
		if(categories.size()>0) {
			String category = categories.get(categories.size()-1);//取最后一级
			PlatformCategory query = new PlatformCategory();
			query.setName(category);
			query.setPlatform("kaola");
			List<PlatformCategory> list = platformCategoryService.findMapping(query);
			if(list.size()>0) {//有则更新
				Map<String,Object> meta = new HashMap<String,Object>();
				meta.put("category", list.get(0).getCategory().getId());
				meta.put("categoryName", list.get(0).getCategory().getName());
				doc.getProperties().put("meta", meta);	
				categoryMapped = true;
			}else {//否则写入等待标注
				query.setIsNewRecord(true);
				query.setId(Util.md5("kaola"+category));//采用手动生成ID，避免多次查询生成多条记录
				query.setPlatform("kaola");
				query.setCreateDate(new Date());
				query.setUpdateDate(new Date());
				platformCategoryService.save(query);
				//检查是否支持无类目映射入库
				if(!"true".equalsIgnoreCase(Global.getConfig("sx.enhouseWithoutCategoryMapping"))) {
					return;
				}
			}
		}
		
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
			processedMap.put(categoryName, processedMap.get(categoryName)+1);
			processedAmount++;
		}
		
		//直接提交到kafka：仅在有类目的情况下推送，便于立即measure
		if(categoryMapped) {
			Map<String,Object> jsonDoc = doc.getProperties();
			jsonDoc.put("_key", itemKey);
			kafkaStuffLogger.info(new Gson().toJson(jsonDoc));
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
    		Map<Long,String> categoryMap = Maps.newHashMap();
    		categoryMap.put(110022L,"宠物");
    		categoryMap.put(9691L,"服装鞋靴");
    		categoryMap.put(381L,"个人洗护");
    		categoryMap.put(836L,"环球美食");
    		categoryMap.put(372L,"家居生活");
    		categoryMap.put(437L,"美容彩妆");
    		categoryMap.put(438L,"母婴");
    		categoryMap.put(8096L,"汽车用品");
    		categoryMap.put(8115L,"生鲜");
    		categoryMap.put(1092L,"手表配饰");
    		categoryMap.put(440L,"数码家电");
    		categoryMap.put(1025L,"箱包");
    		categoryMap.put(837L,"医药健康");
    		categoryMap.put(7578L,"运动户外");
    		
    		//准备连接
    		arangoClient = new ArangoDbClient(host,port,username,password,database);
    		Iterator<Entry<Long,String>> iter = categoryMap.entrySet().iterator();
    		while(iter.hasNext()) {
    			Entry<Long,String> entry = iter.next();
    			String categoryName = entry.getValue();
    			logger.debug("start search category.[categoryName]"+categoryName);
    			processedMap.put(categoryName, 0);//默认设置相应分类的条数为0
    			QueryRecommendGoodsListRequest request = new QueryRecommendGoodsListRequest();
    			request.setCategoryId(entry.getKey());//根据类目查询
    			request.setSortType(1);//1:按佣金比例排序 默认 2:按佣金排序 3:按黑卡价格排序 4:按普通价格排序 5:按30天黑卡用户销量排序 6:按黑卡价差进行排序
    			request.setDesc(0);//0 倒序 默认 1 升序 
    			request.setPageIndex(1);//页码：默认仅取一页
    			request.setPageSize(pageSize);//每页条数：默认取200条：最小20，最大1000
    			request.setSelf(0);//是否自营：0 全部 1 自营 2 非自营
    			//Step1:搜索得到推荐商品列表
    			logger.debug("try to search items.[request]"+JsonUtil.transferToJson(request));
    			QueryRecommendGoodsListResponse resp = kaolaHelper.search(request);
    			List<Long> ids = resp.getData();//返回商品ID列表
    			int totalAmountPerPool = ids.size();//列表长度就是结果数
    			totalMap.put(categoryName,totalAmountPerPool );//默认设置相应分类的总条数
    	    		totalAmount += totalAmountPerPool;
    	    		int totalPages = (totalAmountPerPool + pageSize -1)/pageSize;
    	    		for(int i=2;i<totalPages+1;i++) {//如果有分页则逐页获取：当前预留
    	    			logger.debug("start process search result by page.[pageNo]"+i+"/"+totalPages);
    	    			request.setPageIndex(i);
    	    			resp = kaolaHelper.search(request);
    	    			ids.addAll(resp.getData());
    	    		}
        			totalMap.put(categoryName, ids.size());//理论上由于已经返回了总条数，不需要重新设置。不知道分页做得对不对啊，还是掰手指头数一数，哎，多么不自信~~

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
						upsertItem(categoryName,item);
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
		msg.put("openid", Global.getConfig("default_tech_guy_openid"));//固定发送
		msg.put("title", "导购数据同步任务结果");
		msg.put("task", "考拉商品按品类入库 已同步");
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
