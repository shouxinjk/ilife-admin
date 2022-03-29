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
import com.pcitech.iLife.modules.wx.service.WxArticleService;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;

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
	 * 根据openid获取待阅读文章列表
	 */
	@ResponseBody
	@RequestMapping(value = "rest/pending-articles", method = RequestMethod.GET)
	public List<WxArticle> listPagedPendingArticles( @RequestParam(required=true) int from,@RequestParam(required=true) int to) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("from", from);
		params.put("to", to);
		return wxArticleService.findPendingList(params);
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
		return wxArticleService.findPendingList(params);
	}
	
	/**
	 * 发布文章
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
			//先发布文章
			article.setIsNewRecord(true);//直接作为新文章发布
			article.setCreateDate(new Date());
			article.setUpdateDate(new Date());
			article.setStatus("active");
			article.setBroker(broker);
			wxArticleService.save(article);
			result.put("status",true);
			result.put("description","Article created successfully");
			
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
			result.put("description","Article created successfully and points charged");
			
			//推送文章到企业微信群，便于群发：由于通过微信，无前端接入，只能从后端完成推送
			String img = Global.getConfig("wechat.image.default.prefix")+"logo"+(System.currentTimeMillis()%25)+".jpeg";
			if(article.getCoverImg()!=null && article.getCoverImg().trim().length()>5) {//至少开头是https
				img = article.getCoverImg();
			}
			JSONObject webhookItem = new JSONObject();
			webhookItem.put("title" , article.getTitle());
			webhookItem.put("description" , "有新文章发布，恭请批阅~~");
			webhookItem.put("url" , article.getUrl());//TODO：需要进入文章列表界面，当前直接跳转到文章本身
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
			result.put("description","Article status changed.[new]"+status);
		}
		return result;
	}
	
	/**
	 * 有效阅读，扣除阅豆
	 * 扣除阅豆，并返回本次消耗
	 */
	@ResponseBody
	@RequestMapping(value = "rest/exposure/{id}/{openid}", method = RequestMethod.POST)
	public Map<String, Object> onArticleViewed(@PathVariable String id,@PathVariable String openid){
		Map<String, Object> result = Maps.newHashMap();
		WxArticle article = wxArticleService.get(id);
		
		//检查文章
		if(article == null) {
			result.put("status",false);
			result.put("description","Cannot find article by id:"+id);
		}else{
			//获取文章发布达人
			Broker broker = brokerService.get(article.getBroker().getId());//重要：必须重新获取，否则会导致其他信息丢失
			//扣除虚拟豆
			Dict dict = new Dict();
			dict.setType("publisher_point_cost");//查找流量主虚拟豆字典设置
			List<Dict> points = dictService.findList(dict);
			int pointsCost = 2;//默认为2个
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
			//扣除文章发布者阅豆
			broker.setPoints(broker.getPoints()-pointsCost);//查询扣除虚拟豆数量
			brokerService.save(broker);
			//增加阅读者阅豆
			Broker reader = brokerService.getByOpenid(openid);
			if(reader==null || reader.getId()==null) {
				//直接忽略
				result.put("description","Cannot find broker info for current reader");
			}else {
				reader.setPoints(reader.getPoints()+pointsCost);//增加奖励
				brokerService.save(reader);
			}
			//需要设置返回信息：发布者达人ID、openId、logo、消耗阅豆、
			result.put("openid",broker.getOpenid());
			result.put("brokerId",broker.getId());
			result.put("nickname",broker.getName());
			//result.put("avatarUrl",broker.getOpenid()); //TODO 需要补充
			result.put("points",pointsCost);
			result.put("status",true);
			result.put("description","Article exposure pionts cosumed.[cost]"+pointsCost+"[remain]"+broker.getPoints());
		}
		return result;
	}
}