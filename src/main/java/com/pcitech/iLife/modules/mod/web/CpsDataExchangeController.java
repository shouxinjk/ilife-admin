package com.pcitech.iLife.modules.mod.web;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.cps.KaolaHelper;
import com.pcitech.iLife.cps.kaola.CategoryInfo;
import com.pcitech.iLife.cps.kaola.GoodInfo;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.pdd.pop.sdk.common.util.JsonUtil;
import  com.pcitech.iLife.cps.kaola.OrderInfoPush;

/**
 * 响应第三方CPS网站推送商品更新数据或订单数据
 * @author alexchew
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/third-party")
public class CpsDataExchangeController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(CpsDataExchangeController.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    String brokerId = "system";
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    
    NumberFormat nf = null;
    
    @Autowired
    KaolaHelper kaolaHelper;
    
	/**
	 * 接收考拉推送订单数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "kaola/order", method = RequestMethod.POST)
	public Map<String, Object> handleOrderData(@RequestBody JSONObject data, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> jsonMap = Maps.newHashMap();
		jsonMap.put("messageId",data.getString("messageId"));
		jsonMap.put("data",data);
		jsonMap.put("description","Glad to recieve order data you sent. Thanks a lot. Have a nice day!");
		//TODO 插入或更新订单数据库，待定，需要判定接收到的数据是啥，有可能是列表，也有可能是单条
//		OrderInfoPush order = JSON.parseObject(data.toJSONString(),OrderInfoPush.class);
		return jsonMap;
	}
    
	/**
	 * 接收考拉推送商品更新数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "kaola/goods", method = RequestMethod.POST)
	public Map<String, Object> handleItemData(@RequestBody JSONObject data, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> jsonMap = Maps.newHashMap();
		jsonMap.put("messageId",data.getString("messageId"));
		jsonMap.put("changeGoodsIds",data.getJSONArray("changeGoodsIds"));
		jsonMap.put("description","we handled data you sent. Thanks a lot. Have a nice day!");
		
		List<Long> ids = new ArrayList<Long>();
		for(Object obj:data.getJSONArray("changeGoodsIds"))
			ids.add(Long.parseLong(obj.toString()));
		
		//准备连接
		arangoClient = new ArangoDbClient(host,port,username,password,database);

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
			GoodsInfoResponse goods = kaolaHelper.getItemDetail(brokerId, skuIds);
			for(GoodInfo item:goods.getData()) {//逐个插入
				logger.debug(JsonUtil.transferToJson(item));
				upsertKaolaItem(item);
			}
		}

		//完成后关闭arangoDbClient
		arangoClient.close();
		
		//组装通知信息
		StringBuffer remark = new StringBuffer();
		remark.append("预期数量："+totalAmount);
		remark.append("入库数量："+processedAmount);
		remark.append("\n数量差异："+(totalAmount-processedAmount));
		JSONObject msg = new JSONObject();
		msg.put("title", "导购数据同步任务结果");
		msg.put("task", "考拉商品数据接收 已入库");
		msg.put("remark", remark);
		//发送处理结果到管理员
		sendNotification(msg);
		
		return jsonMap;
	}

	private JSONObject sendNotification(JSONObject msg) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
		msg.put("openid", "o8HmJ1EdIUR8iZRwaq1T7D_nPIYc");//固定发送
		msg.put("time", fmt.format(new Date()));
		msg.put("color", totalAmount-processedAmount==0?"#FF0000":"#000000");
		JSONObject result = HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/data-sync-notify", 
				msg,header);
		//3，更新通知状态
		if(result.getBooleanValue("status")) {
			logger.info("clearing notification msg sent.[msgId] " + result.getString("msgId"));
		}
	    logger.info("Clearing Notification job executed.[msg]" + msg);
	    return result;
	}

    private void upsertKaolaItem(GoodInfo item) {
		String  url = item.getLinkInfo().getGoodsPCUrl();//得到唯一URL
		String itemKey = Util.md5(url);//根据URL生成唯一key
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		doc.getProperties().put("extra", "thirdparty-kaola");//推送接收标记，仅用于测试
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
		task.put("timestamp:new", new Date().getTime());
		task.put("url", url);
		doc.getProperties().put("task", task);
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "kaola");

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
		
		//设置基本信息更新标志为true
		Map<String,Object> syncStatus = new HashMap<String,Object>();
		syncStatus.put("sync", true);
		Map<String,Object> syncTimestamp = new HashMap<String,Object>();
		syncTimestamp.put("sync", new Date());	
		doc.getProperties().put("status", syncStatus);
		doc.getProperties().put("timestamp", syncTimestamp);

		//更新doc
		logger.debug("try to upsert kaola item.[itemKey]"+itemKey,JSON.toJSONString(doc));
		arangoClient.upsert("my_stuff", itemKey, doc);  
		processedAmount++;
    }
    
    private double parseNumber(double d) {
    		String numStr = getNumberFormat().format(d);
    		try {
    			return Double.parseDouble(numStr);
    		}catch(Exception ex) {
    			return d;
    		}
    }
    
    private NumberFormat getNumberFormat() {
	    	if(nf == null) {
			nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);	// 保留两位小数
			nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
	    	}
	    	return nf;
    }
	
}
