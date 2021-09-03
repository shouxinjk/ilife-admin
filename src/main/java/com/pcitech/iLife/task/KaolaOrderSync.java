package com.pcitech.iLife.task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.arangodb.entity.BaseDocument;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.JdHelper;
import com.pcitech.iLife.cps.KaolaHelper;
import com.pcitech.iLife.cps.kaola.OrderInfo;
import com.pcitech.iLife.cps.kaola.OrderInfoResponse;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;

import org.quartz.JobExecutionException;

/**
 * 定时通过API接口获取订单信息。
 * 订单数据按照以下规则同步：
 * 1，原始数据直接存入Arangodb，用于追溯
 * 2，同时同步到MySQL，用于通知与汇总
 */
@Service
public class KaolaOrderSync {
    private static Logger logger = LoggerFactory.getLogger(KaolaOrderSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    KaolaHelper kaolaHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    BrokerService brokerService;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public KaolaOrderSync() {
    }
    
    private void syncOrder(OrderInfo item) {
    	//itemKey是受控的，自动生成一个
		String itemKey = Util.md5("kaola"+item.getOrderId());
		
		//管他三七二十一，全部存储
		Map<String,Object> props = JSONObject.parseObject(JSON.toJSONString(item),new TypeReference<Map<String,Object>>(){});
		
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);//装上我们自己定义的识别ID，避免多个平台间ID冲突
		doc.setProperties(props);//设置所有原始订单信息
		doc.getProperties().put("timestamp", new Date());//加一个我们自己的同步时间戳
		doc.getProperties().put("source", "kaola");//标记来源
		
		//写入 arangodb
		arangoClient.insert("order", doc);    
		
		//接下来写入Order
		Order order = new Order();
		order.setStatus("pending");//等待清分
		order.setId(itemKey);//与NoSQL保持一致
		order.setPlatform("kaola");
		order.setOrderNo(""+item.getOrderId());
		JSONObject traceCode = new JSONObject();
		traceCode.put("tc1", item.getTrackingCode1());
		traceCode.put("tc2", item.getTrackingCode2());
		order.setTraceCode(traceCode.toJSONString());
		order.setAmount(item.getActualPayAmount().doubleValue());
		if(item.getSettleStatus().intValue()==1) {//订单状态为1-已结算则为实际结算值
			order.setCommissionSettlement(item.getCommissionAmount().doubleValue());
		}else {//否则为预估佣金
			order.setCommissionEstimate(item.getCommissionAmount().doubleValue());
		}
		order.setItem(item.getGoodItemList().get(0).getGoodsName());//只取第一个商品名称显示
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//"2018-10-17 11:50:26"
		Date orderTime = new Date();
		try {
			orderTime = df.parse(item.getOrderTime());
		} catch (Exception e) {
			logger.error("parse order time error.",e);
		}
		order.setOrderTime(orderTime);
		Broker broker = brokerService.get(item.getTrackingCode1());//tc1就是达人ID
		order.setBroker(broker);
		order.setNotification("pending");//不用管通知状态，后续通知任务会自动更新
		order.setStatus("pending");
		
		orderService.save(order);
		
		processedAmount++;
		
    }

    /**
	 * 查询订单
	 * 存储进入Arangodb
	 * 存储进入MySQL（写入后自动触发通知）
     */
    public void execute() throws JobExecutionException {
    	logger.info("order sync job start. " + new Date());
    		
    	//1，调用接口查询订单列表
    	//先来头盘，如果调用失败就自己找个地缝钻进去吧
    	OrderInfoResponse  resp = kaolaHelper.getOrder();
    	List<OrderInfo> orders = resp.getData();
    	
    	//再来开胃菜，如果没数据就去捶运营
    	if(orders==null || orders.size()==0) {//接口是调用上了，但没数据，赶紧去捶运营吧，让他们去促成订单
    		logger.warn("no order found.[timestamp]"+new Date());
    		return;
    	}
    	
    	//接下来才是正菜
    	totalAmount = orders.size();
    	arangoClient = new ArangoDbClient(host,port,username,password,database);
    	for(OrderInfo order:orders) {
    		syncOrder(order);
    	}
    	//完成后关闭arangoDbClient
    	arangoClient.close();
    		
		//来个甜点：发送处理结果到管理员，甜到齁死人的那种
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", "o8HmJ1EdIUR8iZRwaq1T7D_nPIYc");//固定发送
		msg.put("title", "订单同步任务结果");
		msg.put("task", "考拉订单数据 已同步");
		msg.put("time", fmt.format(new Date()));
		msg.put("remark", "订单总数："+totalAmount
				+ "\n入库数量："+processedAmount);
		msg.put("color", totalAmount-processedAmount==0?"#FF0000":"#000000");

		result = HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/data-sync-notify", 
				msg,header);
		//3，更新通知状态
		if(result.getBooleanValue("status")) {
			logger.info("order-sync notification msg sent.[msgId] " + result.getString("msgId"));
		}
        logger.debug("order-sync notification job executed.[msg]" + msg);
		//处理数量归零
        processedAmount = 0;
    }

}
