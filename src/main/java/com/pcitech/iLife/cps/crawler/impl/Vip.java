package com.pcitech.iLife.cps.crawler.impl;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.VipHelper;
import com.pcitech.iLife.cps.crawler.Crawler;
import com.pcitech.iLife.cps.crawler.CrawlerBase;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.Util;
import com.vip.adp.api.open.service.GoodsInfo;
import com.vip.adp.api.open.service.UrlInfo;

@Service
public class Vip extends CrawlerBase {
	private static Logger logger = LoggerFactory.getLogger(Vip.class);
	
    @Autowired
    VipHelper vipHelper;
    
	@Override
	public JSONObject enhouse(String url, String openid) {
		logger.debug("try crawl item by url.",url);
		JSONObject result = new JSONObject();
		result.put("success", false);
		
		//检查openid：如果为非推广商品，则根据openid加载达人信息，需要人工介入后返回
		if(openid!=null && openid.trim().length()>0) {
			Broker broker = brokerService.getByOpenid(openid);
			if(broker != null) {
				JSONObject brokerInfo = new JSONObject();
				brokerInfo.put("nickname", broker.getNickname());
				brokerInfo.put("avatarUrl", broker.getAvatarUrl());
				result.put("broker", brokerInfo);
			}
		}

		//准备doc
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		String itemKey = Util.md5(url);
		doc.setKey(itemKey);//url为唯一识别
		//设置状态。注意，需要设置index=pending 等待重新索引。只要有CPS链接，就可以推广了

		//采集任务信息
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-vipItemRest");
		task.put("executor", "robot-vipItemRest-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		Map<String,Object> distributor = new HashMap<String,Object>();
		//distributor
		distributor.put("name", "唯品会");
		doc.getProperties().put("distributor", distributor);	
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
		
		String tipMsg = "";//即时返回通知的描述文字: 店返:xxx 团返:xxx 
		String brokerId = "system";//默认达人为system
		
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "vip");
		
		//获取CPS链接
		Map<String,Object> link = new HashMap<String,Object>();
		link.put("web", url);
		link.put("wap", url);
		//step 1: 将url转换为vip cps链接
		String cpsUrl = url;
		try {
			//调用API接口生成CPS链接
			List<UrlInfo> cpsUrlResult = vipHelper.generateCpsLinkByUrl( brokerId, url,"mp");//默认channelType设置为mp
			if(cpsUrlResult == null || cpsUrlResult.size()==0) {
				result.put("msg", "failed parse url.");
				return result;
			}
			cpsUrl = cpsUrlResult.get(0).getLongUrl();
			link.put("web2", cpsUrl);
			link.put("wap2", cpsUrl);
		} catch (Exception e) {
			logger.error("failed get cps link by url.[url]"+url,e);
			result.put("msg", "failed parse url.");
			return result;
		}
		doc.getProperties().put("link", link);
		
		//根据URL获取商品id，并自动入库
		//期望URL是这个样子：https://detail.vip.com/detail-1710615488-6918808670566474496.html
		String goodsId = "";
		Pattern p=Pattern.compile("\\d+"); 
		Matcher m=p.matcher(url); 
		while(m.find()) { //取得最后一个即可
			goodsId = m.group(); 
		} 
		
		//查询商品详情，得到图片列表。假设商品长度超过品牌长度
		//URL最后一段为goodsId，如果不符合要求则直接忽略
		GoodsInfo goodInfo = null;
		if(goodsId.trim().length()>"1710615488".length()) {
			List<GoodsInfo> itemDetail = null;
			try{
				itemDetail = vipHelper.getItemDetail(brokerId, goodsId);
			}catch(Exception ex) {
				logger.warn("failed get item detail.[id]"+goodsId);
				return result;
			}
			if(itemDetail != null && itemDetail.size()>0) {
				goodInfo = itemDetail.get(0);
				//更新图片列表：注意脚本中已经有采集，此处使用自带的内容
				List<String> images = new ArrayList<String>();
				images.add(goodInfo.getGoodsMainPicture());//logo
				images.addAll(goodInfo.getGoodsDetailPictures());//添加图片列表
				doc.getProperties().put("images", images);
				
				//标题
				doc.getProperties().put("title", goodInfo.getGoodsName());//更新title
				
				//将第一张展示图片作为logo
				doc.getProperties().put("logo", goodInfo.getGoodsMainPicture());

				//如果有documentInfo，则作为summary
				if(goodInfo.getGoodsDesc()!=null && goodInfo.getGoodsDesc().trim().length()>0)
					doc.getProperties().put("summary", goodInfo.getGoodsDesc());
				
				//增加类目
				List<String> categories = new ArrayList<String>();
				categories.add(goodInfo.getCat1stName());
				categories.add(goodInfo.getCat2ndName());
				categories.add(goodInfo.getCategoryName());
				doc.getProperties().put("category", categories);//更新类目，包含多级分类

				//更新价格：直接覆盖
				Map<String,Object> price = new HashMap<String,Object>();
				price.put("currency", "￥");
				price.put("bid", parseNumber(goodInfo.getMarketPrice()));//元
				price.put("sale", parseNumber(goodInfo.getVipPrice()));//元
				if(goodInfo.getCouponInfo()!=null) {//取第一个优惠金额
					price.put("coupon",  parseNumber(goodInfo.getCouponInfo().getFav()));//元
				}
				doc.getProperties().put("price", price);
				
				//更新佣金：直接覆盖
				Map<String,Object> profit = new HashMap<String,Object>();
				profit.put("rate", parseNumber(goodInfo.getCommissionRate()));//返回的是百分比，直接使用即可
				profit.put("amount", parseNumber(goodInfo.getCommission()));//元
				profit.put("type", "2-party");
				
				//直接计算分润
				//直接计算佣金分配：根据佣金分配scheme TODO 注意：当前未考虑类目
				Map<String, Object> profit3party = calcProfit2Party.getProfit2Party("vip", "", parseNumber(goodInfo.getVipPrice()), parseNumber(goodInfo.getCommission()));
				if(profit3party.get("order")!=null&&profit3party.get("order").toString().trim().length()>0) {
					tipMsg +="店返 ￥"+profit3party.get("order");
					profit.put("order", Double.parseDouble(profit3party.get("order").toString()));
				}
				if(profit3party.get("team")!=null&&profit3party.get("team").toString().trim().length()>0) {
					tipMsg +="团返 ￥"+profit3party.get("team");
					profit.put("team", Double.parseDouble(profit3party.get("team").toString()));
				}
				if(profit3party.get("credit")!=null&&profit3party.get("credit").toString().trim().length()>0)profit.put("credit", Double.parseDouble(profit3party.get("credit").toString()));
				profit.put("type", "3-party");//计算完成后直接设置为3-party
				doc.getProperties().put("profit", profit);
				
				doc.getProperties().put("profit", profit);
			}
		}

		//检查类目映射
		if(goodInfo!= null) {
			PlatformCategory platformCategoryMapping = platformCategoryService.get("vip"+goodInfo.getCat2ndId());
			if(platformCategoryMapping!=null) {//有则更新
				doc.getProperties().put("category", platformCategoryMapping.getName());	//补充原始类目名称
				if(platformCategoryMapping.getCategory()!=null) {//有则更新
					Map<String,Object> meta = new HashMap<String,Object>();
					meta.put("category", platformCategoryMapping.getCategory().getId());
					meta.put("categoryName", platformCategoryMapping.getCategory().getName());
					doc.getProperties().put("meta", meta);	
				}
			}/**else {
				//检查是否支持无类目映射入库
				if(!"true".equalsIgnoreCase(Global.getConfig("sx.enhouseWithoutCategoryMapping"))) {
					result.put("msg", "no category mapping");
					return result;
				}
			}//**/
		}
		//时间戳更新
		Map<String,Object> timestamp = new HashMap<String,Object>();
		timestamp.put("crawl", new Date());//入库时间
		doc.getProperties().put("timestamp", timestamp);
		
		/**
		//直接提交到kafka
		//暂缓：由于推送有异步时间可能会导致点击返回卡片时无法读取
		Map<String,Object> jsonDoc = doc.getProperties();
		jsonDoc.put("_key", itemKey);
		System.err.println(new Gson().toJson(jsonDoc));
		kafkaStuffLogger.info(new Gson().toJson(jsonDoc));
		//**/
		
		//更新到arangodb
		arangoClient = new ArangoDbClient(host,port,username,password,database);
		//更新doc
		logger.debug("try to upsert vip item.[itemKey]"+itemKey,JSON.toJSONString(doc));
		arangoClient.upsert("my_stuff", itemKey, doc); 
		//完成后关闭arangoDbClient
		arangoClient.close();
		
		//临时修改返回数据，将佣金信息作为摘要提示
		tipMsg += " 点击查看详情";
		doc.getProperties().put("summary", tipMsg);
		result.put("success", true);
		Map<String,Object> data = doc.getProperties();
		data.put("itemKey", itemKey);
		result.put("data",data );//将properties返回
		
		return result;
	}

    private double parseNumber(String numStr) {
		try {
			return Double.parseDouble(numStr);
		}catch(Exception ex) {
			logger.error("cannot parse double from input string.[numStr]"+numStr);
			return 0;
		}
}
	
}
