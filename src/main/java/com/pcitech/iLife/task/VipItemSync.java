package com.pcitech.iLife.task;

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
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.PromotionGoodsResp;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.response.get.PromotionCodeResp;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.JdHelper;
import com.pcitech.iLife.cps.VipHelper;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;
import com.vip.adp.api.open.service.GoodsInfo;
import com.vip.adp.api.open.service.UrlInfo;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 通过API接口，补充导购链接，及图片列表
 * 通过自动任务触发，每3分钟触发，每次处理30条（接口最大支持40条）。每天处理14400条。
 * 
 * 如果没有待处理内容则跳过
 */
@Service
public class VipItemSync {
    private static Logger logger = LoggerFactory.getLogger(VipItemSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    VipHelper vipHelper;
    
    String brokerId = "system";//默认设置为system
    String sxChannelType = "mp";//默认设置为mp
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public VipItemSync() {
    }
    
    private void syncCpsLinks(BaseDocument item) {
		String itemKey = item.getProperties().get("itemKey").toString();
		String url = item.getProperties().get("url").toString();
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		//设置状态。注意，需要设置index=pending 等待重新索引。只要有CPS链接，就可以推广了
		//状态更新
		Map<String,Object> status = new HashMap<String,Object>();
		status.put("sync", "ready");
		status.put("index", "pending");//等待重新索引
		doc.getProperties().put("status", status);
		//时间戳更新
		Map<String,Object> timestamp = new HashMap<String,Object>();
		timestamp.put("sync", new Date());//CPS链接生成时间
		doc.getProperties().put("timestamp", timestamp);
		
		try {
			//TODO 原始接口支持每10个一组生成cps链接或查询详情，当前是逐个查询
			//调用API接口生成CPS链接
			List<UrlInfo> result = vipHelper.generateCpsLinkByUrl( brokerId, ""+item.getProperties().get("url"),sxChannelType);
			if(result!=null  && result.size()>0) {
				Map<String,String> link = Maps.newHashMap();
				link.put("web2", result.get(0).getLongUrl());
				link.put("wap2", result.get(0).getLongUrl());
				doc.getProperties().put("link", link);
			}
			
			//根据goodsId获取详情更新类目
			String goodsId = ""+item.getProperties().get("goodsId");
			if(item.getProperties().get("goodsId")==null) {//如果查不到ID，先看看直接从URL里匹配一下
				//期望URL是这个样子：https://detail.vip.com/detail-1710615488-6918808670566474496.html
				Pattern p=Pattern.compile("\\d+"); 
				Matcher m=p.matcher(url); 
				while(m.find()) { //取得最后一个即可
					goodsId = m.group(); 
				} 
			}
			
			//查询商品详情，得到图片列表。假设商品长度超过品牌长度
			//URL最后一段为goodsId，如果不符合要求则直接忽略
			if(goodsId.trim().length()>"1710615488".length()) {
				List<GoodsInfo> itemDetail = vipHelper.getItemDetail(brokerId, goodsId);
				if(itemDetail != null && itemDetail.size()>0) {
					GoodsInfo goodInfo = itemDetail.get(0);
					//更新图片列表：注意脚本中已经有采集，此处使用自带的内容
					List<String> images = new ArrayList<String>();
					images.add(goodInfo.getGoodsMainPicture());//logo
					images.addAll(goodInfo.getGoodsDetailPictures());//添加图片列表
					doc.getProperties().put("images", images);
					
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
					doc.getProperties().put("profit", profit);
				}
			}

			//更新doc
			arangoClient.update("my_stuff", itemKey, doc);    	
			processedAmount++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，查询Arangodb中status.sync=="pending" and source=="vip"的记录，限制30条。
	 * 2，通过API接口查询生成商品导购链接
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    	logger.info("item sync job start. " + new Date());
    		
    	//1，查询待处理商品记录 返回itemKey、商品ID、商品链接
        String query = "for doc in my_stuff filter "
        		+ "(doc.source == \"vip\") and "
        		+ "doc.status.sync==\"pending\" "
//        		+ "update doc with {status:{sync:\"ready\"}} in my_stuff "//查询时即更新状态：仅更新记录状态，index状态需要根据计算结果设置
        		+ "limit 100 "//一个批次处理100条
        		+ "return {itemKey:doc._key,goodsId:doc.goodsId,url:doc.link.web}";
        
        try {
            arangoClient = new ArangoDbClient(host,port,username,password,database);
            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
            totalAmount = items.size();
            if(totalAmount ==0) {//如果没有了就提前收工
	            	logger.debug("没有待同步商品条目");
	            	arangoClient.close();//链接还是要关闭的
	            	return;
            }
            for (BaseDocument item:items) {
            		syncCpsLinks(item);//逐条查询CPS链接并更新ArangoDB doc
            }
        } catch (Exception e) {
            logger.error("Failed to execute query.",e);
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
		msg.put("task", "唯品会CPS链接 已同步");
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

    
    private double parseNumber(String numStr) {
    		try {
    			return Double.parseDouble(numStr);
    		}catch(Exception ex) {
    			logger.error("cannot parse double from input string.[numStr]"+numStr);
    			return 0;
    		}
    }
    
}
