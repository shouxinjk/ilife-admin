package com.pcitech.iLife.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.taobao.TaobaoHelper;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 查询达人从微信提交的新商品，并将转换完成后的token及链接发送给达人
 */
@Service
public class BrokerSeedNotifyTask {
    private static Logger logger = LoggerFactory.getLogger(BrokerSeedNotifyTask.class);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    TaobaoHelper taobaoHelper;
    @Autowired
    BrokerService brokerService;

    public BrokerSeedNotifyTask() {
    }

    /**
	 * 查询ArangoDB:broker-seeds，将已经完成采集的新入库商品发送通知
	 * 1，查询broker-seeds，每次限制10条，按照创建时间升序，条件为：status.parse==true && status.cps==true && status.profit==true && status.notify==false，得到达人openid，商品url，提交时间
	 * 2，根据达人openid查询达人id
	 * 3，查询my_stuff，条件为：_key == md5(url)，得到link.cps.达人ID.token，如果该值为空则判断，如果距离提交时间小于3分钟则忽略，否则获取 Link.token
	 * 4，发送通知信息给达人，使用文本信息发送
     */
    public void execute() throws JobExecutionException {
    		logger.info("start broker seed notify job. " + new Date());
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    		//1，查询待处理商品记录 返回itemKey、url、openid、create时间
    		String query = "for doc in broker_seeds filter "
        		//+ "doc.url != null "
        		//+ "and doc.status!=null and doc.status.parse==true and doc.status.`collect`==true and doc.status.cps==true and doc.status.profit==true and doc.status.notify==false "
        		+ "doc.status!=null and doc.status.notify==false "//直接查询所有未发送通知的内容，如果超过时间则直接发送失败信息
        		+ "limit 10 "//限定为10条
        		+ "return {itemKey:doc._key,url:doc.url,openid:doc.openid,createdOn:doc.timestamp.create,text:doc.text}";

        try {
            arangoClient = new ArangoDbClient(host,port,username,password,database);
            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
            for (BaseDocument item:items) {
            		//2,根据openid查询对应达人
            		Broker broker = brokerService.getByOpenid(item.getProperties().get("openid").toString());
            		//3,查询对应的商品详情
            		Map<String,Object> stuff = queryStuff(broker,item.getProperties().get("url").toString());
            		stuff.put("openid", item.getProperties().get("openid").toString());
            		//4,发送通知
            		Date createdOn = sdf.parse(item.getProperties().get("createdOn").toString());//获取seed创建时间
            		long duration = System.currentTimeMillis() - createdOn.getTime();//得到时间间隔
            		String token = "";//判断是否已经生成token，并且优先使用达人自己的token
            		if(stuff.get("tokenBroker") !=null && stuff.get("tokenBroker").toString().trim().length()>2 ) {//已经生成达人token，立即发送通知
            			token = stuff.get("tokenBroker").toString().trim();
            			stuff.put("token", token);
                		if(sendSuccessNotification(stuff)) {
                			updateBrokerSeed(item.getKey());//5，更新broker-seed状态
                		}
            		}else if(stuff.get("tokenSystem") !=null && stuff.get("tokenSystem").toString().trim().length()>2 ) {//达人口令尚未生成，但系统口令已经生成。最多等待3分钟
            			token = stuff.get("tokenSystem").toString().trim();
            			stuff.put("token", token);
            			if(duration>1*60*1000) {//如果已经过了1分钟还是未生成达人token，则直接发送系统token
                    		if(sendSuccessNotification(stuff)) {
                    			updateBrokerSeed(item.getProperties().get("itemKey").toString());//5，更新broker-seed状态
                    		}
            			}
            		}else if(duration > 3*60*1000) {//如果超过3分钟，则告诉达人，找不到对应的商品。同时发送服务器通知信息给管理员
                		if(sendFailNotification(item.getProperties())) {//注意参数是broker_seed
                			updateBrokerSeed(item.getProperties().get("itemKey").toString());//5，更新broker-seed状态：出错后也会更新状态，直接跳过
                		}
            		}else {//我们还是可以再等等
            			//do nothing
            		}
            }
			//完成后关闭arangoDbClient
			arangoClient.close();
        } catch (Exception e) {
            logger.error("Failed to query pending broker seeds.",e);
        }
    }
    
    /**
     * 查询得到相应于URL的商品详情，包括淘口令、标题、佣金等信息
     * @param broker 可以为null。如果为null则直接返回系统淘口令
     * @param url 商品URL
     */
    private Map<String,Object> queryStuff(Broker broker,String url) {
    		Map<String,Object> map = Maps.newHashMap();
    		//1，根据URL查询对应的商品详情，默认情况下使用系统淘口令。注意：佣金使用amount计算，由于分解佣金需要浏览详情界面才会生成
		String query = "for doc in my_stuff filter "
    		+ "doc._key ==\""+Util.md5(url)+"\" "
    		+ "return {itemKey:doc._key,tokenBroker:null,tokenSystem:doc.link.token,title:doc.title,profitOrder:doc.profit.order*0.5,profitTeam:doc.profit.team*0.1}";
		if(broker!=null && broker.getId().trim().length()>0) {//如果是达人则查询对应达人的淘口令
			query = "for doc in my_stuff filter "
		    		+ "doc._key ==\""+Util.md5(url)+"\" "
		    		+ "return {itemKey:doc._key,tokenBroker:doc.link.cps.`"+broker.getId()+"`.token,tokenSystem:doc.link.token,title:doc.title,profitOrder:doc.profit.order,profitTeam:doc.profit.team}";
		}
	    try {
	    		logger.debug("start query stuff by aql.[query]"+query);
	        List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
	        if(items.size()>0) {
	        		 map = items.get(0).getProperties();
	        }
	    } catch (Exception e) {
	        logger.error("Failed to query stuff by url.[url]"+url,e);
	    }
	    logger.debug("query stuff result.[data]"+map);
	    return map;
    }
    
    /**
     * 发送通知消息。消息内容为：
     * 亲，商品已上架，可以发送淘口令推广，或进入商品详情界面推广哦~~
     * 
     * @param item
     */
    private boolean sendSuccessNotification(Map<String,Object> item) {
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
	    
	    Gson gson = new Gson(); 
	    String jsonStr = gson.toJson(item);
	    JSONObject msg = JSONObject.parseObject(jsonStr);
	    logger.debug("convert to json.[JSON]"+msg);
	    
		//JSONObject msg =  JSONObject.parseObject(item.toString());//JSONObject.parseObject(JSON.toJSONString(item));
		JSONObject result = HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/broker-seed-success-notify", 
				msg,header);
        logger.info("Broker seed success notification job executed.[msg]" + msg);
        return result.getBooleanValue("status");
    }
    
    /**
     * 发送上架失败消息。在等待3分钟后，如果还未能得到淘口令，则认为上架失败
     * @param item
     * @return
     */
    private boolean sendFailNotification(Map<String,Object> item) {
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
	    
	    Gson gson = new Gson(); 
	    String jsonStr = gson.toJson(item);
	    JSONObject msg = JSONObject.parseObject(jsonStr);
	    logger.debug("convert to json.[JSON]"+msg);
	    
		//JSONObject msg = JSONObject.parseObject(item.toString()); //JSONObject.parseObject(JSON.toJSONString(item));
		JSONObject result = HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/broker-seed-fail-notify", 
				msg,header);
        logger.info("Broker seed fail notification job executed.[msg]" + msg);
        return result.getBooleanValue("status");
    }
    
    /**
     * 更新BrokerSeed状态。设置status.notify = true
     * @param itemKey
     */
    private void updateBrokerSeed(String itemKey) {
		logger.info("try to update broker seed item.[itemKey]"+itemKey);
		BaseDocument doc = new BaseDocument();
		Map<String,Object> notifyStatus = new HashMap<String,Object>();
		notifyStatus.put("notify", true);
		Map<String,Object> notifyTimestamp = new HashMap<String,Object>();
		notifyTimestamp.put("notify", new Date());			
		doc.setKey(itemKey);
		doc.getProperties().put("status", notifyStatus);
		doc.getProperties().put("timestamp", notifyTimestamp);
		arangoClient.update("broker_seeds", itemKey, doc);
    }
}
