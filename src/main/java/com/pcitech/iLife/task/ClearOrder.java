package com.pcitech.iLife.task;

import java.sql.Types;
import java.text.DecimalFormat;
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
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.CommissionScheme;
import com.pcitech.iLife.modules.mod.entity.CreditScheme;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.entity.ProfitShareItem;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.CommissionSchemeService;
import com.pcitech.iLife.modules.mod.service.CreditSchemeService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.mod.service.ProfitShareItemService;
import com.pcitech.iLife.modules.mod.service.ProfitShareSchemeService;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;

import org.quartz.JobExecutionException;

@Service
public class ClearOrder {
    private static Logger logger = LoggerFactory.getLogger(ClearOrder.class);

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
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private ClearingService clearingService;
	@Autowired
	private OrderService orderService;
	
    // 记录处理条数
    long totalAmount = 0;
    long processedAmount = 0;
    List<String> source = new ArrayList<String>();
    Map<String,Integer> processedMap = null;

    public ClearOrder() {
    }

    /**
	 * 查询待同步订单记录，并执行清分
	 * 1，查询status=pending状态订单
	 * 2，逐条清分
	 * 3，处理完成后发送通知给管理者
     */
    public void execute() throws JobExecutionException {
    	logger.info("order clearing job start. " + new Date());
    	
    	Page<Order> page = new Page<Order>();
    	//page.setOrderBy("a.create_date desc");//后来的先清算
    	page.setPageNo(0);
    	page.setPageSize(20);//每页处理20条
    	
    	Order query = new Order();
    	query.setStatus("pending");
    	
    	int pageNo = 0;
    	boolean hasMore = true;
    	
    	//TODO 注意：在有多页时，数据会有差异。可能是系统bug，但不影响结果，需要等到下一次执行时再处理
    	while(hasMore) {
    		logger.debug("process by page.[pageNo]"+pageNo);
        	page = orderService.findPage(page, query);
        	totalAmount = page.getCount();
        	logger.debug("total orders.[amount]"+page.getCount());
        	List<Order> orders = page.getList();
        	for(Order order:orders)
        		clear(order);
        	
        	//翻页
        	pageNo++;
        	page.setPageNo(pageNo);
        	
        	//判断是否已经结束
        	if(pageNo>page.getTotalPage()-1)
        		hasMore=false;
    	}

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
		msg.put("title", "订单任务结果");
		msg.put("task", "新订单 已清分");
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



    /**
     * 清分  
     */
    private void clear(Order order) {
    	//预处理：如果订单broker为空，则统一认为是平台订单。各个平台有锁定收益规则，非直接订单pid为空
    	if(order.getBroker()==null || order.getBroker().getId()==null || order.getBroker().getId().trim().length()==0) {
    		order.setBroker(brokerService.get("system"));
    	}
    	
		//0，获取上级达人、上上级达人
		Map<String,String> brokers = getBrokers(order.getBroker());

	    //1，根据platform查询所有分润规则。当前不考虑category，均采用default分润规则
		//查询分润规则并根据2方分润计算3方分润
		ProfitShareScheme profitShareScheme = new ProfitShareScheme();
		profitShareScheme.setCategory("default");//不考虑category
		profitShareScheme.setPlatform(order.getPlatform());
		profitShareScheme.setType("order");//默认只查找订单分润的规则：注意，这个值是在字典里定义的，不能弄错了
		profitShareScheme = profitShareSchemeService.getByQuery(profitShareScheme);
		
		logger.debug("try to clear order with scheme.[scheme]"+profitShareScheme.getName());
		
		//注意：必须针对每个平台设置分润规则。这里直接使用。如果没有分润规则忽略
		if(profitShareScheme==null) {
			logger.error("cannot find profit share scheme for platform.[platform]"+order.getPlatform());
			return;
		}
		
		//查询分润规则下的详细条目。通常来说合计应该是100%
		ProfitShareItem queryItem = new ProfitShareItem();
		queryItem.setScheme(profitShareScheme);//针对当前scheme查询明细
		List<ProfitShareItem> items = profitShareItemService.findListByQuery(queryItem);
		
		logger.debug("try to clear order with scheme items.[count]"+items.size());
		
		//！！ 重要：需要进行保护，先检查所有条目合计不超过100%，否则报错
		double percentage = 0;
		if(percentage>1) {
			logger.error("fatal error. sum of profit share is over 100% ");
			//发消息给给运营
			//TODO 
			return;
		}
		//小于100，可以执行清分，但发警告信息
		if(percentage<1) {
			logger.warn("sum of profit share is less than 100%. please check. ");
			//发消息给给运营
			//TODO 
		}
		
		//逐条清算
		for(ProfitShareItem item:items) {
//			logger.debug("try to clear order.[order]"+order.getId()+"[schemeItem]"+item.getId()+"[type]"+item.getBeneficiaryType()+"[person]"+item.getBeneficiary());
            String beneficiary = item.getBeneficiary();
            String beneficiaryType = item.getBeneficiaryType();
            
            //开始计算
            String status = "cleared";
            Broker person = brokerService.get(brokers.get("broker"));//默认为当前broker
            double amount = item.getShare() * order.getCommissionEstimate()/100;//注意：这是是预估佣金
            DecimalFormat df = new DecimalFormat("#.00");//保留两位小数
            String amountStr = df.format(amount);

            if("team".equalsIgnoreCase(beneficiaryType)) { //beneficiaryType==team
         	   	status = "pending";//需要二次清分
         	   	person = brokerService.get(beneficiary);//对于团队绩效，直接使用团队标记：是一个达人账号
            }else{//beneficiaryType==person
				if("platform".equalsIgnoreCase(beneficiary)) { //如果是平台收入，则作为特殊情况处理
					person = brokerService.get("platform");//直接使用平台标记：是一个特殊的达人账户
				}else{//具体个人分润：推广达人、上级、上上级
					person = brokerService.get(brokers.get(beneficiary));//使用真实的ID填充
					//对上级、上上级检查是否已经达标，否则设置为锁定状态
					if("parent".equalsIgnoreCase(beneficiary) ||"grandpa".equalsIgnoreCase(beneficiary)) {
						if(brokerService.countChilds(person.getId())<15)//硬编码：检查是否达到15个下级达人
							status = "locked";
					}
				}
            }
            
            //写入清分记录
            Clearing clearing = new Clearing();
            String idStr = order.getId()+profitShareScheme.getId()+item.getId()+beneficiaryType+beneficiary+person;
            clearing.setId(Util.md5(idStr));//动态构建md5作为id
            clearing.setIsNewRecord(true);//重要：我们指定了ID，但还是要写到数据库的
            clearing.setPlatform(order.getPlatform());
            clearing.setOrder(order);
            clearing.setOrderTime(order.getOrderTime());
            clearing.setItem(order.getItem());
            clearing.setAmountOrder(df.format(order.getAmount()));//仅保留2位小数
            clearing.setAmountCommission(""+order.getCommissionEstimate());
            clearing.setAmountProfit(amountStr);
            clearing.setScheme(profitShareScheme);
            clearing.setSchemeItem(item);
            clearing.setShare(""+item.getShare());//直接写入百分比
            clearing.setSeller(order.getBroker());//添加成交达人信息
            clearing.setBeneficiary(person);
            clearing.setBeneficiaryType(beneficiaryType);
            clearing.setStatusClear(status);
            clearing.setStatusSettle("pending");
            clearing.setStatusCash("pending");
            clearing.setStatusNotify("pending");
            
            try {//由于ID为手动生成，避免重复清分
            	clearingService.save(clearing);
            }catch(Exception ex) {
            	//logger.error("save clearing record error.",ex.getMessage());
            	//do nothing
            }
		}
		
		//更新订单清分状态
		order.setStatus("cleared");
		orderService.save(order);
		
		//处理条数计数
		processedAmount++;
    }   

    /**
     * 查询当前达人的上级达人、上上级达人。如果缺失，则以本人填充    
     */
    private Map<String,String> getBrokers(Broker broker) {
    	String broker_id = broker.getId();
		Map<String,String> brokers = new HashMap<String,String>();
		brokers.put("broker", broker_id);
	    //1，查询上级达人
	    String parent_id = getParentBroker(broker_id);
	    brokers.put("parent", parent_id);

	    //2，查询上上级达人
	    String grandpa_id = getParentBroker(parent_id);
	    brokers.put("grandpa", grandpa_id);

	    return brokers;
    }   
    
    //获取上级达人。如果没有上级达人则设置为当前达人
    private String getParentBroker(String broker_id) {
	    Broker broker = brokerService.get(broker_id);
	    if ( broker!=null ) {
	        Broker parentBroker = broker.getParent();//上级达人ID
	        if(parentBroker == null  || parentBroker.getId().trim().length()==0)
	        		return broker_id;//如果parent 为空则返回达人自己
	        return parentBroker.getId();
	    }else {//如果没有上级达人则返回自己
    		logger.debug("has no parent broker.");
    		return broker_id;
	    }
    }
    
}
