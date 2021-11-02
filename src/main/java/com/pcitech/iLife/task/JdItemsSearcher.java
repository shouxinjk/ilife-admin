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
import com.jd.open.api.sdk.domain.kplunion.GoodsService.request.query.JFGoodsReq;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.CharacteristicServiceInfo;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.JFGoodsResp;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.JingfenQueryResult;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.UrlInfo;
import com.jd.open.api.sdk.internal.JSON.JSON;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.utils.IdGen;
import com.pcitech.iLife.cps.JdHelper;
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
public class JdItemsSearcher {
    private static Logger logger = LoggerFactory.getLogger(JdItemsSearcher.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    JdHelper jdHelper;
    
    //默认设置
    int pageSize = 50;//每页数量，默认20，上限50，建议20
    String urlPrefix = "https://";//https://item.jd.com/26898778009.html
    
    // 记录处理条数
    long totalAmount = 0;
    long processedAmount = 0;
    Map<String,Long> processedMap = null;
    Map<String,Long> totalMap = null;
    Map<String,String> poolNameMap = null;
    
    String brokerId = "system";//默认设置为default
    
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;

    public JdItemsSearcher() {
    	nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
    }
    
    private void upsertItem(String poolName,JFGoodsResp item) {
		String  url = urlPrefix+item.getMaterialUrl();//得到唯一URL
		String itemKey = Util.md5(url);//根据URL生成唯一key
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		doc.getProperties().put("url", url);
		Map<String,Object> seller = new HashMap<String,Object>();
		seller.put("name", item.getShopInfo().getShopName());
		doc.getProperties().put("seller", seller);	
		Map<String,Object> distributor = new HashMap<String,Object>();
		distributor.put("name", "京东");
		doc.getProperties().put("distributor", distributor);	
		Map<String,Object> link = new HashMap<String,Object>();
		link.put("web", url);
		link.put("wap", url);//移动端URL保持与web一致
		if(item.getCouponInfo()!=null && item.getCouponInfo().getCouponList().length>0) {//取第一个优惠券作为领券链接
			link.put("coupon",  item.getCouponInfo().getCouponList()[0].getLink());
		}
		//注意：此接口返回值内没有导购链接，需要二次调用生成补充
		doc.getProperties().put("link", link);		
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-jdItemsSearcher");
		task.put("executor", "robot-jdItemsSearcher-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		doc.getProperties().put("task", task);
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "jd");

		doc.getProperties().put("title", item.getSkuName());//更新title
		
		List<String> tags = new ArrayList<String>();
		//将放心购标签作为商品标签 
		if(item.getSkuLabelInfo()!=null && item.getSkuLabelInfo().getFxgServiceList()!=null) {
			for(CharacteristicServiceInfo label:item.getSkuLabelInfo().getFxgServiceList())
				tags.add(label.getServiceName());
		}
		doc.getProperties().put("tags", tags);
		
		//更新品牌信息到prop列表
		Map<String,String> props = new HashMap<String,String>();
		props.put("品牌", item.getBrandName());//增加品牌属性
		doc.getProperties().put("props", props);
		
		//更新图片列表：注意脚本中已经有采集，此处使用自带的内容
		List<String> images = new ArrayList<String>();
		for(UrlInfo img:item.getImageInfo().getImageList())//增加展示图片
			images.add(img.getUrl());
		doc.getProperties().put("images", images);
		
		//将第一张展示图片作为logo
		doc.getProperties().put("logo", item.getImageInfo().getWhiteImage());
//		doc.getProperties().put("logo", item.getImageInfo().getImageList()[0].getUrl());//图片列表内第一张为主图，作为logo
		
		//如果有documentInfo，则作为summary
		if(item.getDocumentInfo()!=null && item.getDocumentInfo().getDocument().trim().length()>0)
			doc.getProperties().put("summary", item.getDocumentInfo().getDocument().replaceAll("\\s+"," "));
		
		//增加类目
		List<String> categories = new ArrayList<String>();
		categories.add(item.getCategoryInfo().getCid1Name());
		categories.add(item.getCategoryInfo().getCid2Name());
		categories.add(item.getCategoryInfo().getCid3Name());
		doc.getProperties().put("category", categories);//更新类目，包含多级分类

		//更新价格：直接覆盖
		Map<String,Object> price = new HashMap<String,Object>();
		price.put("currency", "￥");
		price.put("bid", parseNumber(item.getPriceInfo().getPrice()));
		price.put("sale", parseNumber(item.getPriceInfo().getLowestPrice()));
		if(item.getCouponInfo()!=null && item.getCouponInfo().getCouponList().length>0) {//取第一个优惠金额
			price.put("coupon",  parseNumber(item.getCouponInfo().getCouponList()[0].getDiscount()));
		}
		doc.getProperties().put("price", price);
		
		//更新佣金：直接覆盖
		Map<String,Object> profit = new HashMap<String,Object>();
		profit.put("rate", parseNumber(item.getCommissionInfo().getCommissionShare()));//返回的是百分比，直接使用即可
		profit.put("amount", parseNumber(item.getCommissionInfo().getCommission()));
		profit.put("type", "2-party");
		doc.getProperties().put("profit", profit);
		
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
		logger.debug("try to upsert jd item.[itemKey]"+itemKey,JSON.toString(doc));
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
    		logger.info("JD cps item search job start. " + new Date());
    		processedMap = new HashMap<String,Long>();
    		totalMap = new HashMap<String,Long>();
    		poolNameMap = new HashMap<String,String>();

        //准备查询条件，除1001-选品库外，每一种分别查询
    		//频道ID:1-好券商品,2-精选卖场,10-9.9包邮,15-京东配送,22-实时热销榜,23-为你推荐,24-数码家电,25-超市,26-母婴玩具,27-家具日用,28-美妆穿搭,30-图书文具,31-今日必推,32-京东好物,33-京东秒杀,34-拼购商品,40-高收益榜,41-自营热卖榜,108-秒杀进行中,109-新品首发,110-自营,112-京东爆品,125-首购商品,129-高佣榜单,130-视频商品,153-历史最低价商品榜，210-极速版商品，238-新人价商品，247-京喜9.9，249-京喜秒杀，1000-招商团长，1001-选品库
    		String poolNameStr = "1-好券商品,2-精选卖场,10-9.9包邮,15-京东配送,22-实时热销榜,23-为你推荐,24-数码家电,25-超市,26-母婴玩具,27-家具日用,28-美妆穿搭,30-图书文具,31-今日必推,32-京东好物,33-京东秒杀,34-拼购商品,40-高收益榜,41-自营热卖榜,108-秒杀进行中,109-新品首发,110-自营,112-京东爆品,125-首购商品,129-高佣榜单,130-视频商品,153-历史最低价商品榜，210-极速版商品，238-新人价商品，247-京喜9.9，249-京喜秒杀，1000-招商团长";
    		String[] poolNameArray = poolNameStr.split(",");
    		poolNameStr = poolNameStr.replaceAll("\\d+\\-", "");
    		String[] poolNames = poolNameStr.split(",");
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
    			processedMap.put(poolName, 0L);//默认设置相应分类的条数为0
    			JFGoodsReq request = new JFGoodsReq();
    			request.setEliteId(poolNameIndex++);//传递下标
    			request.setPageIndex(1);//默认从第一页开始:下标从1开始
    			request.setPageSize(pageSize);
    			request.setSortName("commissionShare");//排序字段(price：单价, commissionShare：佣金比例, commission：佣金， inOrderCount30DaysSku：sku维度30天引单量，comments：评论数，goodComments：好评数)
    			request.setSort("desc");//asc,desc升降序,默认降序
    			request.setPid(Global.getConfig("jd.pid"));//联盟id_应用id_推广位id，三段式
    			request.setFields("hotWords,documentInfo,skuLabelInfo,promotionLabelInfo");//支持出参数据筛选，逗号','分隔，目前可用：videoInfo(视频信息),hotWords(热词),similar(相似推荐商品),documentInfo(段子信息)，skuLabelInfo（商品标签），promotionLabelInfo（商品促销标签）
    			
    			//Step1:搜索得到推荐商品列表
    			logger.debug("try to search items.[request]"+JsonUtil.transferToJson(request));
    			try {
	    			JingfenQueryResult resp = jdHelper.search(request);
	    			JFGoodsResp[] jfgoods = resp.getData();
	    			for(JFGoodsResp jfgood:jfgoods) {
	    				upsertItem(poolName,jfgood);
	    			}
	    			//检查分页，如果有分页则循环遍历
				long totalAmountPerPool = resp.getTotalCount();
				totalMap.put(poolName,totalAmountPerPool );//默认设置相应分类的总条数
		    		totalAmount += totalAmountPerPool;
		    		long totalPages = (totalAmountPerPool + pageSize -1)/pageSize;
		    		for(int i=2;i<totalPages+1;i++) {//如果有分页则逐页获取
		    			logger.debug("start process search result by page.[pageNo]"+i+"/"+totalPages);
		    			request.setPageIndex(i);
		    			resp = jdHelper.search(request);
		    			jfgoods = resp.getData();
		    			for(JFGoodsResp jfgood:jfgoods) {
		    				upsertItem(poolName,jfgood);
		    			}
		    		}
	    		}catch(Exception ex) {
	    			
	    		}
    		}
		//完成后关闭arangoDbClient
		arangoClient.close();
		if(totalAmount == 0)//啥活都没干，发啥消息
			return;
		//组装通知信息
		StringBuffer remark = new StringBuffer();
		remark.append("预期数量："+totalAmount);
		for(Map.Entry<String, Long> entry:processedMap.entrySet()) {
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
		msg.put("task", "京东商品入库 已同步");
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
