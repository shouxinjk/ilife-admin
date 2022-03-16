package com.pcitech.iLife.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.CommissionScheme;
import com.pcitech.iLife.modules.mod.entity.CreditScheme;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.entity.ProfitShareItem;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.CommissionSchemeService;
import com.pcitech.iLife.modules.mod.service.CreditSchemeService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.mod.service.ProfitShareItemService;
import com.pcitech.iLife.modules.mod.service.ProfitShareSchemeService;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.NumberUtil;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Service
public class CalcProfit {
    private static Logger logger = LoggerFactory.getLogger(CalcProfit.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
	@Autowired
	private DictService dictService;
	@Autowired
	private CommissionSchemeService commissionSchemeService;
	@Autowired
	private CreditSchemeService creditSchemeService;
	@Autowired
	private ProfitShareSchemeService profitShareSchemeService;
	@Autowired
	private ProfitShareItemService profitShareItemService;
    

    // 记录处理条数
    int totalAmount = 0;
    int processedAmount = 0;
    List<String> source = new ArrayList<String>();
    Map<String,Integer> processedMap = null;

    public CalcProfit() {
    }
	
    private void calculate(BaseDocument item) {
		String itemKey = item.getProperties().get("itemKey").toString();
		logger.debug("calculate 1-party profit.[itemKey]"+itemKey);
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		//状态更新
		Map<String,Object> status = new HashMap<String,Object>();
		status.put("index", "pending");//重新提交“待索引”：需要在cps链接生成后会再次提交
		status.put("monitize", "ready");//分佣计算完成
		doc.getProperties().put("status", status);
		//时间戳更新
		Map<String,Object> timestamp = new HashMap<String,Object>();
		timestamp.put("monitize", new Date());//分佣计算完成时间
		doc.getProperties().put("timestamp", timestamp);
		//计算二方分润
		double price = 0;
		try{
			//部分情况下数据采集带有千分位分隔符，直接去掉
			price = Double.parseDouble(item.getProperties().get("price").toString().replaceAll(",", ""));
		}catch(Exception ex) {
			logger.error("Item price cannot be parsed.[price]"+item.getProperties().get("price"));		}
		String platform = item.getProperties().get("source").toString();
		String category = item.getProperties().get("category").toString();
		Map<String, Object>  profit = getProfit(platform,category,price);
		profit.put("type", "3-party");
		doc.getProperties().put("profit", profit);
		//根据source类型分别处理链接
		processedMap.put(platform, processedMap.get(platform)+1);
		
		//更新doc
		arangoClient.update("my_stuff", itemKey, doc);    	
		processedAmount++;
    }

    /**
	 * 查询待同步数据记录，并提交查询商品信息
	 * 1，查询Arangodb中(link.web2==null) || (link.web2 == link.web)的记录，限制30条。
	 * 2，通过拼多多API接口查询生成商品导购链接
	 * 3，逐条解析，并更新Arangodb商品记录
	 * 4，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    		logger.info("1-party calc job start. " + new Date());
    		source = commissionSchemeService.getCommissionSources();//获取所有支持的source
    		//初始化处理结果记录器
    		processedMap = new HashMap<String,Integer>();
        arangoClient = new ArangoDbClient(host,port,username,password,database);
        for(String s:source) {
        		processedMap.put(s, 0);

	    		//1，查询待处理商品记录 :使用hash索引：source-pricebid-profittype
	        String query = "for doc in my_stuff filter "
	        		+ "doc.source == \""+s+"\" and "        		
//					+ "doc.price.sale!=null and " //仅对于有price.sale才处理 
	        		+ "doc.status.monetize == \"pending\" "//根据状态查询，优先使用枚举值使用索引查询，需要同时完成对初始profit.type的设置
//	        		+ "update doc with {status:{monetize:\"ready\"}} in my_stuff "
	        		+ "limit 20 "//一个批次处理100条
	        		+ "return {itemKey:doc._key,source:doc.source,category:doc.categoryId==null?\"\":doc.categoryId,price:doc.price.sale}";
	        logger.error("try to query pending 1-party items.[query]"+query);
	        try {
	            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
	            totalAmount += items.size();
	            if(totalAmount ==0) {//如果没有了就提前收工
		            	logger.debug("没有待计算1-party分润的商品条目.[source]"+s);
		            	continue;//某个单个来源没有待计算条目，直接跳过
	//	            	arangoClient.close();//链接还是要关闭的
	//	            	return;
		        }
	            for (BaseDocument item:items) {
	            		calculate(item);//逐条计算并更新ArangoDB doc
	            }
	        } catch (Exception e) {
	            logger.error("Failed to execute query.",e);
	        }
        }
		//完成后关闭arangoDbClient
		arangoClient.close();
		if(totalAmount == 0)//啥活都没干，发啥消息
			return;
		//组装通知信息
		StringBuffer remark = new StringBuffer();
		remark.append("总数："+totalAmount);
		for(String s:source) {
			int numPerSource = processedMap.get(s);
			if(numPerSource>0)
				remark.append("\n"+s+"："+numPerSource);
		}
		remark.append("\n数量差异："+(totalAmount-processedAmount));
		
		//发送处理结果到管理员
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", "o8HmJ1EdIUR8iZRwaq1T7D_nPIYc");//固定发送
		msg.put("title", "分润计算任务结果");
		msg.put("task", "1-party分润 已同步");
		msg.put("time", fmt.format(new Date()));
		msg.put("remark", remark);
		msg.put("color", totalAmount-processedAmount==0?"#FF0000":"#000000");

		logger.error("pending notification message.[body]",msg);
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

	public Map<String, Object> getProfit(String source, String category, double price) {
		Map<String, Object> map = Maps.newHashMap();
		
		//查询佣金规则
		CommissionScheme commission = new CommissionScheme();
		commission.setCategory(category);
		commission.setPlatform(source);
		commission = commissionSchemeService.getByQuery(commission);
		
		//如果没有根据类目设置佣金则默认使用平台佣金规则
		if(commission == null) {
			map.put("warn-commission", "use platform default commission scheme");
			commission = new CommissionScheme();
			commission.setCategory("default");
			commission.setPlatform(source);
			commission = commissionSchemeService.getByQuery(commission);
		}
		
		if(commission == null) {//如果没有佣金规则，那当然就不算钱了，但是积分还是要算的
			map.put("warn-commission", "no commission scheme");
		}else {//如果有佣金规则，就计算佣金总额
			double amount  = 0;
			if("absolute".equalsIgnoreCase(commission.getType())) {//如果是固定金额，则直接用固定金额
				amount = commission.getAmount();
				map.put("rate", -1);//固定佣金rate设置为-1
			}else {//如果是百分比，则计算得到。作为默认规则
				amount = commission.getAmount()*price/100;
				map.put("rate", NumberUtil.getInstance().parseNumber(commission.getAmount()));
			}
			
			//查询分润规则并进行二次计算
			ProfitShareScheme profitShareScheme = new ProfitShareScheme();
			profitShareScheme.setCategory(category);
			profitShareScheme.setPlatform(source);
			profitShareScheme.setType("order");//默认只查找订单分润的规则：注意，这个值是在字典里定义的，不能弄错了
			profitShareScheme = profitShareSchemeService.getByQuery(profitShareScheme);
			
			//如果无针对指定类别的分润规则，则直接该平台的默认分润规则
			if(profitShareScheme==null) {//如果无针对指定类别的分润规则，则直接该平台的默认分润规则
				map.put("warn-profit", "use platform default profit share scheme");
				profitShareScheme = new ProfitShareScheme();
				profitShareScheme.setCategory("default");
				profitShareScheme.setPlatform(source);//仅根据source查找分润规则
				profitShareScheme.setType("order");//默认只查找订单分润的规则：注意，这个值是在字典里定义的，不能弄错了
				profitShareScheme = profitShareSchemeService.getByQuery(profitShareScheme);
			}
			
			if(profitShareScheme==null) {//如果没有分润规则，那也没法算钱啊。快找运营去设置
				map.put("warn-profit", "no profit share scheme");
			}else {//那就找分润明细，并计算达人分润和上级分润
				ProfitShareItem profitShareItem = new ProfitShareItem();
				profitShareItem.setScheme(profitShareScheme);
				profitShareItem.setBeneficiaryType("person");//个人分润
				profitShareItem.setBeneficiary("broker");//特定给推广者
				//查找并计算达人分润
				ProfitShareItem brokerShare = profitShareItemService.getByQuery(profitShareItem);
				if(brokerShare ==null) {//我还能说什么，运营把推广达人的分润都忘到九霄云网了，去找他
					map.put("warn-broker", "no broker profit share item");
				}else {//计算达人的分润金额，并设置店返
					double shareAmount = amount*brokerShare.getShare()/100;
					map.put("order", NumberUtil.getInstance().parseNumber(shareAmount));
				}
				//查找并计算上级达人分润
				profitShareItem.setBeneficiary("parent");//特定给上级达人
				ProfitShareItem parentBrokerShare = profitShareItemService.getByQuery(profitShareItem);
				if(brokerShare ==null) {//彻底无语了，上级达人的分润也没设置
					map.put("warn-parent", "no parent broker profit share item");
				}else {//计算上级达人的分润金额，并设置店返
					double shareAmount = amount*parentBrokerShare.getShare()/100;
					map.put("team", NumberUtil.getInstance().parseNumber(shareAmount));
				}
			}
		}
		
		//查询积分规则，并进行脚本计算，样例如下：
		//注意：脚本中字符串操作需要用单引号，双引号表示模板消息，会出现解析错误
		//return price*0.01
		CreditScheme creditScheme = new CreditScheme();
		creditScheme.setPlatform(source);
		creditScheme.setCategory(category);
		creditScheme = creditSchemeService.getByQuery(creditScheme);
		String script = "return price";//默认积分与价格相同
		
		//如果没有特定品类积分规则，则使用平台积分规则
		if(creditScheme == null) {
			map.put("warn-credit", "no credit scheme. use platform default credit scheme");
			creditScheme = new CreditScheme();
			creditScheme.setPlatform(source);
			creditScheme.setCategory("default");
			creditScheme = creditSchemeService.getByQuery(creditScheme);
		}
		
		if(creditScheme == null) {//不用说了，运营没设置积分规则。直接采用与价格相等作为积分
			map.put("warn-credit", "no credit scheme. use default one let credit=price");
		}else {//否则就获取脚本算积分吧
			script = creditScheme.getScript();
		}
		Binding binding = new Binding();
		binding.setVariable("source",source);
		binding.setVariable("category",category);
		binding.setVariable("price",price);
		try {
	        GroovyShell shell = new GroovyShell(binding);
	        Object value = shell.evaluate(script);//计算得到积分
	        map.put("credit", NumberUtil.getInstance().parseNumber(value.toString()));
		}catch(Exception ex) {//如果计算发生错误也使用默认链接
			map.put("error-script", ex.getMessage());
		}
		
        return map;
	}
    
}
