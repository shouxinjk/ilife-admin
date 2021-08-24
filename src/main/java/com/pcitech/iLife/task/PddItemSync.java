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
import com.google.gson.Gson;
import com.jd.open.api.sdk.internal.JSON.JSON;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 通过拼多多API接口，补充导购链接。
 * 通过自动任务触发，每3分钟触发，每次处理30条（接口最大支持40条）。每天处理14400条。
 * 
 * 查询条件：
 * status.sync==null
 * 
 * 如果没有待处理内容则等待
 */
@Service
public class PddItemSync {
    private static Logger logger = LoggerFactory.getLogger(PddItemSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    PddHelper pddHelper;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    
    //默认设置brokerId
    String brokerId = "default";
    
    //准备本地goodSign列表
    Map<String,List<String>> goodsSignListMap = null;//注意：需要根据searchId分别准备SignList
    Map<String,String> cpsLinkMapWeb = null;
    Map<String,String> cpsLinkMapWap = null;

    public PddItemSync() {
    }
	
    private void syncGoodsItemDetail(BaseDocument item) {
		String itemKey = item.getProperties().get("itemKey").toString();
		String  url = item.getProperties().get("link").toString();
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		Map<String,Object> syncStatus = new HashMap<String,Object>();
		syncStatus.put("sync", true);
		Map<String,Object> syncTimestamp = new HashMap<String,Object>();
		syncTimestamp.put("sync", new Date());	
		doc.setKey(itemKey);
		doc.getProperties().put("status", syncStatus);
		doc.getProperties().put("timestamp", syncTimestamp);
		
		//匹配获取goodsSign
		String goodsSign = item.getProperties().get("sign").toString();
		logger.debug("start get item detail by goodsSign.[goodsSign]"+goodsSign);
		try {
			//从上一步已经准备好的map里取得链接
			Map<String,String> links = new HashMap<String,String>();
			links.put("wap2", cpsLinkMapWap.get(goodsSign));//采用短连接
			links.put("web2", cpsLinkMapWeb.get(goodsSign));//采用短连接
			doc.getProperties().put("link", links);
			logger.debug("get links from local buffered map.[itemKey]"+itemKey+"[goodsSign]"+goodsSign+"[links]"+JsonUtil.transferToJson(links));
		
			//获取商品详情，填充tags等信息
			GoodsDetailResponse resp = pddHelper.getItemDetail(brokerId, goodsSign);//TODO:注意：有可能不是多多进宝商品，返回为null，会导致异常
			if(resp != null) {
				List<String> tags = new ArrayList<String>();
				List<String> unifiedTags = resp.getGoodsDetails().get(0).getUnifiedTags();//商品unified tags
				tags.add(resp.getGoodsDetails().get(0).getOptName());//商品标签
				for(String tag:unifiedTags) {//商品unified tags里有null项 ，需要排除
					if(null != tag && tag.trim().length() > 0 && !"null".equalsIgnoreCase(tag))
						tags.add(tag);
				}
				doc.getProperties().put("title", resp.getGoodsDetails().get(0).getGoodsName());//更新title
				doc.getProperties().put("tags", tags);//更新类目，包含3级别分类
				doc.getProperties().put("logo", resp.getGoodsDetails().get(0).getGoodsImageUrl());//更新首图 img
				doc.getProperties().put("summary", resp.getGoodsDetails().get(0).getGoodsDesc());//更新简介
				doc.getProperties().put("images", resp.getGoodsDetails().get(0).getGoodsGalleryUrls());//更新图片

				//更新品牌信息到prop列表
				Map<String,String> props = new HashMap<String,String>();
				//如果原来已经有属性，需要继续保留
				if(doc.getProperties().get("props") != null) {
					Map<String,String> oldProps = (Map<String,String>)doc.getProperties().get("props");
					props = oldProps;
				}
				props.put("品牌", resp.getGoodsDetails().get(0).getBrandName());//增加品牌属性
				doc.getProperties().put("props", props);
				//TODO 当前仅返回分类ID列表，尚未能获取分类明细。待完成
			}else {
				logger.warn("cannot get item detail.");
			}
			//更新doc
			arangoClient.update("my_stuff", itemKey, doc);    	
			processedAmount++;
			
		} catch (Exception e) {
			//TODO 需要注意：当前有重复提示问题，直接更新doc，避免导致重复提示
			//arangoClient.update("my_stuff", itemKey, doc);   
			e.printStackTrace();
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
    		logger.info("Pinduoduo item sync job start. " + new Date());
    		
    		//1，查询待处理商品记录 返回itemKey、商品ID、商品链接
    		//for doc in my_stuff filter (doc.source == "pdd") and (doc.status==null or doc.status.sync==null) limit 30 return {itemKey:doc._key,id:split(doc.link.web,"id=")[1],link:doc.link.web}    		
        String query = "for doc in my_stuff filter "
        		+ "(doc.source == \"pdd\") and "
//        		+ "(doc.status==null or doc.status.sync==null) "
        		+ "doc.status.crawl==\"pending\" "
        		+ "update doc with {status:{crawl:\"ready\"}} in my_stuff "//查询时即更新状态
        		+ "sort doc.searchId desc "//根据searchId排序，便于批量生成CPS link
        		+ "limit 1000 "//一个批次处理30条
        		+ "return {itemKey:doc._key,link:doc.link.web,sign:REGEX_REPLACE(doc.link.web,\"http.+s=\",\"\"),searchId:doc.searchId==null?\"-\":doc.searchId}";
        
        try {
            arangoClient = new ArangoDbClient(host,port,username,password,database);
            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
            totalAmount = items.size();
            if(totalAmount ==0) {//如果没有了就提前收工
	            	logger.debug("没有待同步拼多多商品条目");
	            	arangoClient.close();//链接还是要关闭的
	            	return;
	        }
            //Step1：为节省API调用频次，首先组装goodSign，批量一次性生成CPS链接
            goodsSignListMap = new HashMap<String,List<String>>();//key:searchId, value: goodsSigns列表
            cpsLinkMapWeb = new HashMap<String,String>();
            cpsLinkMapWap = new HashMap<String,String>();
            for (BaseDocument item:items) {
            		if(item.getProperties().get("sign")!=null && item.getProperties().get("sign").toString().trim().length()>0) {
            			List<String> signsListBySearchId = goodsSignListMap.get(item.getProperties().get("searchId").toString());
            			if(signsListBySearchId == null)
            				signsListBySearchId = new ArrayList<String>();
            			signsListBySearchId.add(item.getProperties().get("sign").toString());
            			goodsSignListMap.put(item.getProperties().get("searchId").toString(),signsListBySearchId);
            		}
	        }
            //根据searchId逐次查询CSP链接，结果存放到cpsLinkMapWeb及cpsLinkMapWap
            for(Map.Entry<String, List<String>> entry:goodsSignListMap.entrySet()) {
            		String searchId = entry.getKey();
            		List<String> goodsSigns = entry.getValue();
            		String brokerId = "default";
            		try {
            			logger.debug("generate cps links by searchId.[searchId]"+searchId,goodsSigns);
            			PddDdkGoodsPromotionUrlGenerateResponse response = pddHelper.generateCpsLinksByGoodsSign(brokerId,"-".equalsIgnoreCase(searchId)?null:searchId,goodsSigns);
            			logger.debug("generate cps links by searchId.[result]"+JsonUtil.transferToJson(response));
            			List<GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem> urlList = response.getGoodsPromotionUrlGenerateResponse().getGoodsPromotionUrlList();
            			//注意，由于返回列表可能与传入goodsSign次序不一致，此处通过解析URL的方法获取goodsSign及对应的CPS链接
            			//嗯，赤果果的对API接口返回数据表示不信任，hiahiahia~~~
            			Pattern p=Pattern.compile("goods_sign=([A-Za-z0-9_\\-]+)"); 
            			for(GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem url:urlList) {
	            			Matcher m=p.matcher(url.getUrl()); 
	            			if(m.find()) { //仅在发现后进行
	            				String goodsSign = m.group(m.groupCount()); //只处理最后一组，实际上也只有一组
	            				logger.debug("generateCpsLink API return success.[goodsSign]"+goodsSign);
	            				cpsLinkMapWeb.put(goodsSign, url.getShortUrl());
	            				cpsLinkMapWap.put(goodsSign, url.getMobileShortUrl());
	            			} else {
	            				logger.error("generateCpsLink API doesnot return goodsSign in URL.",JSON.toString(url));
	            			}
            			}
            		} catch (Exception e) {
            			logger.error("try to generate CPS links error.",e);
            		}
            }
            
            //Step2：逐条更新商品详情
            for (BaseDocument item:items) {
            		syncGoodsItemDetail(item);//逐条查询CPS链接并更新ArangoDB doc
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
		msg.put("task", "拼多多CPS链接 已同步");
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

}
