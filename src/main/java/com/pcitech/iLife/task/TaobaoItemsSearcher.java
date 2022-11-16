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
import com.pcitech.iLife.cps.TaobaoHelper;
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
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkDgOptimusMaterialResponse;
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
public class TaobaoItemsSearcher extends ItemSearcherBase {
    private static Logger logger = LoggerFactory.getLogger(TaobaoItemsSearcher.class);

    @Autowired
    TaobaoHelper taobaoHelper;

    //默认设置
    int pageSize = 50;//每页数量，默认20，上限50，建议20
    String itemPrefix = "https://detail.tmall.com/item.htm?id=";//默认所有采集数据均以id为唯一识别。注意，该地址不能作为商品跳转地址
    String urlPrefix = "https:"; //返回的url缺少协议头
    
    // 记录处理条数
    long totalAmount = 0;
    long processedAmount = 0;
    Map<String,Long> processedMap = null;
    Map<String,Long> totalMap = null;
    Map<String,String> poolNameMap = null;
    
    String brokerId = "system";//默认设置为default
    
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;

    public TaobaoItemsSearcher() {
    	nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
    }
    
    private void upsertItem(String poolName,TbkDgOptimusMaterialResponse.MapData item) {
		String  url = itemPrefix+item.getItemId();//得到唯一URL
		String itemKey = Util.md5(url);//根据URL生成唯一key
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		doc.getProperties().put("url", url);
		Map<String,Object> seller = new HashMap<String,Object>();
		seller.put("name", item.getShopTitle());
		doc.getProperties().put("seller", seller);	
		Map<String,Object> distributor = new HashMap<String,Object>();
		distributor.put("name", "淘宝");
		doc.getProperties().put("distributor", distributor);	
		Map<String,Object> link = new HashMap<String,Object>();
		link.put("web", urlPrefix+item.getClickUrl());
		link.put("wap", urlPrefix+item.getClickUrl());
		link.put("web2", urlPrefix+item.getClickUrl());
		link.put("wap2", urlPrefix+item.getClickUrl());	
		if(item.getCouponClickUrl()!=null && item.getCouponClickUrl().trim().length()>0) {//取第一个优惠券作为领券链接
			link.put("coupon",  urlPrefix+item.getCouponClickUrl());
		}
		//注意：此接口返回值内没有导购链接，需要二次调用生成补充
		doc.getProperties().put("link", link);		
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-taobaoItemsSearcher");
		task.put("executor", "robot-taobaoItemsSearcher-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		doc.getProperties().put("task", task);
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "taobao");

		doc.getProperties().put("title", item.getTitle());//更新title
		
		List<String> tags = new ArrayList<String>();
		//将放心购标签作为商品标签 
		if(item.getItemDescription()!=null && item.getItemDescription().trim().length()>0) {
			for(String tag: item.getItemDescription().split(" ") )
				if(tag.trim().length()>0)
					tags.add(tag.trim());
		}
		doc.getProperties().put("tags", tags);
		
		//更新图片列表：注意脚本中已经有采集，此处使用自带的内容
		List<String> images = new ArrayList<String>();
		images.add(urlPrefix+item.getPictUrl());
		doc.getProperties().put("images", images);
		
		//将第一张展示图片作为logo
		doc.getProperties().put("logo", urlPrefix+item.getPictUrl());
		
		//增加类目
		doc.getProperties().put("category", poolName);//采用物料分类作为类目描述
		//检查类目映射
		boolean categoryMapped = false;
		PlatformCategory query = new PlatformCategory();
		query.setName(poolName);
		query.setPlatform("taobao");
		List<PlatformCategory> list = platformCategoryService.findMapping(query);
		if(list.size()>0) {//有则更新
			Map<String,Object> meta = new HashMap<String,Object>();
			meta.put("category", list.get(0).getCategory().getId());
			meta.put("categoryName", list.get(0).getCategory().getName());
			doc.getProperties().put("meta", meta);	
			categoryMapped = true;
		}else {//否则写入等待标注
			query.setIsNewRecord(true);
			query.setId(Util.md5("taobao"+poolName));//采用手动生成ID，避免多次查询生成多条记录
			query.setPlatform("taobao");
			query.setCreateDate(new Date());
			query.setUpdateDate(new Date());
			platformCategoryService.save(query);
			//检查是否支持无类目映射入库
			if(!"true".equalsIgnoreCase(Global.getConfig("sx.enhouseWithoutCategoryMapping"))) {
				return;
			}
		}

		//更新价格：直接覆盖
		Map<String,Object> price = new HashMap<String,Object>();
		price.put("currency", "￥");
		price.put("bid", item.getReservePrice());
		price.put("sale", item.getZkFinalPrice());
		if(item.getCouponAmount()!=null && item.getCouponAmount()>0) {
			price.put("coupon", item.getCouponAmount()*0.01);//待定
		}
		doc.getProperties().put("price", price);
		
		//更新佣金：直接覆盖
		Map<String,Object> profit = new HashMap<String,Object>();
		try {
			double rate = Double.parseDouble(item.getCommissionRate())*0.01;//需要核对数据
			profit.put("rate", parseNumber(rate*100));//返回的是百分比，直接使用即可
			profit.put("amount", rate*Double.parseDouble(item.getZkFinalPrice()));
			profit.put("type", "2-party");
			//直接计算佣金分配：根据佣金分配scheme TODO 注意：当前未考虑类目
			Map<String, Object> profit3party = calcProfit2Party.getProfit2Party("taobao", poolName, Double.parseDouble(item.getZkFinalPrice()), rate*Double.parseDouble(item.getZkFinalPrice()));
			if(profit3party.get("order")!=null&&profit3party.get("order").toString().trim().length()>0) {
				profit.put("order", Double.parseDouble(profit3party.get("order").toString()));
			}
			if(profit3party.get("team")!=null&&profit3party.get("team").toString().trim().length()>0) {
				profit.put("team", Double.parseDouble(profit3party.get("team").toString()));
			}
			if(profit3party.get("credit")!=null&&profit3party.get("credit").toString().trim().length()>0)profit.put("credit", Double.parseDouble(profit3party.get("credit").toString()));
			profit.put("type", "3-party");//计算完成后直接设置为3-party
			
			doc.getProperties().put("profit", profit);
		}catch(Exception ex) {
			logger.debug("failed parse commission",ex);
		}
		
		//设置状态。注意，需要设置sync=pending 等待计算CPS链接
		//状态更新
		Map<String,Object> status = new HashMap<String,Object>();
		status.put("crawl", "ready");
		status.put("sync", "ready");//等待生成CPS链接
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
		logger.debug("try to upsert taobao item.[itemKey]"+itemKey,JSON.toString(doc));

		//TODO：可以直接推送到kafka，减少数据库操作
		BaseDocument old = arangoClient.find("my_stuff", itemKey);
		if(old!=null && itemKey.equals(old.getKey())) {//已经存在，则直接跳过
			//do nothing
		}else {//否则写入
			Gson gson = new Gson();
			System.err.println(gson.toJson(doc));
			arangoClient.insert("my_stuff", doc);   
			processedMap.put(poolName, processedMap.get(poolName)+1);
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
    		logger.info("Taobao cps item search job start. " + new Date());
    		processedMap = new HashMap<String,Long>();
    		totalMap = new HashMap<String,Long>();
    		poolNameMap = new HashMap<String,String>();

    		//准备选品库信息
    		Map<String, Long> materialCategoryMap = getMaterialCateogry();
    		//准备连接
    		arangoClient = new ArangoDbClient(host,port,username,password,database);
    		for(String poolName:materialCategoryMap.keySet()) {//逐个分类查询，每个分类均进行遍历
    			logger.debug("start search by poolName.[poolName]"+poolName);
    			processedMap.put(poolName, 0L);//默认设置相应分类的条数为0
    			try {
    				//Step1:搜索得到推荐商品列表
    				List<TbkDgOptimusMaterialResponse.MapData> items = taobaoHelper.getOptimusMaterial(materialCategoryMap.get(poolName));//默认直接获取20条，每天执行即可
    				logger.debug("got items by material id.[name]"+poolName+"[id]"+materialCategoryMap.get(poolName));
    				//Step2:遍历得到所有条目
	    			for(TbkDgOptimusMaterialResponse.MapData item:items) {
	    				upsertItem(poolName,item);
	    			}
	    		}catch(Exception ex) {
	    			logger.warn("error while query items by material id.ignore.[name]"+poolName+"[id]"+materialCategoryMap.get(poolName),ex.getMessage());
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
		msg.put("openid", Global.getConfig("default_tech_guy_openid"));//固定发送
		msg.put("title", "数据自动上架任务结果");
		msg.put("task", "淘宝商品入库 已同步");
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
    
    private Map<String,Long> getMaterialCateogry(){
    	Map<String,Long> materialCategory = Maps.newHashMap();
		materialCategory.put("天猫国际直营类目爆款清单",44413L);
		materialCategory.put("天猫国际大贸清单",44412L);
		materialCategory.put("国际直营爆款补贴清单",37089L);
		materialCategory.put("国际直营爆款清单",37088L);
		materialCategory.put("天猫国际直营品牌清单",38508L);
		materialCategory.put("天猫国际直营99元选10件",36223L);
		materialCategory.put("天猫国际直营2件5折起",36224L);
		materialCategory.put("天猫国际199选N件",28659L);
		materialCategory.put("天猫国际直营99元选35件",36222L);
		materialCategory.put("特色淘抢购商品库 ",34616L);
		materialCategory.put("好券-综合",3756L);
		materialCategory.put("好券-鞋包配饰",3762L);
		materialCategory.put("好券-母婴",3760L);
		materialCategory.put("好券-女装",3767L);
		materialCategory.put("好券-美妆个护",3763L);
		materialCategory.put("好券-食品",3761L);
		materialCategory.put("好券-家居家装",3758L);
		materialCategory.put("好券-男装",3764L);
		materialCategory.put("好券-运动户外",3766L);
		materialCategory.put("好券-数码家电",3759L);
		materialCategory.put("好券-内衣",3765L);
		materialCategory.put("实时热销-综合",28026L);
		materialCategory.put("实时热销-大服饰 ",28029L);
		materialCategory.put("实时热销-大快消 ",28027L);
		materialCategory.put("实时热销-电器美家 ",28028L);
		materialCategory.put("大额券-综合",27446L);
		materialCategory.put("大额券-女装",27448L);
		materialCategory.put("大额券-食品",27451L);
		materialCategory.put("大额券-美妆个护",27453L);
		materialCategory.put("大额券-家居家装",27798L);
		materialCategory.put("大额券-母婴 ",27454L);
		materialCategory.put("品牌券-综合",3786L);
		materialCategory.put("品牌券-鞋包配饰",3796L);
		materialCategory.put("品牌券-母婴",3789L);
		materialCategory.put("品牌券-女装",3788L);
		materialCategory.put("品牌券-美妆个护",3794L);
		materialCategory.put("品牌券-食品",3791L);
		materialCategory.put("品牌券-家居家装",3792L);
		materialCategory.put("品牌券-男装",3790L);
		materialCategory.put("品牌券-运动户外",3795L);
		materialCategory.put("品牌券-数码家电",3793L);
		materialCategory.put("品牌券-内衣",3787L);
		materialCategory.put("潮流范",4093L);
		materialCategory.put("特惠",4094L);
		materialCategory.put("本地生活-电影代金券",19812L);
		materialCategory.put("本地生活-演出/演唱会/剧目/会展",25378L);
		materialCategory.put("本地生活-视频年卡",28636L);
		materialCategory.put("本地生活-喜马拉雅年卡儿童节目等",29105L);
		materialCategory.put("本地生活-hpv疫苗预约",25885L);
		materialCategory.put("本地生活-体检",25886L);
		materialCategory.put("本地生活-口腔",25888L);
		materialCategory.put("本地生活-基因检测",25890L);
		materialCategory.put("飞猪-签证",26077L);
		materialCategory.put("飞猪-酒店",27913L);
		materialCategory.put("飞猪-自助餐",27914L);
		materialCategory.put("飞猪-门票",19811L);
		materialCategory.put("本地生活-肯德基/必胜客/麦当劳",19810L);
		materialCategory.put("本地生活-生活服务",28888L);
		materialCategory.put("本地生活-家政服务",19814L);
		materialCategory.put("品牌精选清单",39313L);
		
		return materialCategory;
    }

}
