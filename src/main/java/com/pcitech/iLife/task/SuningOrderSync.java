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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.arangodb.entity.BaseDocument;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.JdHelper;
import com.pcitech.iLife.cps.KaolaHelper;
import com.pcitech.iLife.cps.SuningHelper;
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
public class SuningOrderSync {
    private static Logger logger = LoggerFactory.getLogger(SuningOrderSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    SuningHelper suningHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    BrokerService brokerService;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public SuningOrderSync() {
    }
    
    /**
数据结构参考：https://open.suning.com/ospos/apipage/toApiMethodDetailMenuNew.do?interCode=suning.netalliance.orderinfo.query#
{
      "queryOrderinfo": [
        {
          "timeInfo": {
            "updateDate": "1578987802000",
            "orderTime": "1578987802000",
            "settleTime": "1578985200000",
            "payTime": "1578987814000",
            "refundTime": "1578985200000",
            "orderFinishTime": "1578985200000"
          },
          "orderId": "38404277469",
          "orderStatus": {
            "traceId": "2770",
            "itemPicUrl": "https://uimgpre.cnsuning.com/uimg/b2c/newcatentries/0010001242-000000000121307256_1.jpg_800w_800h_4e",
            "itemTitle": "Apple iPhone 6 Plus-下单后改名",
            "settleStatus": "0",
            "orderStatus": {
              "traceId": "2770",
              "itemPicUrl": "https://uimgpre.cnsuning.com/uimg/b2c/newcatentries/0010001242-000000000121307256_1.jpg_800w_800h_4e",
              "itemTitle": "Apple iPhone 6 Plus-下单后改名",
              "settleStatus": "0",
              "orderStatus": "2",
              "pid": "101220022",
              "relShopId": "7018120455",
              "parentId": "38404172416",
              "itemNum": "1",
              "relItemId": "121307256",
              "itemPrice": "2770",
              "subSideRate": "10",
              "promotion": "0"
            },
            "pid": "101220022",
            "relShopId": "7018120455",
            "parentId": "38404172416",
            "itemNum": "1",
            "relItemId": "121307256",
            "itemPrice": "2770",
            "subSideRate": "10",
            "promotion": "0"
          },
          "orderPrice": "2770",
          "amountInfo": {
            "totalAmount": "2770",
            "commissionRate": "21",
            "estimateCosPrice": "0",
            "payAmount": "2770",
            "platformEstimateCosPrice": "277",
            "platformCommissionRate": "platformCommissionRate",
            "settleAmount": "2770"
          },
          "buyerId": "1578985200000",
          "closeType": "1",
          "payStatus": "0"
        }
      ]
    }
     */
    private void syncOrder(JSONObject item) {
    	//itemKey是受控的，自动生成一个
		String itemKey = Util.md5("suning"+item.getString("orderId"));
		
		//管他三七二十一，全部存储
		Map<String,Object> props = JSONObject.parseObject(JSON.toJSONString(item),new TypeReference<Map<String,Object>>(){});
		
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);//装上我们自己定义的识别ID，避免多个平台间ID冲突
		doc.setProperties(props);//设置所有原始订单信息
		doc.getProperties().put("timestamp", new Date());//加一个我们自己的同步时间戳
		
		//写入 arangodb
		arangoClient.insert("order", doc);    
		
		//接下来写入Order
		Order order = new Order();
		order.setId(itemKey);//与NoSQL保持一致
		order.setPlatform("suning");
		order.setOrderNo(item.getString("orderId"));
		order.setTraceCode(item.getJSONObject("orderStatus").getString("traceId"));
		order.setAmount(Long.parseLong(item.getJSONObject("amountInfo").getString("payAmount"))/100);//单位是分:以实际支付金额为准
		order.setCommissionEstimate(Long.parseLong(item.getJSONObject("amountInfo").getString("estimateCosPrice"))/100);//单位是分
		if("1".equalsIgnoreCase(item.getJSONObject("orderStatus").getString("settleStatus"))) {//订单状态为1-已结算则为实际结算值
			order.setCommissionSettlement(Long.parseLong(item.getJSONObject("amountInfo").getString("settleAmount"))/100);//单位是分
		}
		order.setItem(item.getJSONObject("orderStatus").getString("itemTitle"));
		order.setOrderTime(new Date(Long.parseLong(item.getJSONObject("timeInfo").getString("payTime"))));//以付款时间为准
		Broker broker = brokerService.get(item.getJSONObject("orderStatus").getString("traceId"));//traceId就是达人ID
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
    	JSONArray orders = suningHelper.getOrders();
    	
    	//再来开胃菜，如果没数据就去捶运营
    	if(orders==null || orders.size()==0) {//接口是调用上了，但没数据，赶紧去捶运营吧，让他们去促成订单
    		logger.warn("no order found.[timestamp]"+new Date());
    		return;
    	}
    	
    	//接下来才是正菜
    	totalAmount = orders.size();
    	arangoClient = new ArangoDbClient(host,port,username,password,database);
    	for(int i=0;i<orders.size();i++) {
    		syncOrder(orders.getJSONObject(i));
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
		msg.put("task", "苏宁订单数据 已同步");
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
