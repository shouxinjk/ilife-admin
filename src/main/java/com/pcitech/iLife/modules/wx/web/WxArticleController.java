/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.entity.WxReads;
import com.pcitech.iLife.modules.wx.service.WxArticleService;
import com.pcitech.iLife.modules.wx.service.WxReadsService;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.pcitech.iLife.util.WxHelper;

import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 微信文章管理Controller
 * @author ilife
 * @version 2022-03-28
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxArticle")
public class WxArticleController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private WxArticleService wxArticleService;
	@Autowired
	private WxReadsService wxReadsService;
	
	@Autowired
	private BrokerService brokerService;
	
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public WxArticle get(@RequestParam(required=false) String id) {
		WxArticle entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxArticleService.get(id);
		}
		if (entity == null){
			entity = new WxArticle();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxArticle:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxArticle wxArticle, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxArticle> page = wxArticleService.findPage(new Page<WxArticle>(request, response), wxArticle); 
		model.addAttribute("page", page);
		return "modules/wx/wxArticleList";
	}

	@RequiresPermissions("wx:wxArticle:view")
	@RequestMapping(value = "form")
	public String form(WxArticle wxArticle, Model model) {
		model.addAttribute("wxArticle", wxArticle);
		return "modules/wx/wxArticleForm";
	}

	@RequiresPermissions("wx:wxArticle:edit")
	@RequestMapping(value = "save")
	public String save(WxArticle wxArticle, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxArticle)){
			return form(wxArticle, model);
		}
		wxArticleService.save(wxArticle);
		addMessage(redirectAttributes, "保存微信文章成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxArticle/?repage";
	}
	
	@RequiresPermissions("wx:wxArticle:edit")
	@RequestMapping(value = "delete")
	public String delete(WxArticle wxArticle, RedirectAttributes redirectAttributes) {
		wxArticleService.delete(wxArticle);
		addMessage(redirectAttributes, "删除微信文章成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxArticle/?repage";
	}
	
	/**
	 * 获取待阅读文章列表:支持根据发布者 openid 过滤
	 * 包括置顶文章及普通文章两部分，在获取第一页时，优先获取指定列表，
	 * 然后分页获取普通待阅读文章列表
	 * 注意：文章的status字段保持的是置顶信息或顶一下信息。前端需要转换为数字，如果大于1为置顶，等于1为顶一下，0则为普通文章
	 */
	@ResponseBody
	@RequestMapping(value = "rest/pending-articles", method = RequestMethod.GET)
	public List<WxArticle> listPagedPendingArticles( @RequestParam(required=true) int from,
			@RequestParam(required=true) int to,
			@RequestParam String openid,
			@RequestParam String publisherOpenid) {
		List<WxArticle> list = Lists.newArrayList();
		//组织参数
		Map<String,Object> params = Maps.newHashMap();
		params.put("from", from);
		params.put("to", to);
		if(openid!=null && openid.trim().length()>0) {
			params.put("openid", openid);
		}
		if(publisherOpenid!=null && publisherOpenid.trim().length()>0) {
			params.put("publisherOpenid", publisherOpenid);
		}
		
		//先查询获取指定文章列表，仅在第一页时加载
		if(from==0) {
			list.addAll(wxArticleService.findToppingList(params));
		}
		
		//然后获取普通文章列表
		list.addAll(wxArticleService.findPendingList(params));

		return list;
	}
	
	/**
	 * 获取班车待阅读文章列表:支持根据发布者 openid 过滤
	 * 显示开车群code内的所有文章列表，按照加入时间升序排列
	 */
	@ResponseBody
	@RequestMapping(value = "rest/grouping-articles", method = RequestMethod.GET)
	public List<WxArticle> listPagedGroupingArticles( @RequestParam(required=true) int from,
			@RequestParam(required=true) int to,
			@RequestParam String openid,
			@RequestParam String code,
			@RequestParam String publisherOpenid) {
		List<WxArticle> list = Lists.newArrayList();
		//组织参数
		Map<String,Object> params = Maps.newHashMap();
		params.put("from", from);
		params.put("to", to);
		if(openid!=null && openid.trim().length()>0) {
			params.put("openid", openid);
		}
		if(publisherOpenid!=null && publisherOpenid.trim().length()>0) {
			params.put("publisherOpenid", publisherOpenid);
		}
		if(code!=null && code.trim().length()>0) {//如果带有微信群编码，则获取指定群文章列表
			params.put("code", code);
		}else {//code必须传递，如果没有则返回空列表
			return list;
		}
		list.addAll(wxArticleService.findPendingGroupingList(params));
		return list;
	}
	
	/**
	 * 根据openid获取已发布文章列表
	 */
	@ResponseBody
	@RequestMapping(value = "rest/my-articles/{openid}", method = RequestMethod.GET)
	public List<WxArticle> findMyArticles(@PathVariable String openid, @RequestParam(required=true) int from,@RequestParam(required=true) int to) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("openid", openid);
		params.put("from", from);
		params.put("to", to);
		return wxArticleService.findMyArticles(params);
	}
	
	/**
	 * 根据openid获取已发布文章总数
	 */
	@ResponseBody
	@RequestMapping(value = "rest/total-articles/{openid}", method = RequestMethod.GET)
	public Map<String,Object> countMyArticles(@PathVariable String openid) {
		logger.debug("try to count articles by openid.[openid]"+openid);
		Map<String, Object> result = Maps.newHashMap();
		result.put("status",true);
		result.put("count",wxArticleService.countMyArticles(openid));
		return result;
	}	
	
	/**
	 * 发布文章。数据：
	 * 	url：文章地址
	 * 	broker：id或openid至少传递一个，在传递openid时需要同时传递nickname
	 */
	@ResponseBody
	@RequestMapping(value = "rest/article", method = RequestMethod.POST)
	public Map<String, Object> publishArticle(@RequestBody WxArticle article){
		logger.debug("try to publish article.[data]"+article);
		Map<String, Object> result = Maps.newHashMap();
		Broker broker = null;
		
		//设置ID，并检查是否存在：
		String id = Util.md5(article.getUrl());
		WxArticle article2 = wxArticleService.get(id);
		if(article2!=null) {//表示存在，直接返回，不扣除阅豆
			result.put("status",true);
			result.put("description","Article exists.");
			result.put("code", "duplicate");//特殊标记，对于已经存在的文章不会扣减阅豆，需要提示发布者
			result.put("data", article2);//将文章作为数据返回
			return result;
		}
		
		//检查达人信息，如果不存在则静默创建
		if(article.getBroker()!=null && article.getBroker().getId()!=null) {
			broker = brokerService.get(article.getBroker().getId());
		}else if(article.getBroker()!=null && article.getBroker().getOpenid()!=null) {
			broker = brokerService.getByOpenid(article.getBroker().getOpenid());//先根据openid查询
			if(broker == null) {//如果不存在则直接默认创建
				//如果不存在，表示未注册的情况下直接发了链接，默认注册达人
				String parentdBrokerId = Global.getConfig("default_parent_broker_id");//固定达人ID 
				broker = new Broker();
				broker.setId(Util.md5(article.getBroker().getOpenid()));
				broker.setIsNewRecord(true);
				broker.setParent(brokerService.get(parentdBrokerId));
				broker.setOpenid(article.getBroker().getOpenid());
				broker.setPoints(20);//默认设置
				String nickname = "流量主达人";
				if(article.getBroker().getNickname()!=null&&article.getBroker().getNickname().trim().length()>0) {
					nickname = article.getBroker().getNickname();
				}
				broker.setNickname(nickname);
				brokerService.save(broker);
				//发送通知到上级达人
				JSONObject json = new JSONObject();
				json.put("title", "新流量主自动注册");
				json.put("name", nickname);
				json.put("openid", Global.getConfig("default_parent_broker_openid"));//固定达人openid
				HttpClientHelper.getInstance().post(
						Global.getConfig("wechat.templateMessenge")+"/notify-parent-broker", 
						json,null);//推送上级达人通知
			}
		}else {//直接提示出错了
			result.put("status",false);
			result.put("description","No broker info.");
		}
		
		//检查虚拟豆
		if(broker == null || broker.getId() == null || broker.getId().trim().length()==0 ) {
			result.put("status",false);
			result.put("description","Cannot find broker. "+article.getBroker());
		}else if(broker.getPoints()>0) {
			//获取文章信息
			try {
				JSONObject wechatArticle = WxHelper.getInstance().getWxArticleInfo(article.getUrl());
				if(wechatArticle.getString("title")!=null)
					  article.setTitle(wechatArticle.getString("title"));
				  if(wechatArticle.getString("coverImg")!=null) {
					  article.setCoverImg(wechatArticle.getString("coverImg"));
				  }
			}catch(Exception ex) {
				logger.error("failed check wechat article.",ex);
			}
			//发布文章
			article.setId(id);
			article.setIsNewRecord(true);//直接作为新文章发布
			article.setCreateDate(new Date());
			article.setUpdateDate(new Date());
			article.setStatus("active");
			article.setChannel("manual");
			article.setBroker(broker);
			wxArticleService.save(article);
			result.put("status",true);
			result.put("description","Article created successfully");
			result.put("data", wxArticleService.get(id));//将文章作为数据返回
			
			//扣除虚拟豆
			Dict dict = new Dict();
			dict.setType("publisher_point_cost");//查找流量主虚拟豆字典设置
			List<Dict> points = dictService.findList(dict);
			int pointsCost = 5;//默认为5个
			for(Dict point:points) {
				if("publish-article".equalsIgnoreCase(point.getValue())) {
					try {
						pointsCost = Integer.parseInt(point.getLabel());
					}catch(Exception ex) {
						//do nothing
					}
					break;
				}
			}
			broker.setPoints(broker.getPoints()-pointsCost);//TODO：查询扣除虚拟豆数量
			brokerService.save(broker);
			result.put("points", pointsCost);	
			result.put("pointsRemain", broker.getPoints());	
			result.put("description","Article created successfully and points charged");
			
			//推送文章到企业微信群，便于群发：由于通过微信，无前端接入，只能从后端完成推送
			String img = Global.getConfig("wechat.image.default.prefix")+"logo"+(System.currentTimeMillis()%25)+".jpeg";
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe12f24bb8146b774&redirect_uri=https://www.biglistoflittlethings.com/ilife-web-wx/dispatch.html&response_type=code&scope=snsapi_userinfo&state=____STATE____#wechat_redirect";

			if(article.getCoverImg()!=null && article.getCoverImg().trim().length()>5) {//至少开头是https
				img = article.getCoverImg();
			}
			JSONObject webhookItem = new JSONObject();
			webhookItem.put("title" , article.getTitle());
			webhookItem.put("description" , "有新文章发布，恭请批阅~~");
//			webhookItem.put("url" , article.getUrl());//TODO：需要进入文章列表界面，当前直接跳转到文章本身
			webhookItem.put("url" ,url.replace("____STATE____", "publisher__articles___byPublisherOpenid="+article.getBroker().getOpenid()));
			webhookItem.put("picurl" , img);//采用默认图片
			JSONArray webhookItems = new JSONArray();
			webhookItems.add(webhookItem);
			JSONObject webhookArticles = new JSONObject();
			webhookArticles.put("articles", webhookItems);
			JSONObject webhookMsg = new JSONObject();
			webhookMsg.put("msgtype", "news");
			webhookMsg.put("news", webhookArticles);
			logger.debug("try to post webhook msg."+webhookMsg);
			Map<String,String> headers = Maps.newHashMap();
			headers.put("Content-Type", "application/json");
			HttpClientHelper.getInstance().post(Global.getConfig("webHookUrlPrefix")+Global.getConfig("webHookCompanyBroker"), webhookMsg,headers);//推送到微信达人运营群webhook
		}else {
			result.put("status",false);
			result.put("description","Canceld. Points not enough.");
		}
		return result;
	}
	
	/**
	 * 上架、下架文章
	 */
	@ResponseBody
	@RequestMapping(value = "rest/article/{id}/{status}", method = RequestMethod.POST)
	public Map<String, Object> changeArticleStatus(@PathVariable String id,@PathVariable String status){
		Map<String, Object> result = Maps.newHashMap();
		WxArticle article = wxArticleService.get(id);
		
		//检查文章
		if(article == null) {
			result.put("status",false);
			result.put("description","Cannot find article by id:"+id);
		}else{
			//修改文章状态
			article.setStatus(status);
			wxArticleService.save(article);
			result.put("status",true);
			result.put("data",article);
			result.put("description","Article status changed.[new]"+status);
		}
		return result;
	}
	
	/**
	 * 有效阅读，扣除阅豆，并返回本次消耗。参数：
	 * articleId：文章ID
	 * readerOpenid：读者openid
	 * readCount：阅读次数
	 */
	@ResponseBody
	@RequestMapping(value = "rest/exposure", method = RequestMethod.POST)
	public Map<String, Object> onArticleViewed(@RequestBody JSONObject json){
		Map<String, Object> result = Maps.newHashMap();
		WxArticle article = wxArticleService.get(json.getString("articleId"));
		
		//检查文章
		if(article == null) {
			result.put("status",false);
			result.put("description","Cannot find article by id:"+json.getString("articleId"));
		}else{
			//获取文章发布达人
			Broker broker = brokerService.get(article.getBroker().getId());//重要：必须重新获取，否则会导致其他信息丢失
			//扣除虚拟豆
			Dict dict = new Dict();
			dict.setType("publisher_point_cost");//查找流量主虚拟豆字典设置
			List<Dict> points = dictService.findList(dict);
			int pointsCost = 2;//默认为2个
			for(Dict point:points) {
				if("view-article".equalsIgnoreCase(point.getValue())) {
					try {
						pointsCost = Integer.parseInt(point.getLabel());
					}catch(Exception ex) {
						//do nothing
					}
					break;
				}
			}
			//扣除文章发布者阅豆
			broker.setPoints(broker.getPoints()-pointsCost);//查询扣除虚拟豆数量
			brokerService.save(broker);
			//增加阅读者阅豆
			Broker reader = brokerService.getByOpenid(json.getString("readerOpenid"));
			if(reader==null || reader.getId()==null) {
				//直接忽略
				result.put("description","Cannot find broker info for current reader");
			}else {
				reader.setPoints(reader.getPoints()+pointsCost);//增加奖励
				brokerService.save(reader);
			}
			//写入阅读记录
			WxReads wxReads = new WxReads();
			wxReads.setBroker(broker);
			wxReads.setOpenid(json.getString("readerOpenid"));
			wxReads.setArticle(article);
			wxReads.setReadCount(json.getInteger("readCount"));
			wxReads.setCreateDate(new Date());
			wxReads.setUpdateDate(new Date());
			wxReadsService.save(wxReads);
			
			//需要设置返回信息：发布者达人ID、openId、logo、消耗阅豆、
			result.put("openid",broker.getOpenid());
			result.put("brokerId",broker.getId());
			result.put("nickname",broker.getName());
			result.put("avatarUrl",broker.getAvatarUrl()); 
			result.put("points",pointsCost);
			result.put("pointsRemain",broker.getPoints());
			result.put("status",true);
			result.put("description","Article exposure pionts cosumed.[cost]"+pointsCost+"[remain]"+broker.getPoints());
		}
		return result;
	}
}