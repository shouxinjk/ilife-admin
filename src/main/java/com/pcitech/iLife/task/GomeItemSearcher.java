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
import java.util.TreeMap;
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
import com.pcitech.iLife.cps.GomeHelper;
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
import com.pcitech.iLife.util.NumberUtil;
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
public class GomeItemSearcher {
    private static Logger logger = LoggerFactory.getLogger(GomeItemSearcher.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    GomeHelper gomeHelper;
    
    //默认设置
    int pageSize = 100;//最大单页200条
    String urlPrefix = "https://jinbao.pinduoduo.com/goods-detail?s=";//https://jinbao.pinduoduo.com/goods-detail?s=Y9X2m1wSlN1U8LcVwvfZHeaZwozbWFjf_JQvP59gKL7
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    Map<String,Integer> processedMap = null;
    Map<String,Integer> totalMap = null;
    Map<String,String> poolNameMap = null;
    
    String brokerId = "default";//默认设置为default
    
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;

    public GomeItemSearcher() {
    		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
    }
    
    private void upsertItem(String poolName,JSONObject item) {
		String  url = item.getString("product_url");//得到唯一URL
		String itemKey = Util.md5(url);//根据URL生成唯一key
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		doc.getProperties().put("url", url);
		Map<String,Object> distributor = new HashMap<String,Object>();
		distributor.put("name", "国美");
		doc.getProperties().put("distributor", distributor);	
		Map<String,Object> link = new HashMap<String,Object>();
		link.put("web", url);
		link.put("wap", item.getString("product_url_wap"));
		doc.getProperties().put("link", link);		
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-gomeItemsSearcher");
		task.put("executor", "robot-gomeItemsSearcher-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		doc.getProperties().put("task", task);
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "gome");

		doc.getProperties().put("title", item.getString("sku_name"));//更新title
		
		//更新品牌信息到prop列表
		if(item.getString("brand")!=null) {
			Map<String,String> props = new HashMap<String,String>();
			props.put("品牌", item.getString("brand"));//增加品牌属性
			doc.getProperties().put("props", props);
		}
		
		//更新图片列表：只有一张
		List<String> images = new ArrayList<String>();
		images.add(item.getString("picture_url"));
		doc.getProperties().put("images", images);
		
		//将第一张展示图片作为logo
		doc.getProperties().put("logo", item.getString("picture_url"));
		
		//设置summary
		doc.getProperties().put("summary", item.getString("service_desc"));
		
		//增加类目
//		List<String> categories = new ArrayList<String>();
//		for(CategoryInfo category:item.getCategoryInfo()){//增加类目
//			logger.debug("[category name]"+category.getCategoryName());
//			categories.add(category.getCategoryName());
//		}
//		doc.getProperties().put("category", categories);//更新类目，包含多级分类

		//更新CPS链接：在link基础上补充
//		link.put("wap2", item.getLinkInfo().getShortShareUrl());
//		link.put("web2", item.getLinkInfo().getShortShareUrl());
//		link.put("miniprog", item.getLinkInfo().getMiniShareUrl());
//		doc.getProperties().put("link", link);

		//更新价格：直接覆盖
		Map<String,Object> price = new HashMap<String,Object>();
		price.put("currency", "￥");
		price.put("bid", NumberUtil.getInstance().parseNumber(item.getString("list_price")));
		price.put("sale",  NumberUtil.getInstance().parseNumber(item.getString("sale_price")));
		doc.getProperties().put("price", price);
		
		//更新佣金：直接覆盖
//		Map<String,Object> profit = new HashMap<String,Object>();
//		profit.put("rate", parseNumber(item.getCommissionInfo().getCommissionRate().doubleValue()));//返回的是百分比，直接使用即可
//		profit.put("amount", parseNumber(item.getPriceInfo().getCurrentPrice().multiply(item.getCommissionInfo().getCommissionRate()).doubleValue()));
//		profit.put("type", "2-party");
//		doc.getProperties().put("profit", profit);
		
		//设置基本信息更新标志为true
//		Map<String,Object> syncStatus = new HashMap<String,Object>();
//		syncStatus.put("sync", true);
//		Map<String,Object> syncTimestamp = new HashMap<String,Object>();
//		syncTimestamp.put("sync", new Date());	
//		doc.getProperties().put("status", syncStatus);
//		doc.getProperties().put("timestamp", syncTimestamp);

		//更新doc
		logger.debug("try to upsert gome item.[itemKey]"+itemKey,JSON.toString(doc));
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

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，查询Arangodb中(status==null || status.sync==null) and (source=="pdd")的记录，限制30条。
	 * 2，通过拼多多API接口查询生成商品导购链接
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		logger.info("Gomme cps item search job start. " + new Date());
    		processedMap = new HashMap<String,Integer>();
    		totalMap = new HashMap<String,Integer>();
    		poolNameMap = new HashMap<String,String>();

        //TODO 准备查询条件。预留，可以根据类目逐个查询，不需要获取数据库全部商品
    		String[] poolNames = {"所有类目"};
    		
    		//准备连接
    		arangoClient = new ArangoDbClient(host,port,username,password,database);
    		/**
    		//查询得到所有3级叶子分类，然后逐个遍历：当前有问题，gome API限制调用次数，需要调用侧处理API分时调用问题
    		List<BaseDocument> categories = new ArrayList<BaseDocument>();
        String query = "for doc in platform_categories filter "
        		+ "doc.source == \"gome\" and doc.level ==3  "
        		+ "return {id:doc.id,name:doc.name}";
        
        try {
            arangoClient = new ArangoDbClient(host,port,username,password,database);
            categories = arangoClient.query(query, null, BaseDocument.class);
            totalAmount = categories.size();
            if(totalAmount ==0) {//如果没有类目则表示出错了，提前收工吧
	            	logger.debug("查询gome类目失败");
	            	arangoClient.close();//链接还是要关闭的
	            	return;
            }
        }catch(Exception ex) {
        		logger.error("failed query gome categories.",ex);
        }
    		//**/
        int poolNameIndex = 1;
//		for(BaseDocument category:categories) {//逐个分类查询，每个分类均进行遍历
//			String poolName = category.getProperties().get("name").toString();
		for(String poolName:poolNames) {//逐个分类查询，每个分类均进行遍历
			logger.debug("start search by poolName.[poolName]"+poolName);
			totalMap.put(poolName, 0);//初始化各个分类的总数量，后面查询时累计
			processedMap.put(poolName, 0);//默认设置相应分类的条数为0
    			
    			TreeMap<String,String> request = gomeHelper.getParamMap();
//    			request.put("category_id", category.getProperties().get("id").toString());
    			request.put("page_no", "1");//分页：默认第1页
    			request.put("page_size", ""+pageSize);//每页条数：最大10000
    			int pageNo = 1;
    			boolean hasNextPage = true;
    			while(hasNextPage) {//至少会有一页，先查查看
    				logger.debug("start process search result by page.[pageNo]"+pageNo);
    				request.put("page_no", ""+pageNo);//分页：默认第1页
    				pageNo++;
	    			logger.debug("try to search items.[request]"+JsonUtil.transferToJson(request));
	    			JSONObject resp = gomeHelper.getIncreasedItems(request);//增量查询竟然不好使
//	    			JSONObject resp = gomeHelper.getAllItems(request);//全量很傻，竟然一次性丢过来90万条数据，需要按分类取
				int totalAmountPerPool = resp.getIntValue("total_count");
				totalMap.put(poolName,totalAmountPerPool );//默认设置相应分类的总条数
		    		totalAmount += totalAmountPerPool;
		    		int totalPages = (totalAmountPerPool + pageSize -1)/pageSize;
		    		if(pageNo>totalPages)
		    			hasNextPage = false;
		    		//逐条处理
	    			logger.debug("try to deal with item detail.[resp]"+JsonUtil.transferToJson(resp));
	    			JSONArray itemArray = resp.getJSONArray("items");
				for(int j=0;j<itemArray.size();j++) {//逐个插入
					JSONObject item = itemArray.getJSONObject(j);
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
		msg.put("task", "国美商品入库 已同步");
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
