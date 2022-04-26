package com.pcitech.iLife.task;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jd.open.api.sdk.internal.JSON.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.utils.IdGen;
import com.pcitech.iLife.cps.VipHelper;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.vip.adp.api.open.service.GoodsInfo;
import com.vip.adp.api.open.service.GoodsInfoResponse;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 通过API接口，搜索得到在推广商品。
 * 通过自动任务触发，每天执行一次即可。遍历所有新增的商品
 * 
 */
@Service
public class VipItemSearcher {
    private static Logger logger = LoggerFactory.getLogger(VipItemSearcher.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    
    @Autowired
    VipHelper vipHelper;
    @Autowired
	private PlatformCategoryService platformCategoryService;
    
    // 记录处理条数
    long totalAmount = 0;
    long processedAmount = 0;
    Map<String,Long> processedMap = null;
    Map<String,Long> totalMap = null;
    Map<String,String> poolNameMap = null;
    
    String brokerId = "system";//默认设置为default
    
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;

    public VipItemSearcher() {
    	nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
    }
    
    private void upsertItem(String poolName,GoodsInfo item) {
		String  url = item.getDestUrl();//得到唯一URL
		String itemKey = Util.md5(url);//根据URL生成唯一key
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		doc.setKey(itemKey);
		doc.getProperties().put("url", url);
		Map<String,Object> seller = new HashMap<String,Object>();
		seller.put("name", item.getStoreInfo().getStoreName());
		doc.getProperties().put("seller", seller);	
		Map<String,Object> distributor = new HashMap<String,Object>();
		distributor.put("name", "唯品会");
		doc.getProperties().put("distributor", distributor);	
		Map<String,Object> link = new HashMap<String,Object>();
		link.put("web", url);
		link.put("wap", url);//移动端URL保持与web一致
		//注意：此接口返回值内没有导购链接，需要二次调用生成补充
		doc.getProperties().put("link", link);		
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-vipItemsSearcher");
		task.put("executor", "robot-vipItemsSearcher-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", url);
		doc.getProperties().put("task", task);
		doc.getProperties().put("type", "commodity");
		doc.getProperties().put("source", "vip");
		
		doc.getProperties().put("goodsId", item.getGoodsId());//记录goodsId，后面需要根据该数值查询详情
		doc.getProperties().put("title", item.getGoodsName());//更新title
		
		List<String> tags = new ArrayList<String>();
		tags.add(item.getCat1stName());
		tags.add(item.getCat2ndName());
		tags.add(item.getCategoryName());
		//是否海淘 
		if(item.getHaiTao()==1) {
			tags.add("海淘");
		}
		doc.getProperties().put("tags", tags);
		
		//更新品牌信息到prop列表
		Map<String,String> props = new HashMap<String,String>();
		props.put("品牌", item.getBrandName());//增加品牌属性
		doc.getProperties().put("props", props);
		
		//更新图片列表：注意脚本中已经有采集，此处使用自带的内容
		List<String> images = new ArrayList<String>();
		images.add(item.getGoodsMainPicture());//列表中仅返回主图，直接加到图片列表
		doc.getProperties().put("images", images);
		
		//将第一张展示图片作为logo
		doc.getProperties().put("logo", item.getGoodsMainPicture());
//		doc.getProperties().put("logo", item.getImageInfo().getImageList()[0].getUrl());//图片列表内第一张为主图，作为logo
		
		//如果有documentInfo，则作为summary
		if(item.getGoodsDesc()!=null && item.getGoodsDesc().trim().length()>0)
			doc.getProperties().put("summary", item.getGoodsDesc());
		
		//增加类目
//		List<String> categories = new ArrayList<String>();
//		categories.add(item.getCat1stName());
//		categories.add(item.getCat2ndName());
//		categories.add(item.getCategoryName());
//		doc.getProperties().put("category", categories);//更新类目，包含多级分类
		
		String category = item.getCat1stName();
		category += " "+item.getCat2ndName();
		category += " "+item.getCategoryName();
		doc.getProperties().put("category", category);//更新类目，包含多级分类
		
		//检查类目映射
		PlatformCategory query = new PlatformCategory();
		query.setName(category);
		query.setPlatform("vip");
		List<PlatformCategory> list = platformCategoryService.findMapping(query);
		if(list.size()>0) {//有则更新
			Map<String,Object> meta = new HashMap<String,Object>();
			meta.put("category", list.get(0).getCategory().getId());
			meta.put("categoryName", list.get(0).getCategory().getName());
			doc.getProperties().put("meta", meta);	
		}else {//否则写入等待标注
			query.setIsNewRecord(true);
			query.setId(Util.md5("vip"+category));//采用手动生成ID，避免多次查询生成多条记录
			query.setPlatform("vip");
			query.setCreateDate(new Date());
			query.setUpdateDate(new Date());
			platformCategoryService.save(query);
			//检查是否支持无类目映射入库
			if(!"true".equalsIgnoreCase(Global.getConfig("sx.enhouseWithoutCategoryMapping"))) {
				return;
			}
		}

		//更新价格：直接覆盖
		Map<String,Object> price = new HashMap<String,Object>();
		price.put("currency", "￥");
		price.put("bid", parseNumber(item.getMarketPrice()));//元
		price.put("sale", parseNumber(item.getVipPrice()));//元
		if(item.getCouponInfo()!=null) {//取第一个优惠金额
			price.put("coupon",  parseNumber(item.getCouponInfo().getFav()));//元
		}
		doc.getProperties().put("price", price);
		
		//更新佣金：直接覆盖
		Map<String,Object> profit = new HashMap<String,Object>();
		profit.put("rate", parseNumber(item.getCommissionRate()));//返回的是百分比，直接使用即可
		profit.put("amount", parseNumber(item.getCommission()));//元
		profit.put("type", "2-party");
		doc.getProperties().put("profit", profit);
		
		//设置状态。注意，需要设置sync=pending 等待计算CPS链接
		//状态更新
		Map<String,Object> status = new HashMap<String,Object>();
		status.put("crawl", "ready");
		status.put("sync", "pending");//等待生成CPS链接
		status.put("load", "pending");
		status.put("classify", "pending");
		status.put("satisify", "pending");//这个要在classify之后才执行
		status.put("measure", "pending");
		status.put("evaluate", "pending");
		status.put("monitize", "pending");//等待计算3-party分润
		status.put("poetize", "pending");//实际上这个要在classify之后才执行
		status.put("index", "pending");//先入库一次，能够立即看到：注意这时候没有CPS，不能推广
		doc.getProperties().put("status", status);
		//时间戳更新
		Map<String,Object> timestamp = new HashMap<String,Object>();
		timestamp.put("crawl", new Date());//入库时间
		doc.getProperties().put("timestamp", timestamp);

		//更新doc
		logger.debug("try to upsert vip item.[itemKey]"+itemKey,JSON.toString(doc));
//		需要区分是否已经存在，如果已经存在则直接更新，但要避免更新profit信息，以免出发3-party分润任务
//		arangoClient.upsert("my_stuff", itemKey, doc);   
		BaseDocument old = arangoClient.find("my_stuff", itemKey);
		if(old!=null && itemKey.equals(old.getKey())) {//已经存在，则直接跳过
			//do nothing
		}else {//否则写入
			arangoClient.insert("my_stuff", doc);   
			processedMap.put(poolName, processedMap.get(poolName)+1);
			processedAmount++;
		}
    }
    
    private double parseNumber(String numStr) {
    		try {
    			return Double.parseDouble(numStr);
    		}catch(Exception ex) {
    			logger.error("cannot parse double from input string.[numStr]"+numStr);
    			return 0;
    		}
    }
    
    //根据类别执行查询请求，并处理翻页
    private void searchItems(int sourceType,int channelType,String jxCodeName,String jxCodeId,String brokerId) throws Exception {
    	logger.debug("try to search items.[sourceType]"+sourceType+"[channelType]"+channelType+"[jxCodeName]"+jxCodeName+"[jxCodeId]"+jxCodeId+"[brokerId]"+brokerId);
    	processedMap.put(sourceType+"-"+channelType+"-"+jxCodeName, 0L);
    	int pageSize = 20;//默认每页20条
		int pageNo = 0;
		boolean hasMore = true;
		while(hasMore) {//逐页查询
			logger.debug("try to get by page.[pageNo]"+pageNo);
			GoodsInfoResponse result = vipHelper.searchItems(sourceType, channelType, jxCodeId, brokerId, pageSize, pageNo);
			logger.debug("total items:"+result.getTotal());
			List<GoodsInfo> goods = result.getGoodsInfoList();
			int totalPages = (result.getTotal()+pageSize-1)/pageSize;
			for(GoodsInfo good:goods) {
				upsertItem(sourceType+"-"+channelType+"-"+jxCodeName,good);
			}
			if(totalPages > pageNo+1)
				pageNo++;
			else
				hasMore = false;
		}
    }

    /**
	 * 查询推广商品列表，并完成入库
     */
    public void execute() throws JobExecutionException {
		logger.info("VIP cps item search job start. " + new Date());
		processedMap = new HashMap<String,Long>();
		totalMap = new HashMap<String,Long>();
		poolNameMap = new HashMap<String,String>();

        //需要根据sourceType、ChannelType、jxCode分别查询
		int[] channelTypes = {0,1,2};//频道类型:0-超高佣，1-出单爆款，2-商家补贴佣金; 当请求类型为频道时必传，
		String[] jxCodeNames = {"女装精选","男装精选","美妆精选","数码电子","精选-首饰","鞋包精选","母婴精选","居家精选","运动户外精选","家用电器"};
		String[] jxCodeIds = {"7hfpy0m4","wj7evz2j","vd0wbfdx","dpot8m5u","szkl4kj7","byh9331t","gkf52p8p","cnrzcs22","indvf44e","uggxpyh5"};

		//准备连接
		arangoClient = new ArangoDbClient(host,port,username,password,database);
		
		//先循环查询sourceType=0，即按频道查询。循环channelType，jxCode设为空
		for(int channelType:channelTypes) {
			try {
				searchItems(0,channelType,"","",brokerId);//固定sourceType为0，表示按channelType查询
			}catch(Exception ex) {
				logger.error("error occured while search items by [sourceType]0[channelType]"+channelType,ex);
			}
		}
		//然后循环查询sourceType=1 即按类目查询。循环jxCode，channelType设为0
		for(int i=0;i<jxCodeIds.length;i++) {
			try {
				searchItems(1,0,jxCodeNames[i],jxCodeIds[i],brokerId);//固定sourceType为1,channelType为0，表示按jxCode类目查询
			}catch(Exception ex) {
				logger.error("error occured while search items by [sourceType]0[channelType]0[jxCodeName]"+jxCodeNames[i]+"[jxCodeId]"+jxCodeIds[i],ex);
			}
		}
        
		//完成后关闭arangoDbClient
		arangoClient.close();

		if(totalAmount == 0)//啥活都没干，发啥消息
			return;

		//组装通知信息
		StringBuffer remark = new StringBuffer();
		remark.append("预期数量："+totalAmount);
		for(Map.Entry<String, Long> entry:processedMap.entrySet()) {
			if(entry.getValue()>0)
				remark.append("\n"+entry.getKey()+"："+entry.getValue());
		}
		remark.append("\n数量差异："+(totalAmount-processedAmount));
		
		
		//发送处理结果到管理员
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	    Map<String,String> header = new HashMap<String,String>();
    	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
    	    JSONObject result = null;
		JSONObject msg = new JSONObject();
		msg.put("openid", Global.getConfig("default_tech_guy_openid"));//固定发送
		msg.put("title", "导购数据同步任务结果");
		msg.put("task", "唯品会商品入库 已同步");
		msg.put("time", fmt.format(new Date()));
		msg.put("remark", remark);
		msg.put("color", totalAmount-processedAmount==0?"#FF0000":"#000000");

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

}
