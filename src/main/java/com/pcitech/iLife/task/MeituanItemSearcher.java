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
import com.pcitech.iLife.cps.MeituanHelper;
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
public class MeituanItemSearcher {
    private static Logger logger = LoggerFactory.getLogger(MeituanItemSearcher.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    MeituanHelper meituanHelper;
    
    //默认设置
    int pageSize = 100;//最大单页200条

    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    Map<String,Integer> processedMap = null;
    Map<String,Integer> totalMap = null;
    Map<String,String> poolNameMap = null;
    
    String brokerId = "system";//默认设置为default
    
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;

    public MeituanItemSearcher() {
    		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
    }
    
    private void upsertItem(String poolName,JSONObject item) {
    	logger.debug("try to upsert "+poolName+" item.",item);
		String  url = item.getString("itemDeepLinkUrl");//得到唯一URL：是deep link
		String itemKey = Util.md5(url);//根据URL生成唯一key
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		doc.getProperties().put("url", url);
		Map<String,Object> distributor = new HashMap<String,Object>();
		distributor.put("name", "美团");
		doc.getProperties().put("distributor", distributor);	
		Map<String,Object> link = new HashMap<String,Object>();
		link.put("web", url);
		link.put("wap", item.getString("itemMiddlePageLinkUrl"));
		doc.getProperties().put("link", link);		
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-meituanItemsSearcher");
		task.put("executor", "robot-meituanItemsSearcher-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		doc.getProperties().put("task", task);
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "meituan");

		doc.getProperties().put("title", item.getString("title"));//更新title
		
		//更新图片列表
		JSONArray imgArray = item.getJSONArray("smallImages");
		List<String> images = new ArrayList<String>();
		for(int i=0;i<imgArray.size();i++)
			images.add(imgArray.getString(i));
		doc.getProperties().put("images", images);
		
		//更新logo
		doc.getProperties().put("logo", item.getString("pictUrl"));
		
		//设置summary
		//doc.getProperties().put("summary", item.getString("service_desc"));
		
		//增加类目
//		List<String> categories = new ArrayList<String>();
//		for(CategoryInfo category:item.getCategoryInfo()){//增加类目
//			logger.debug("[category name]"+category.getCategoryName());
//			categories.add(category.getCategoryName());
//		}
//		doc.getProperties().put("category", categories);//更新类目，包含多级分类

		//更新CPS链接：在link基础上补充
		link.put("web2", item.getString("itemDeepLinkUrl"));
		link.put("wap2", item.getString("itemMiddlePageLinkUrl"));
		link.put("miniprog", item.getString("itemWXLinkUrl"));
		doc.getProperties().put("link", link);

		//更新价格：直接覆盖
		Map<String,Object> price = new HashMap<String,Object>();
		price.put("currency", "￥");
		price.put("bid", NumberUtil.getInstance().parseNumber(item.getString("originPrice")));
		price.put("sale",  NumberUtil.getInstance().parseNumber(item.getString("promotionPrice")));
		doc.getProperties().put("price", price);
		
		//更新佣金：不处理，等待monetize任务自动完成
//		Map<String,Object> profit = new HashMap<String,Object>();
//		profit.put("rate", parseNumber(item.getCommissionInfo().getCommissionRate().doubleValue()));//返回的是百分比，直接使用即可
//		profit.put("amount", parseNumber(item.getPriceInfo().getCurrentPrice().multiply(item.getCommissionInfo().getCommissionRate()).doubleValue()));
//		profit.put("type", "2-party");
//		doc.getProperties().put("profit", profit);
		
		//设置基本信息更新标志为true
		//设置状态。注意，需要设置sync=pending 等待计算CPS链接
		//状态更新
		Map<String,Object> status = new HashMap<String,Object>();
		status.put("crawl", "ready");
		status.put("sync", "ready");//CPS链接已经获取了，不用再次重新生成
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
		logger.debug("try to upsert meituan item.[itemKey]"+itemKey,JSON.toString(doc));
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
     */
    public void execute() throws JobExecutionException {
    		logger.info("Meituan cps item search job start. " + new Date());
    		processedMap = new HashMap<String,Integer>();
    		totalMap = new HashMap<String,Integer>();
    		poolNameMap = new HashMap<String,String>();

    		//TODO 准备查询条件。预留，可以根据类目逐个查询，当前仅优选
    		String[] poolNames = {"优选"};
    		String[] poolIds = {"6"};
    		
    		int pageSize = 100;//默认每页返回100条
    		
    		String longitude = "114.42916810517";
    		String latitude = "30.4561282109";
    		
    		//准备连接
    		arangoClient = new ArangoDbClient(host,port,username,password,database);
	        int poolNameIndex = 0;
			for(String poolName:poolNames) {//逐个分类查询，每个分类均进行遍历
				logger.debug("start search by poolName.[poolName]"+poolName);
				totalMap.put(poolName, 0);//初始化各个分类的总数量，后面查询时累计
				processedMap.put(poolName, 0);//默认设置相应分类的条数为0
    			
    			int pageNo = 1;
    			boolean hasNextPage = true;
    			while(hasNextPage) {//至少会有一页，先查查看
    				logger.debug("start process search result by page.[pageNo]"+pageNo);
	    			JSONObject resp = meituanHelper.search(poolIds[poolNameIndex], pageSize, pageNo, longitude, latitude);
	    			JSONObject data = resp.getJSONObject("data");
					int totalAmountPerPool = data.getIntValue("total");
					totalMap.put(poolName,totalAmountPerPool );//默认设置相应分类的总条数
		    		totalAmount += totalAmountPerPool;
		    		int totalPages = (totalAmountPerPool + pageSize -1)/pageSize;
		    		if(pageNo>totalPages)
		    			hasNextPage = false;
		    		//逐条处理
	    			logger.debug("try to deal with item detail.[resp]"+JsonUtil.transferToJson(data));
	    			JSONArray itemArray = data.getJSONArray("skuList");
					for(int j=0;j<itemArray.size();j++) {//逐个插入
						JSONObject item = itemArray.getJSONObject(j);
						logger.debug(JsonUtil.transferToJson(item));
						upsertItem(poolName,item);
					}
					//自动翻页
					pageNo++;
    			}
    			poolNameIndex++;//多个分类遍历
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
		msg.put("task", "美团商品入库 已同步");
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
