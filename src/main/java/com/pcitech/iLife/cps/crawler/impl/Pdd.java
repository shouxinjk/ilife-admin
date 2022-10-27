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
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.cps.crawler.Crawler;
import com.pcitech.iLife.cps.crawler.CrawlerBase;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.Util;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;

@Service
public class Pdd extends CrawlerBase {
	private static Logger logger = LoggerFactory.getLogger(Pdd.class);
	
    @Autowired
    PddHelper pddHelper;
    
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
		
		//step 1: 将url转换为pdd cps链接
		String cpsUrl = url;
		try {
			GoodsZsUnitGenerateResponse response = pddHelper.generateCpsLinksByUrl(brokerId, url);
			cpsUrl = response.getMobileUrl();
		} catch (Exception e) {
			logger.error("failed get cps link by url.[url]"+url,e);
			result.put("msg", "failed parse url.");
		}//默认为system
		
		//step 2: 根据goods_sign获取商品详情并入库
		//https://mobile.yangkeduo.com/duo_coupon_landing.html?goods_id=247148672711&pid=20434335_206807608&goods_sign=E932m-F7iFNU8LcRwfDaiZopXsF9TOKL_JQENcpyaEj&cpsSign=CC_220531_20434335_206807608_167757c35a33e72414d1bf494a2ac57a&_x_ddjb_act=%7B%22st%22%3A%221%22%7D&duoduo_type=2
		String goodsSign = "";
		Pattern p=Pattern.compile("goods_sign=([A-Za-z0-9_\\-]+)"); 
		Matcher m=p.matcher(cpsUrl); 
		if(m.find()) { //仅在发现后进行
			goodsSign = m.group(m.groupCount()); //只处理最后一组，实际上也只有一组
			logger.debug("generateCpsLink API return success.[goodsSign]"+goodsSign);
		} else {
			logger.error("generateCpsLink API does not return goodsSign in URL.",cpsUrl);
			result.put("msg", "no goods_sign found.");
			return result;
		}

		//准备doc
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		String itemKey = Util.md5(url);
		doc.setKey(itemKey);//url为唯一识别
		//设置状态。注意，需要设置index=pending 等待重新索引。只要有CPS链接，就可以推广了

		//采集任务信息
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-pddItemRest");
		task.put("executor", "robot-pddItemRest-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		Map<String,Object> distributor = new HashMap<String,Object>();
		//distributor
		distributor.put("name", "拼多多");
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
		//获取商品详情，填充tags等信息
		try {
			//获取CPS链接
			Map<String,Object> link = new HashMap<String,Object>();
			link.put("web", url);
			link.put("wap", url);
			//针对当前goodsign单独请求CPS链接
			List<String> goodSignList = Lists.newArrayList();
			goodSignList.add(goodsSign);
			PddDdkGoodsPromotionUrlGenerateResponse response = pddHelper.generateCpsLinksByGoodsSign(brokerId,null,goodSignList);
			GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem cpsResult = response.getGoodsPromotionUrlGenerateResponse().getGoodsPromotionUrlList().get(0);
			if(cpsResult.getMobileShortUrl()==null) {//如果还是没拿到，那就真的没招了，赶紧再去对接口找原因
				logger.warn("cannot generate cps link by goodsign. disable this item.");
				status.put("index", "ready");//因为没有wap2，不需要索引
				doc.getProperties().put("status", status);
			}else {
				logger.debug("generate cps link by goodsign successfully.");
				link.put("wap2", cpsResult.getMobileShortUrl());//采用短连接
				link.put("web2", cpsResult.getShortUrl());
			}
			doc.getProperties().put("link", link);
		
			//获取商品详情
			GoodsDetailResponse resp = pddHelper.getItemDetail(brokerId, goodsSign);//TODO:注意：有可能不是多多进宝商品，返回为null，会导致异常
			if(resp != null) {
				List<String> tags = new ArrayList<String>();
				List<String> unifiedTags = resp.getGoodsDetails().get(0).getUnifiedTags();//商品unified tags
				tags.add(resp.getGoodsDetails().get(0).getOptName());//商品标签
				for(String tag:unifiedTags) {//商品unified tags里有null项 ，需要排除
					if(null != tag && tag.trim().length() > 0 && !"null".equalsIgnoreCase(tag))
						tags.add(tag);
				}
				doc.getProperties().put("type", "commodity");
				doc.getProperties().put("source", "pdd");
				doc.getProperties().put("catIds", resp.getGoodsDetails().get(0).getCatIds());//更新category TODO 当前为ID，需要在同步替换
				doc.getProperties().put("searchId", "");//TODO:没有searchId
				doc.getProperties().put("title", resp.getGoodsDetails().get(0).getGoodsName());//更新title
				doc.getProperties().put("tags", tags);//更新类目，包含3级别分类
				doc.getProperties().put("logo", resp.getGoodsDetails().get(0).getGoodsImageUrl());//更新首图 img
				doc.getProperties().put("summary", resp.getGoodsDetails().get(0).getGoodsDesc());//更新简介
				doc.getProperties().put("images", resp.getGoodsDetails().get(0).getGoodsGalleryUrls());//更新图片
				
				Map<String,Object> seller = new HashMap<String,Object>();
				seller.put("name", resp.getGoodsDetails().get(0).getMallName());
				doc.getProperties().put("seller", seller);	
	
				//更新品牌信息到prop列表
				Map<String,String> props = new HashMap<String,String>();
				//如果原来已经有属性，需要继续保留
				if(doc.getProperties().get("props") != null) {
					Map<String,String> oldProps = (Map<String,String>)doc.getProperties().get("props");
					props = oldProps;
				}
				props.put("品牌", resp.getGoodsDetails().get(0).getBrandName());//增加品牌属性
				doc.getProperties().put("props", props);

				List<String> images = new ArrayList<String>();
				images.add(resp.getGoodsDetails().get(0).getGoodsImageUrl());
				doc.getProperties().put("images", images);//更新图片
				//价格
				Map<String,Object> price = new HashMap<String,Object>();
				price.put("currency", "￥");
				price.put("coupon", parseNumber(resp.getGoodsDetails().get(0).getCouponDiscount()*0.01));//单位为分，换算为元
				price.put("bid", parseNumber(resp.getGoodsDetails().get(0).getMinNormalPrice()*0.01));//单位为分，换算为元
				price.put("sale", parseNumber(resp.getGoodsDetails().get(0).getMinGroupPrice()*0.01));//单位为分，换算为元
				doc.getProperties().put("price", price);	
				Map<String,Object> profit = new HashMap<String,Object>();
				profit.put("rate", parseNumber(resp.getGoodsDetails().get(0).getPromotionRate()*0.1));//是千分比，转换为百分比
				profit.put("type", "2-party");//待自动任务转换为3-party
				double salePrice = resp.getGoodsDetails().get(0).getMinGroupPrice()*0.01;//默认按卖价计算佣金
				if(resp.getGoodsDetails().get(0).getHasCoupon()) //如果有coupon则扣除coupon后计算
					salePrice = (resp.getGoodsDetails().get(0).getMinGroupPrice()-resp.getGoodsDetails().get(0).getCouponDiscount())*0.01;//实际卖价：转换为元
				double commssion = salePrice*resp.getGoodsDetails().get(0).getPromotionRate()*0.001;//千分比，转换为元
				profit.put("amount",parseNumber(commssion));//单位为元
				//直接计算佣金分配：根据佣金分配scheme TODO 注意：当前未考虑类目
				Map<String, Object> profit3party = calcProfit2Party.getProfit2Party("pdd", "", salePrice, parseNumber(commssion));
				if(profit3party.get("order")!=null&&profit3party.get("order").toString().trim().length()>0)profit.put("order", Double.parseDouble(profit3party.get("order").toString()));
				if(profit3party.get("team")!=null&&profit3party.get("team").toString().trim().length()>0)profit.put("team", Double.parseDouble(profit3party.get("team").toString()));
				if(profit3party.get("credit")!=null&&profit3party.get("credit").toString().trim().length()>0)profit.put("credit", Double.parseDouble(profit3party.get("credit").toString()));
				profit.put("type", "3-party");//计算完成后直接设置为3-party
				doc.getProperties().put("profit", profit);	
				
				//检查类目映射
				PlatformCategory platformCategoryMapping = platformCategoryService.get("pdd"+resp.getGoodsDetails().get(0).getCatIds().get(resp.getGoodsDetails().get(0).getCatIds().size()-1));
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
				logger.warn("cannot get item detail.");
			}
		}catch(Exception ex) {
			logger.error("error occured while enhouse pdd item.",ex);
		}
		
		return result;
	}

}
