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
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.JdHelper;
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
public class JdOrderSync {
    private static Logger logger = LoggerFactory.getLogger(JdOrderSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    JdHelper jdHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    BrokerService brokerService;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public JdOrderSync() {
    }
    
    private void syncOrder(OrderRowResp item) {
    	//itemKey是受控的，自动生成一个:根据父订单号ID+子订单号ID+SkuId生成
		String itemKey = Util.md5("jd"+item.getOrderId()+item.getParentId()+item.getSkuId());
		
		//管他三七二十一，全部存储
		Map<String,Object> props = JSONObject.parseObject(JSON.toJSONString(item),new TypeReference<Map<String,Object>>(){});
		
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);//装上我们自己定义的识别ID，避免多个平台间ID冲突
		doc.setProperties(props);//设置所有原始订单信息
		doc.getProperties().put("timestamp", new Date());//加一个我们自己的同步时间戳
		doc.getProperties().put("source", "jd");//标记来源
		
		//写入 arangodb
		try {
			arangoClient.insert("order", doc);    
		}catch(Exception ex) {
			logger.error("failed insert order info.",ex);
		}
		
		//先查看是否已经入库：仅对未入库订单操作
		Order order = orderService.get(itemKey);
		if(order == null) {//仅对新订单创建记录
			//接下来写入Order
			order = new Order();
			order.setStatus("pending");//等待清分
			order.setId(itemKey);//与NoSQL保持一致
			order.setIsNewRecord(true);
			order.setPlatform("jd");
			order.setOrderNo(""+item.getOrderId());
			order.setTraceCode(item.getExt1());
			order.setAmount(item.getPrice());
			order.setCommissionEstimate(item.getEstimateFee());
			order.setCommissionSettlement(item.getActualFee());//可能为空，没关系，等着后面结算更新
			order.setItem(item.getSkuName());
			long timestamp = System.currentTimeMillis();
			try {timestamp = Long.parseLong(item.getOrderTime());}catch(Exception ex){}//"1529271683000"，直接取值
			order.setOrderTime(new Date(timestamp));
			Broker broker = brokerService.get(item.getExt1());//跟踪码就是达人ID
			if(broker==null)broker=brokerService.get("system");//如果找不到，则直接使用平台默认账户
			order.setBroker(broker);
			order.setNotification("0");//不用管通知状态，后续通知任务会自动更新
			order.setStatus("pending");
			
			orderService.save(order);
			
			processedAmount++;
		}
		
    }

    /**
	 * 查询订单
	 * 存储进入Arangodb
	 * 存储进入MySQL（写入后自动触发通知）
     */
    public void execute() throws JobExecutionException {
    	logger.info("Pinduoduo item sync job start. " + new Date());
    		
    	//1，调用接口查询订单列表
    	//先来头盘，如果调用失败就自己锤自己
    	OrderRowResp[] orders = null;
    	try {
			//获取指定时间段前30分钟的订单：用于手动修复数据时
//			Calendar cal = Calendar.getInstance();
//			cal.set(Calendar.YEAR, 2022);
//			cal.set(Calendar.MONTH, 8);//月份，开始为0
//			cal.set(Calendar.DATE, 3);//日期，开始为1
//			cal.set(Calendar.HOUR, 11);//24小时时间
//			cal.set(Calendar.MINUTE, 5);
//			orders = jdHelper.getOrder(cal);
			
			//获取当前时间段前30分钟订单
    		orders = jdHelper.getOrder();
		} catch (Exception ex) {//搞毛线啊，这个是接口调用错误，直接退出，等着挨捶吧
			logger.error("failed query order.",ex);	
			return;
		}
    	
    	//再来开胃菜，如果没数据就去捶运营
    	if(orders==null || orders.length==0) {//接口是调用上了，但没数据，赶紧去捶运营吧，让他们去促成订单
    		logger.warn("no order found.[timestamp]"+new Date());
    		return;
    	}
    	
    	//接下来才是正菜
    	totalAmount = orders.length;
    	arangoClient = new ArangoDbClient(host,port,username,password,database);
    	for(OrderRowResp order:orders) {
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
		msg.put("openid", Global.getConfig("default_tech_guy_openid"));//固定发送
		msg.put("title", "订单同步任务结果");
		msg.put("task", "京东订单数据 已同步");
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
