package com.pcitech.iLife.task;

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
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse.OrderListGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse.OrderListGetResponseOrderListItem;

import org.quartz.JobExecutionException;

/**
 * 定时通过API接口获取订单信息。
 * 订单数据按照以下规则同步：
 * 1，原始数据直接存入Arangodb，用于追溯
 * 2，同时同步到MySQL，用于通知与汇总
 */
@Service
public class PddOrderSync {
    private static Logger logger = LoggerFactory.getLogger(PddOrderSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    PddHelper pddHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    BrokerService brokerService;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public PddOrderSync() {
    }
    
    private void syncOrder(OrderListGetResponseOrderListItem item) {
    	//itemKey是受控的，自动生成一个
		String itemKey = Util.md5("pdd"+item.getOrderSn());
		
		//管他三七二十一，全部存储
		Map<String,Object> props = JSONObject.parseObject(JSON.toJSONString(item),new TypeReference<Map<String,Object>>(){});
		
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);//装上我们自己定义的识别ID，避免多个平台间ID冲突
		doc.setProperties(props);//设置所有原始订单信息
		doc.getProperties().put("timestamp", new Date());//加一个我们自己的同步时间戳
		doc.getProperties().put("source", "pdd");//标记来源
		
		//写入 arangodb
		arangoClient.upsert("order",itemKey, doc);    
		
		//接下来写入Order
		Order order = orderService.get(itemKey);
		if(order == null) {
			order = new Order();
			order.setId(itemKey);//与NoSQL保持一致
			order.setIsNewRecord(true);//新建记录
		}
		order.setStatus("pending");//等待清分
		order.setPlatform("pdd");
		order.setOrderNo(""+item.getOrderSn());
		order.setTraceCode(item.getCustomParameters());
		order.setAmount(Double.parseDouble(String.format("%.2f",item.getOrderAmount()*0.01)));//返回为分的long型，仅保留2位小数
		order.setCommissionEstimate(Double.parseDouble(String.format("%.2f",item.getPromotionAmount()*0.01)));//返回为分的long型，仅保留2位小数
//		order.setCommissionSettlement();//留空，等着后面结算更新
		order.setItem(item.getGoodsName());
		order.setOrderTime(new Date(item.getOrderPayTime()*1000));//以订单支付时间为准
		//customParameter格式为：{"uid":"20434335","brokerId":"o8HmJ1EdIUR8iZRwaq1T7D_nPIYc"}
		String brokerId = "system";
		Map<String,String> customParam = JSONObject.parseObject(item.getCustomParameters(),new TypeReference<Map<String,String>>(){});
		if(customParam.get("brokerId")!=null)
			brokerId = customParam.get("brokerId");
		Broker broker = brokerService.get(brokerId);//跟踪码就是达人ID：注意需要解析自定义参数：构建链接时传递参数为{uid:xx,brokerId:xxxx}
		if(broker==null)broker=brokerService.get("system");//如果找不到，则直接使用平台默认账户
		order.setBroker(broker);
		order.setNotification("0");//不用管通知状态，后续通知任务会自动更新
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
    	logger.info("Pinduoduo item sync job start. " + new Date());
    		
    	//1，调用接口查询订单列表
    	//先来头盘，如果调用失败就自己锤自己
    	List<OrderListGetResponseOrderListItem> orders = null;
    	try {
    		orders = pddHelper.getOrders();
		} catch (Exception ex) {//搞毛线啊，这个是接口调用错误，直接退出，等着挨捶吧
			logger.error("failed query order.",ex);	
			return;
		}
    	
    	//再来开胃菜，如果没数据就去捶运营
    	if(orders==null || orders.size()==0) {//接口是调用上了，但没数据，赶紧去捶运营吧，让他们去促成订单
    		logger.warn("no order found.[timestamp]"+new Date());
    		return;
    	}
    	
    	//接下来才是正菜
    	totalAmount = orders.size();
    	arangoClient = new ArangoDbClient(host,port,username,password,database);
    	for(OrderListGetResponseOrderListItem order:orders) {
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
		msg.put("task", "拼多多订单数据 已同步");
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
