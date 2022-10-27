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
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.PromotionGoodsResp;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.response.get.PromotionCodeResp;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.JdHelper;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.cps.crawler.Crawler;
import com.pcitech.iLife.cps.crawler.CrawlerBase;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.Util;

@Service
public class Jd extends CrawlerBase {
	private static Logger logger = LoggerFactory.getLogger(Jd.class);
	
    @Autowired
    JdHelper jdHelper;
    
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
		
		String brokerId = "system";//默认达人为system
	
		//准备doc
		BaseDocument doc = new BaseDocument();
		String itemKey = Util.md5(url);
		doc.setKey(itemKey);//url为唯一识别
		//设置状态。注意，需要设置index=pending 等待重新索引。只要有CPS链接，就可以推广了

		//采集任务信息
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-api");
		task.put("executor", "robot-api-jd");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		Map<String,Object> distributor = new HashMap<String,Object>();
		//distributor
		distributor.put("name", "京东");
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
		
		//1，查询商品详情
		//获取详情更新类目
		Pattern p=Pattern.compile("\\d+"); 
		Matcher m=p.matcher(url); 
		try {
			while(m.find()) { //仅处理第一个即可
			    String skuId = m.group(); 
				PromotionGoodsResp[] goods = jdHelper.getDetail(skuId);
				if(goods != null && goods.length>0) {
					PromotionGoodsResp good = goods[0];
					StringBuffer sb = new StringBuffer();
					sb.append(good.getCidName());
					sb.append(" "+good.getCid2Name());
					sb.append(" "+good.getCid3Name());
					doc.getProperties().put("category", sb.toString());//更新类目，包含3级别分类
					doc.getProperties().put("logo", good.getImgUrl());//更新首图 img
					doc.getProperties().put("title", good.getGoodsName());//更新title
					//图片列表
					List<String> images = Lists.newArrayList();
					images.add(good.getImgUrl());
					doc.getProperties().put("images", images);//图片列表
					//价格
					Map<String,Object> price = Maps.newHashMap();
					price.put("currency", "￥");
					price.put("sale", good.getUnitPrice());//京东价
					price.put("bid", good.getUnitPrice());//直接以售价填充
					doc.getProperties().put("price", price);
					//获取佣金信息
					Map<String,Object> profit = Maps.newHashMap();
					double amount = good.getWlUnitPrice()*good.getCommisionRatioWl()*0.01;
					profit.put("type", "2-party");
					profit.put("amount", amount);
					profit.put("rate", good.getCommisionRatioWl());//是百分比
					//直接计算佣金分配：根据佣金分配scheme TODO 注意：当前未考虑类目
					Map<String, Object> profit3party = calcProfit2Party.getProfit2Party("pdd", good.getCid3Name(), good.getWlUnitPrice(), amount);
					if(profit3party.get("order")!=null&&profit3party.get("order").toString().trim().length()>0)profit.put("order", Double.parseDouble(profit3party.get("order").toString()));
					if(profit3party.get("team")!=null&&profit3party.get("team").toString().trim().length()>0)profit.put("team", Double.parseDouble(profit3party.get("team").toString()));
					if(profit3party.get("credit")!=null&&profit3party.get("credit").toString().trim().length()>0)profit.put("credit", Double.parseDouble(profit3party.get("credit").toString()));
					profit.put("type", "3-party");//计算完成后直接设置为3-party
					
					doc.getProperties().put("profit", profit);//直接覆盖更新profit
					
					//查询CPS链接
					Map<String,String> links = new HashMap<String,String>();
					links.put("web", url);
					links.put("wap", url);
					try {
						PromotionCodeResp promotion=null;
						promotion = jdHelper.getCpsLink(url,brokerId);
						if(promotion!=null) {
							links.put("wap2", promotion.getClickURL());
							links.put("web2", promotion.getClickURL());
							doc.getProperties().put("link", links);
						}else {
							logger.warn("获取CPS链接失败："+url);
						}
					}catch(Exception ex) {
						logger.error("failed get item cps link.[url]"+url,ex.getMessage());
					}
					
					//检查类目映射
					PlatformCategory platformCategoryMapping = platformCategoryService.get("jd"+good.getCid3());
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
					
					//时间戳更新
					Map<String,Object> timestamp = new HashMap<String,Object>();
					timestamp.put("crawl", new Date());//入库时间
					doc.getProperties().put("timestamp", timestamp);

					//更新到arangodb
		    		arangoClient = new ArangoDbClient(host,port,username,password,database);
		    		//更新doc
		    		logger.debug("try to upsert pdd item.[itemKey]"+itemKey,JSON.toJSONString(doc));
		    		arangoClient.upsert("my_stuff", itemKey, doc); 
		    		//完成后关闭arangoDbClient
		    		arangoClient.close();

		    		result.put("success", true);
		    		Map<String,Object> data = doc.getProperties();
		    		data.put("itemKey", itemKey);
		    		result.put("data",data );//将properties返回
				}else {
					logger.warn("查询详情失败。【SKU】"+skuId+"【URL】"+url);
				}
				break;
			} 
		}catch(Exception ex) {
			logger.error("failed get item detail.[url]"+url,ex.getMessage());
		}
		
		return result;
	}

}
