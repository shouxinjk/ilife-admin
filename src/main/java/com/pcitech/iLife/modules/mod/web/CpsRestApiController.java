/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.entity.BaseDocument;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.cps.PddHelper;
import com.pcitech.iLife.cps.crawler.Crawler;
import com.pcitech.iLife.cps.crawler.CrawlerUtil;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.entity.ViewTemplate;
import com.pcitech.iLife.modules.mod.service.BoardItemService;
import com.pcitech.iLife.modules.mod.service.BoardService;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.OccasionService;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.modules.mod.service.PlatformSourceService;
import com.pcitech.iLife.modules.mod.service.ViewTemplateService;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.ope.service.PerformanceService;
import com.pcitech.iLife.task.PddItemSync;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.FastDFSUtils;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * CPS Restful API Controller
 * 支持通过API直接完成商品上架。
 * 支持平台包括：
 * 
 * 拼多多
 * 
 * @author ilife
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/rest/cps")
public class CpsRestApiController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(CpsRestApiController.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
    
    @Autowired
   	private BrokerService brokerService;
    @Autowired
	private PlatformCategoryService platformCategoryService;
    
    @Autowired
    PddHelper pddHelper;
    @Autowired
    PlatformSourceService platformSourceService;
    @Autowired
    CrawlerUtil crawlerUtil;
	
    /**
     * 检查URL是否支持自动采集入库
     * @param json
     * @return
     */
	@ResponseBody
	@RequestMapping(value = "support", method = RequestMethod.POST)
	public JSONObject checkUrlSupport(@RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(json.getString("url")==null || json.getString("url").trim().length()==0) {
			result.put("msg", "url is mandatory");
			return result;
		}
		String platform = platformSourceService.getPlatformByUrl(json.getString("url"));
		if("unsupport".equalsIgnoreCase(platform)) {
			result.put("platform", platform);
		}else {
			result.put("success", true);
			result.put("platform", platform);
		}
		return result;
	}
	
	/**
	 * 根据URL自动采集商品信息入库。对于不能采集或采集失败的情况返回success=false
	 * @param json
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "enhouse", method = RequestMethod.POST)
	public JSONObject autoEnhouse(@RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(json.getString("url")==null || json.getString("url").trim().length()==0) {
			result.put("msg", "url is mandatory");
			return result;
		}
		if(json.getString("text")==null || json.getString("text").trim().length()==0) { //原始文本内容也作为必要参数
			result.put("msg", "text is mandatory");
			return result;
		}
		//增加发送达人信息
		Broker broker = brokerService.get("system");//获取系统达人，如：未注册用户直接发送则会需要该返回
		broker.setOpenid(json.getString("openid"));
		broker.setNickname("小确幸er");
		if(json.getString("openid")!=null && json.getString("openid").trim().length()>0) {
			broker = brokerService.getByOpenid(json.getString("openid"));
		}
		
		//开始采集
		Crawler crawler = crawlerUtil.getCrawler(json.getString("url"));
		if(crawler == null) {
			//存入达人链接数据库，等待手动处理
			String type = "url";
			if(json.getString("text").indexOf("m.tb.cn")>0) { //检查移动端短连接
					type = "taobaoToken";
			}else if(json.getString("text").indexOf("s.click.taobao.com")>0) { //检查click跳转链接
					type = "taobaoClick";
			}else {//检查淘口令
				  String pattern = "[a-zA-Z0-9]{11}";
					 try {
					     Pattern r = Pattern.compile(pattern);
					     Matcher m = r.matcher(json.getString("text")); 
					     if (m.find()) {
					    	 String token = m.group();
					         logger.debug("match: " + token);
					         type = "taobaoToken";
					     }
					 }catch(Exception ex) {//无需处理
					 	logger.debug("Failed to match taobao token.",ex);
					 }				
			}
			insertBrokerSeed(json.getString("openid"),type,json.getString("url"),json.getString("text"),false);
			//推送通知消息
			sendWeworkMsg("达人商品上架", "发送后未能自动采集，请前往查看", json.getString("url"));//推送卡片
			sendWeworkMsg("达人商品上架", "发送后未能自动采集，请前往查看", "https://www.biglistoflittlethings.com/static/logo/distributor/ilife.png", json.getString("url"));
			result.put("msg", "not support yet.");
			result.put("broker",broker);
			return result;
		}
		result = crawler.enhouse(json.getString("url"), json.getString("openid"));
		result.put("broker",broker);
		if("nocps".equalsIgnoreCase(result.getString("type"))) {
			//存入达人链接数据库，等待手动处理
			insertBrokerSeed(json.getString("openid"),"url",json.getString("url"),json.getString("url"),false);
			//推送通知消息
			sendWeworkMsg("非CPS商品上架", "非导购商品信息未能自动采集，请前往查看", "https://www.biglistoflittlethings.com/static/logo/distributor/ilife.png", json.getString("url"));
			
		}
		
		return result;
	}
	
	
	/**
	 * 拼多多商品上架
	 * 
	 * 输入参数：
	 * 1, url:拼多多商品链接
	 * 2, openid:提交人openid。可以为空。如果传递则推送上架结果
	 * 
	 * 处理逻辑：
	 * 1，通过转链接口转换为CPS链接，获取goods_sign
	 * 2，根据goods_sign获取商品详情，并入库
	 * 3，如果为非推广商品，则根据openid查询达人信息并返回
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping(value = "pdd", method = RequestMethod.POST)
	public JSONObject getPddItem(@RequestBody JSONObject json) {
		return autoEnhouse(json);
		/**
		JSONObject result = new JSONObject();
		result.put("success", false);
		
		//检查参数
		if(json.getString("url")==null || json.getString("url").trim().length()==0) {
			result.put("msg", "URL错误");
			return result;
		}
		
		//检查openid：如果为非推广商品，则根据openid加载达人信息，需要人工介入后返回
		if(json.getString("openid")!=null && json.getString("openid").trim().length()>0) {
			Broker broker = brokerService.getByOpenid(json.getString("openid"));
			if(broker != null) {
				JSONObject brokerInfo = new JSONObject();
				brokerInfo.put("nickname", broker.getNickname());
				brokerInfo.put("avatarUrl", broker.getAvatarUrl());
				result.put("broker", brokerInfo);
			}
		}
		
		String brokerId = "system";//默认达人为system
		
		//step 1: 将url转换为pdd cps链接
		String cpsUrl = json.getString("url");
		try {
			GoodsZsUnitGenerateResponse response = pddHelper.generateCpsLinksByUrl(brokerId, json.getString("url"));
			cpsUrl = response.getMobileUrl();
		} catch (Exception e) {
			logger.error("failed get cps link by url.[url]"+json.getString("url"),e);
			result.put("msg", "failed parse url.");
		}//默认为system
		
		//step 2: 根据goods_sign获取商品详情并入库
		//https://mobile.yangkeduo.com/duo_coupon_landing.html?goods_id=247148672711&pid=20434335_206807608&goods_sign=E932m-F7iFNU8LcRwfDaiZopXsF9TOKL_JQENcpyaEj&cpsSign=CC_220531_20434335_206807608_167757c35a33e72414d1bf494a2ac57a&_x_ddjb_act=%7B%22st%22%3A%221%22%7D&duoduo_type=2
		String goodsSign = "";
		Pattern p=Pattern.compile("goods_sign=([A-Za-z0-9_\\-]+)"); 
		Matcher m=p.matcher(cpsUrl); 
		if(m.find()) { //仅在发现后进行
			goodsSign = m.group(m.groupCount()); //只处理最后一组，实际上也只有一组
			logger.debug("generateCpsLink API return success.[goodsSign]"+goodsSign);
		} else {
			logger.error("generateCpsLink API does not return goodsSign in URL.",cpsUrl);
			result.put("msg", "no goods_sign found.");
			return result;
		}

		//准备doc
		//准备更新doc
		BaseDocument doc = new BaseDocument();
		String itemKey = Util.md5(json.getString("url"));
		doc.setKey(itemKey);//url为唯一识别
		//设置状态。注意，需要设置index=pending 等待重新索引。只要有CPS链接，就可以推广了

		//采集任务信息
		Map<String,Object> task = new HashMap<String,Object>();
		task.put("user", "robot-pddItemRest");
		task.put("executor", "robot-pddItemRest-instance");
		task.put("timestamp", new Date().getTime());
		task.put("url", json.getString("url"));
		Map<String,Object> distributor = new HashMap<String,Object>();
		//distributor
		distributor.put("name", "拼多多");
		doc.getProperties().put("distributor", distributor);	
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
		//获取商品详情，填充tags等信息
		try {
			//获取CPS链接
			Map<String,Object> link = new HashMap<String,Object>();
			link.put("web", json.getString("url"));
			link.put("wap", json.getString("url"));
			//针对当前goodsign单独请求CPS链接
			List<String> goodSignList = Lists.newArrayList();
			goodSignList.add(goodsSign);
			PddDdkGoodsPromotionUrlGenerateResponse response = pddHelper.generateCpsLinksByGoodsSign(brokerId,null,goodSignList);
			GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem cpsResult = response.getGoodsPromotionUrlGenerateResponse().getGoodsPromotionUrlList().get(0);
			if(cpsResult.getMobileShortUrl()==null) {//如果还是没拿到，那就真的没招了，赶紧再去对接口找原因
				logger.warn("cannot generate cps link by goodsign. disable this item.");
				status.put("index", "ready");//因为没有wap2，不需要索引
				doc.getProperties().put("status", status);
			}else {
				logger.debug("generate cps link by goodsign successfully.");
				link.put("wap2", cpsResult.getMobileShortUrl());//采用短连接
				link.put("web2", cpsResult.getShortUrl());
			}
			doc.getProperties().put("link", link);
		
			//获取商品详情
			GoodsDetailResponse resp = pddHelper.getItemDetail(brokerId, goodsSign);//TODO:注意：有可能不是多多进宝商品，返回为null，会导致异常
			if(resp != null) {
				List<String> tags = new ArrayList<String>();
				List<String> unifiedTags = resp.getGoodsDetails().get(0).getUnifiedTags();//商品unified tags
				tags.add(resp.getGoodsDetails().get(0).getOptName());//商品标签
				for(String tag:unifiedTags) {//商品unified tags里有null项 ，需要排除
					if(null != tag && tag.trim().length() > 0 && !"null".equalsIgnoreCase(tag))
						tags.add(tag);
				}
				doc.getProperties().put("type", "commodity");
				doc.getProperties().put("source", "pdd");
				doc.getProperties().put("catIds", resp.getGoodsDetails().get(0).getCatIds());//更新category TODO 当前为ID，需要在同步替换
				doc.getProperties().put("searchId", "");//TODO:没有searchId
				doc.getProperties().put("title", resp.getGoodsDetails().get(0).getGoodsName());//更新title
				doc.getProperties().put("tags", tags);//更新类目，包含3级别分类
				doc.getProperties().put("logo", resp.getGoodsDetails().get(0).getGoodsImageUrl());//更新首图 img
				doc.getProperties().put("summary", resp.getGoodsDetails().get(0).getGoodsDesc());//更新简介
				doc.getProperties().put("images", resp.getGoodsDetails().get(0).getGoodsGalleryUrls());//更新图片
				
				Map<String,Object> seller = new HashMap<String,Object>();
				seller.put("name", resp.getGoodsDetails().get(0).getMallName());
				doc.getProperties().put("seller", seller);	
	
				//更新品牌信息到prop列表
				Map<String,String> props = new HashMap<String,String>();
				//如果原来已经有属性，需要继续保留
				if(doc.getProperties().get("props") != null) {
					Map<String,String> oldProps = (Map<String,String>)doc.getProperties().get("props");
					props = oldProps;
				}
				props.put("品牌", resp.getGoodsDetails().get(0).getBrandName());//增加品牌属性
				doc.getProperties().put("props", props);

				List<String> images = new ArrayList<String>();
				images.add(resp.getGoodsDetails().get(0).getGoodsImageUrl());
				doc.getProperties().put("images", images);//更新图片
				//价格
				Map<String,Object> price = new HashMap<String,Object>();
				price.put("currency", "￥");
				price.put("coupon", parseNumber(resp.getGoodsDetails().get(0).getCouponDiscount()*0.01));//单位为分，换算为元
				price.put("bid", parseNumber(resp.getGoodsDetails().get(0).getMinNormalPrice()*0.01));//单位为分，换算为元
				price.put("sale", parseNumber(resp.getGoodsDetails().get(0).getMinGroupPrice()*0.01));//单位为分，换算为元
				doc.getProperties().put("price", price);	
				Map<String,Object> profit = new HashMap<String,Object>();
				profit.put("rate", parseNumber(resp.getGoodsDetails().get(0).getPromotionRate()*0.1));//是千分比，转换为百分比
				profit.put("type", "2-party");//待自动任务转换为3-party
				double salePrice = resp.getGoodsDetails().get(0).getMinGroupPrice()*0.01;//默认按卖价计算佣金
				if(resp.getGoodsDetails().get(0).getHasCoupon()) //如果有coupon则扣除coupon后计算
					salePrice = (resp.getGoodsDetails().get(0).getMinGroupPrice()-resp.getGoodsDetails().get(0).getCouponDiscount())*0.01;//实际卖价：转换为元
				double commssion = salePrice*resp.getGoodsDetails().get(0).getPromotionRate()*0.001;//千分比，转换为元
				profit.put("amount",parseNumber(commssion));//单位为元
				doc.getProperties().put("profit", profit);	
				//检查类目映射
				PlatformCategory platformCategoryMapping = platformCategoryService.get("pdd"+resp.getGoodsDetails().get(0).getCatIds().get(resp.getGoodsDetails().get(0).getCatIds().size()-1));
				if(platformCategoryMapping!=null) {//有则更新
					doc.getProperties().put("category", platformCategoryMapping.getName());	//补充原始类目名称
					if(platformCategoryMapping.getCategory()!=null) {//有则更新
						Map<String,Object> meta = new HashMap<String,Object>();
						meta.put("category", platformCategoryMapping.getCategory().getId());
						meta.put("categoryName", platformCategoryMapping.getCategory().getName());
						doc.getProperties().put("meta", meta);	
					}
				}else {
					//检查是否支持无类目映射入库
					if(!"true".equalsIgnoreCase(Global.getConfig("sx.enhouseWithoutCategoryMapping"))) {
						result.put("msg", "no category mapping");
						return result;
					}
				}

				//时间戳更新
				Map<String,Object> timestamp = new HashMap<String,Object>();
				timestamp.put("crawl", new Date());//入库时间
				doc.getProperties().put("timestamp", timestamp);

				//更新到arangodb
	    		arangoClient = new ArangoDbClient(host,port,username,password,database);
	    		//更新doc
	    		logger.debug("try to upsert pdd item.[itemKey]"+itemKey,JSON.toJSONString(doc));
	    		arangoClient.upsert("my_stuff", itemKey, doc); 
	    		//完成后关闭arangoDbClient
	    		arangoClient.close();

	    		result.put("success", true);
	    		Map<String,Object> data = doc.getProperties();
	    		data.put("itemKey", itemKey);
	    		result.put("data",data );//将properties返回
			}else {
				logger.warn("cannot get item detail.");
			}
		}catch(Exception ex) {
			logger.error("error occured while enhouse pdd item.",ex);
		}
		
		return result;
		//**/
	}
	

	/**
	 * 淘宝、天猫、飞猪商品上架
	 * 
	 * 输入参数：
	 * 1, url:淘宝、飞猪、天猫等淘系商品链接
	 * 2, openid:提交人openid。可以为空。如果传递则推送上架结果
	 * 
	 * 处理逻辑：
	 * TODO 通过API检查商品。当前无API权限。
	 * 直接返回达人信息完事
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping(value = "taobao", method = RequestMethod.POST)
	public JSONObject getTaobaoItem(@RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		
		//检查参数
		if(json.getString("url")==null || json.getString("url").trim().length()==0) {
			result.put("msg", "URL错误");
			return result;
		}
		
		//检查openid：如果为非推广商品，则根据openid加载达人信息，需要人工介入后返回
		if(json.getString("openid")!=null && json.getString("openid").trim().length()>0) {
			Broker broker = brokerService.getByOpenid(json.getString("openid"));
			if(broker != null) {
				JSONObject brokerInfo = new JSONObject();
				brokerInfo.put("nickname", broker.getNickname());
				brokerInfo.put("avatarUrl", broker.getAvatarUrl());
				result.put("broker", brokerInfo);
			}
		}
		
		String brokerId = "system";//默认达人为system
		
		return result;
	}
	

	/**
	 * 手动上架商品
	 * 
	 * 输入参数：
	 * 1, url:京东商品链接
	 * 2, openid:提交人openid。可以为空。如果传递则推送上架结果
	 * 
	 * 处理逻辑：
	 * TODO 通过API检查商品。当前无API权限。
	 * 直接返回达人信息完事
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping(value = "manual", method = RequestMethod.POST)
	public JSONObject enhouseItem(@RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		
		//检查参数
		if(json.getString("url")==null || json.getString("url").trim().length()==0) {
			result.put("msg", "URL错误");
			return result;
		}
		
		//检查openid：如果为非推广商品，则根据openid加载达人信息，需要人工介入后返回
		if(json.getString("openid")!=null && json.getString("openid").trim().length()>0) {
			Broker broker = brokerService.getByOpenid(json.getString("openid"));
			if(broker != null) {
				JSONObject brokerInfo = new JSONObject();
				brokerInfo.put("nickname", broker.getNickname());
				brokerInfo.put("avatarUrl", broker.getAvatarUrl());
				result.put("broker", brokerInfo);
			}
		}
		
		String brokerId = "system";//默认达人为system
		
		return result;
	}

    private double parseNumber(double d) {
//		return Double.valueOf(String.format("%.2f", d ));//会四舍五入，丢弃
    	NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
		String numStr = nf.format(d);
		try {
			return Double.parseDouble(numStr);
		}catch(Exception ex) {
			return d;
		}
    }	
    
	//存储提交的URL到种子库，便于手动入库处理
	public void insertBrokerSeed(String openid,String type, String data, String text, boolean notifyStatus) {
		//更新到arangodb
		arangoClient = new ArangoDbClient(host,port,username,password,database);

	  //组织默认broker-seed文档
		BaseDocument doc = new BaseDocument();
		Map<String,Object> statusNode = new HashMap<String,Object>();
		statusNode.put("parse", false);
		statusNode.put("collect", false);
		statusNode.put("cps", false);
		statusNode.put("profit", false);
		statusNode.put("notify", notifyStatus);
		Map<String,Object> timestampNode = new HashMap<String,Object>();
		timestampNode.put("create", new Date());	
		
		//doc.setKey(Util.md5(text));
		doc.getProperties().put("openid", openid);
		doc.getProperties().put("type", type);
		doc.getProperties().put("data", data);
		doc.getProperties().put("status", statusNode);
		doc.getProperties().put("timestamp", timestampNode);
		doc.getProperties().put("text", text);
		
		//更新doc
		logger.debug("try to upsert seed item.[url]"+data,JSON.toJSONString(doc));
		arangoClient.insert("broker_seeds", doc); 
		//完成后关闭arangoDbClient
		arangoClient.close();
  }
	  //发送企业微信通知消息
	  //采用文本卡片形式，便于复制文字
	  public void sendWeworkMsg(String title,String description,String url) {
			//组装模板消息
			JSONObject json = new JSONObject();
			json.put("msgtype", "textcard");
			JSONObject textcard = new JSONObject();
			textcard.put("title" , title);
			textcard.put("description" , url);
			textcard.put("url" , url);
			textcard.put("btntxt" , "前往查看");
			json.put("textcard", textcard);
			
			logger.debug("try to send cp msg. ",json);
			
	   	    //准备发起HTTP请求：设置data server Authorization
		    Map<String,String> header = new HashMap<String,String>();
		    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
		    
			//发送到企业微信
			HttpClientHelper.getInstance().post(
					Global.getConfig("wework.templateMessenge")+"/notify-cp-company-broker", 
					json,header);
	  }
	  
	  //发送企业微信通知消息
	  //直接用卡片方式组织
	  public void sendWeworkMsg(String title,String description,String picUrl,String url) {
			//组装模板消息
			JSONObject json = new JSONObject();
			json.put("msgtype", "news");
			JSONObject jsonArticle = new JSONObject();
			jsonArticle.put("title" , title);
			jsonArticle.put("description" , description);
			jsonArticle.put("url" , url);
			jsonArticle.put("picurl" , picUrl);

			JSONArray jsonArticles = new JSONArray();
			jsonArticles.add(jsonArticle);
			JSONObject jsonNews = new JSONObject();
			jsonNews.put("articles", jsonArticles);
			json.put("news", jsonNews);
			
			logger.debug("try to send cp msg. ",json);
			
	   	    //准备发起HTTP请求：设置data server Authorization
		    Map<String,String> header = new HashMap<String,String>();
		    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
		    
			//发送到企业微信
			HttpClientHelper.getInstance().post(
					Global.getConfig("wework.templateMessenge")+"/notify-cp-company-broker", 
					json,header);
	  }

}