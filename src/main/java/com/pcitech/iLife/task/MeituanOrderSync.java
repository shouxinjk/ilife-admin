package com.pcitech.iLife.task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import com.pcitech.iLife.cps.GomeHelper;
import com.pcitech.iLife.cps.JdHelper;
import com.pcitech.iLife.cps.KaolaHelper;
import com.pcitech.iLife.cps.MeituanHelper;
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
import com.pdd.pop.sdk.common.util.JsonUtil;

import org.quartz.JobExecutionException;

/**
 * 定时通过API接口获取订单信息。
 * 订单数据按照以下规则同步：
 * 1，原始数据直接存入Arangodb，用于追溯
 * 2，同时同步到MySQL，用于通知与汇总
 */
@Service
public class MeituanOrderSync {
    private static Logger logger = LoggerFactory.getLogger(MeituanOrderSync.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    MeituanHelper meituanHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    BrokerService brokerService;
    
    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;

    public MeituanOrderSync() {
    }
    
    private void syncOrder(String category,JSONObject item) {
    	//itemKey是受控的，自动生成一个
		String itemKey = Util.md5("meituan"+item.getString("orderid"));
		
		//管他三七二十一，全部存储
		Map<String,Object> props = JSONObject.parseObject(JSON.toJSONString(item),new TypeReference<Map<String,Object>>(){});
		
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);//装上我们自己定义的识别ID，避免多个平台间ID冲突
		doc.setProperties(props);//设置所有原始订单信息
		doc.getProperties().put("timestamp", new Date());//加一个我们自己的同步时间戳
		doc.getProperties().put("source", "meituan");//标记来源
		
		//写入 arangodb
		arangoClient.insert("order", doc);    
		
		//接下来写入Order
		Order order = new Order();
		order.setStatus("pending");//等待清分
		order.setId(itemKey);//与NoSQL保持一致
		order.setPlatform("meituan");
		order.setOrderNo(item.getString("orderid"));
		order.setTraceCode(item.getString("sid"));//对应的推广位
		order.setAmount(item.getDoubleValue("payprice"));
		order.setCommissionEstimate(item.getDoubleValue("profit"));//TODO:注意CPA佣金这里未能体现
//		order.setCommissionSettlement(item.getDoubleValue("profit")-item.getDoubleValue("refundprofit"));//扣除退款佣金后作为结算金额
		order.setItem(item.getString("smstitle"));
		Date orderTime = new Date(item.getLongValue("paytime")*1000);//返回的是10位秒数
		order.setOrderTime(orderTime);
		Broker broker = brokerService.get(item.getString("sid"));
		if(broker==null)broker=brokerService.get("system");//如果找不到，则直接使用平台默认账户
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
    		
		//TODO 订单类型：0 团购订单，2 酒店订单，4 外卖订单，5 话费&团好货订单，6 闪购订单，8 优选订单
		String[] poolNames = {"优选","酒店","外卖","闪购","团购"};//当前仅接入了8：优选
		String[] poolIds = {"8","2","4","6","0"};
		
		int pageSize = 100;//默认每页返回100条
        int poolNameIndex = 0;
        
    	arangoClient = new ArangoDbClient(host,port,username,password,database);
    	
		for(String poolName:poolNames) {//逐个分类查询，每个分类均进行遍历
			logger.debug("start check order by poolName.[poolName]"+poolName);
			int pageNo = 1;
			boolean hasNextPage = true;
			while(hasNextPage) {//至少会有一页，先查查看
				logger.debug("start process search result by page.[pageNo]"+pageNo);
    			JSONObject resp = meituanHelper.getOrders(poolIds[poolNameIndex], pageSize, pageNo);
				int totalAmountPerPool = resp.getIntValue("total");
	    		totalAmount += totalAmountPerPool;
	    		int totalPages = (totalAmountPerPool + pageSize -1)/pageSize;
	    		if(pageNo>totalPages)
	    			hasNextPage = false;
	    		//逐条处理
    			logger.debug("try to deal with item detail.[resp]"+JsonUtil.transferToJson(resp));
    			JSONArray itemArray = resp.getJSONArray("dataList");
    			if(itemArray == null) {//没有订单则忽略
    				logger.error("no order found.");
    			}else {
					for(int j=0;j<itemArray.size();j++) {//逐个插入
						JSONObject item = itemArray.getJSONObject(j);
						logger.debug(JsonUtil.transferToJson(item));
						syncOrder(poolName,item);
					}
					pageNo++;//自动翻页
    			}
			}
			poolNameIndex++;//多个分类遍历
		}

    	//完成后关闭arangoDbClient
    	arangoClient.close();
    		
    	if(totalAmount == 0) {//没有订单就别发通知了，去找运营吧
    		logger.debug("no order found.");
    		return;
    	}
    	
		//来个甜点：发送处理结果到管理员，甜到齁死人的那种
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", Global.getConfig("default_tech_guy_openid"));//固定发送
		msg.put("title", "订单同步任务结果");
		msg.put("task", "美团订单数据 已同步");
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
