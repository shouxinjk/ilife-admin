package com.pcitech.iLife.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.arangodb.entity.BaseDocument;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.VipHelper;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.vip.adp.api.open.service.OrderInfo;
import com.vip.adp.api.open.service.OrderResponse;

import org.quartz.JobExecutionException;

/**
 * 定时通过API接口获取订单信息。
 * 订单数据按照以下规则同步：
 * 1，原始数据直接存入Arangodb，用于追溯
 * 2，同时同步到MySQL，用于通知与汇总
 */
@Service
public class VipOrderSync {
    private static Logger logger = LoggerFactory.getLogger(VipOrderSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    VipHelper vipHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    BrokerService brokerService;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public VipOrderSync() {
    }
    
    private void syncOrder(OrderInfo item) {
    	//itemKey是受控的，自动生成一个
		String itemKey = Util.md5("vip"+item.getOrderSn());
		
		//管他三七二十一，全部存储
		Map<String,Object> props = JSONObject.parseObject(JSON.toJSONString(item),new TypeReference<Map<String,Object>>(){});
		
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);//装上我们自己定义的识别ID，避免多个平台间ID冲突
		doc.setProperties(props);//设置所有原始订单信息
		doc.getProperties().put("timestamp", new Date());//加一个我们自己的同步时间戳
		doc.getProperties().put("source", "vip");//标记来源
		
		//写入 arangodb
		arangoClient.insert("order", doc);    
		
		//接下来写入Order
		Order order = new Order();
		order.setStatus("pending");//等待清分
		order.setId(itemKey);//与NoSQL保持一致
		order.setPlatform("vip");
		order.setOrderNo(""+item.getOrderSn());
		order.setTraceCode(item.getPid());
		order.setAmount(parseNumber(item.getTotalCost()));
		order.setCommissionEstimate(parseNumber(item.getCommission()));
		if(item.getAfterSaleChangeCommission()!=null && item.getAfterSaleChangeCommission().trim().length()>0)
			order.setCommissionSettlement(parseNumber(item.getAfterSaleChangeCommission()));//TODO 需要确认
		else
			order.setCommissionSettlement(parseNumber(item.getCommission()));//TODO 需要确认
		order.setItem(item.getDetailList().get(0).getGoodsName());
		order.setOrderTime(new Date(item.getOrderTime()));
		Broker broker = brokerService.get(item.getPid());//跟踪码就是达人ID
		if(broker==null)broker=brokerService.get("system");//如果找不到，则直接使用平台默认账户
		order.setBroker(broker);
		order.setNotification("pending");//不用管通知状态，后续通知任务会自动更新
		order.setStatus("pending");
		
		orderService.save(order);
		
		processedAmount++;
		
    }

    //根据状态查询订单，并处理翻页
    private void getOrders(int status) {
    	int pageSize = 100;
		int pageNo = 1;//默认页码从1开始
		
		Calendar cal = Calendar.getInstance();
		long end = cal.getTime().getTime();
		cal.add(Calendar.MINUTE, -30);//每半小时查询一次
		long from = cal.getTime().getTime();
		
		boolean hasMore = true;
		
		try {
			while(hasMore) {
				OrderResponse result = vipHelper.getOrders(status, from, end, pageSize, pageNo);
				logger.debug("[total]"+result.getTotal());
				totalAmount += result.getTotal();
				int totalPages = (result.getTotal()+pageSize-1)/pageSize;
				for(OrderInfo orderInfo:result.getOrderInfoList())
					syncOrder(orderInfo);
				if(totalPages > pageNo)
					pageNo++;
				else
					hasMore = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
	 * 查询订单
	 * 存储进入Arangodb
	 * 存储进入MySQL（写入后自动触发通知）
     */
    public void execute() throws JobExecutionException {
    	logger.info("order sync job start. " + new Date());
    		
    	//准备连接
    	arangoClient = new ArangoDbClient(host,port,username,password,database);
    	//1，查询已成交未结算订单
    	getOrders(1);//1-待结算
    	getOrders(2);//2-已结算
    	//2，查询已结算订单
    	//完成后关闭arangoDbClient
    	arangoClient.close();
    	
		if(totalAmount == 0)//啥活都没干，发啥消息
			return;
    		
		//来个甜点：发送处理结果到管理员，甜到齁死人的那种
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", Global.getConfig("default_tech_guy_openid"));//固定发送
		msg.put("title", "订单同步任务结果");
		msg.put("task", "唯品会订单数据 已同步");
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

    private double parseNumber(String numStr) {
		try {
			return Double.parseDouble(numStr);
		}catch(Exception ex) {
			logger.error("cannot parse double from input string.[numStr]"+numStr);
			return 0;
		}
}
    
    
}
