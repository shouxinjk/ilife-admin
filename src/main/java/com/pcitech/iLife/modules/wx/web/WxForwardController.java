/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.wx.entity.WxAccount;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.entity.WxForward;
import com.pcitech.iLife.modules.wx.entity.WxTopping;
import com.pcitech.iLife.modules.wx.service.WxAccountService;
import com.pcitech.iLife.modules.wx.service.WxArticleService;
import com.pcitech.iLife.modules.wx.service.WxForwardService;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;

/**
 * 开白请求Controller
 * @author ilife
 * @version 2022-05-11
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxForward")
public class WxForwardController extends BaseController {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	@Autowired
	private WxForwardService wxForwardService;
	
	@Autowired
	private BrokerService brokerService;

	@Autowired
	private WxArticleService wxArticleService;

	@Autowired
	private WxAccountService wxAccountService;
	
	@ModelAttribute
	public WxForward get(@RequestParam(required=false) String id) {
		WxForward entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxForwardService.get(id);
		}
		if (entity == null){
			entity = new WxForward();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxForward:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxForward wxForward, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxForward> page = wxForwardService.findPage(new Page<WxForward>(request, response), wxForward); 
		model.addAttribute("page", page);
		return "modules/wx/wxForwardList";
	}

	@RequiresPermissions("wx:wxForward:view")
	@RequestMapping(value = "form")
	public String form(WxForward wxForward, Model model) {
		model.addAttribute("wxForward", wxForward);
		return "modules/wx/wxForwardForm";
	}

	@RequiresPermissions("wx:wxForward:edit")
	@RequestMapping(value = "save")
	public String save(WxForward wxForward, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxForward)){
			return form(wxForward, model);
		}
		wxForwardService.save(wxForward);
		addMessage(redirectAttributes, "保存开白请求成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxForward/?repage";
	}
	
	@RequiresPermissions("wx:wxForward:edit")
	@RequestMapping(value = "delete")
	public String delete(WxForward wxForward, RedirectAttributes redirectAttributes) {
		wxForwardService.delete(wxForward);
		addMessage(redirectAttributes, "删除开白请求成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxForward/?repage";
	}
	
	
	/**
	 * 根据请求的主题类型及ID获取开白请求列表
	 * 输入参数：主题类型、主题ID
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/requests/{subjectType}/{subjectId}", method = RequestMethod.GET)
	public List<WxForward> listForwardList(@PathVariable String subjectType, @PathVariable String subjectId) {
		WxForward wxForward = new WxForward();
		wxForward.setSubjectType(subjectType);
		wxForward.setSubjectId(subjectId);
		return wxForwardService.findList(wxForward);
	}
	
	/**
	 * 根据ID查找指定开白请求记录
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/request/{id}", method = RequestMethod.GET)
	public Map<String,Object> getForwardById(@PathVariable String id) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		WxForward wxForward = wxForwardService.get(id);
		if(wxForward!=null) {
			result.put("success", true);
			result.put("data", wxForward);
		}
		return result;
	}
	
	/**
	 * 发起开白请求
	 * accountId 为发起者的的开白公众号ID
	 * 前端直接构建wxforward
	 */
	@ResponseBody
	@RequestMapping(value = "rest/requests/{accountId}", method = RequestMethod.POST)
	public Map<String,Object> requestForward(@PathVariable String accountId,@RequestBody WxForward wxForward) {
		Map<String,Object> result = Maps.newHashMap();
		Broker broker = brokerService.get(wxForward.getRequester().getId());//获得请求达人
		Broker publisher = brokerService.get(wxForward.getResponder().getId());//获得被请求达人
		if(broker==null ) {
			result.put("success", false);
			result.put("msg", "cannot find request broker by id."+wxForward.getRequester().getId());
			return result;
		}
		//获取请求者的公众号信息
		String title = "亲，有粉丝申请文章开白哦";
		if("account".equalsIgnoreCase(wxForward.getType())){
			title = "亲，有粉丝申请公众号开白哦";
		}
		
		String remark = "";
		WxAccount requesterAccount = wxAccountService.get(accountId);
		if(requesterAccount==null ) {
			result.put("success", false);
			result.put("msg", "cannot find starter wx account by id."+accountId);
			return result;
		}
		remark =   "粉丝昵称 ："+broker.getNickname()
				+"\n开白类型 ："+("article".equalsIgnoreCase(wxForward.getType())?"单篇文章开白":"全局开白")
				+"\n粉丝公众号名称 ："+requesterAccount.getName()
				+"\n粉丝公众号ID  ："+requesterAccount.getOriginalId()
				+"\n粉丝公众号简介 ："+requesterAccount.getDescription()
				+"\n\n开白后请点击卡片回复哦，不想开白可直接点击卡片回绝~~";
		
		if(publisher ==null) {
			result.put("success", false);
			result.put("msg", "cannot find response  broker by id."+wxForward.getResponder().getId());
			return result;
		}
		
		WxArticle article = null;
		WxAccount account = null;
		String requestContent = "";
		if("article".equalsIgnoreCase(wxForward.getSubjectType())) {
			article = wxArticleService.get(wxForward.getArticle().getId());
			if(article==null) {
				result.put("success", false);
				result.put("msg", "cannot find article by id."+wxForward.getArticle().getId());
				return result;
			}
			requestContent = article.getTitle();
		}else if("account".equalsIgnoreCase(wxForward.getSubjectType())) {
			account = wxAccountService.get(wxForward.getAccount().getId());
			if(account==null) {
				result.put("success", false);
				result.put("msg", "cannot find account by id."+wxForward.getAccount().getId());
				return result;
			}
			requestContent = "公众号 "+ account.getName();
		}else {
			//do nothing
		}
		//请求者+请求公众号+请求内容类别+请求内容ID 为唯一识别码
		String id = Util.md5(wxForward.getRequester().getId()+requesterAccount.getId()+wxForward.getSubjectType()+wxForward.getSubjectId());
		
		wxForward.setCreateDate(new Date());
		wxForward.setUpdateDate(new Date());
		wxForward.setStatus("pending");
		wxForward.setIsNewRecord(true);
		wxForward.setId(id);
		wxForwardService.save(wxForward);
		result.put("success", true);
		result.put("data", wxForwardService.get(id));
		
		//发送申请消息 
		JSONObject json = new JSONObject();
		json.put("title", title);
		json.put("openid", publisher.getOpenid());//发给被请求者
		json.put("redirectUrl", "http://www.biglistoflittlethings.com/ilife-web-wx/publisher/forward.html?id="+id);
		json.put("request", requestContent);
		json.put("requestTime", sdf.format(new Date()));
		json.put("remark", remark);
		
   	    //准备发起HTTP请求：设置data server Authorization
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
		
		HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/notify-mp-forward", 
				json,header);
		
		return result;
	}
	
	/**
	 * 响应开白请求
	 * 前端直接构建wxforward
	 */
	@ResponseBody
	@RequestMapping(value = "rest/requests/{id}/{status}", method = RequestMethod.PUT)
	public Map<String,Object> respondRequest(@PathVariable String id,@PathVariable String status) {
		Map<String,Object> result = Maps.newHashMap();
		WxForward wxForward = wxForwardService.get(id);
		if(wxForward == null) {
			result.put("success", false);
			result.put("msg", "no wxForward with id "+id);
			return result;
		}
		wxForward.setUpdateDate(new Date());
		wxForward.setStatus(status);
		wxForwardService.save(wxForward);
		//获取开白请求信息
		Broker broker = brokerService.get(wxForward.getRequester().getId());//获得请求达人
		Broker publisher = brokerService.get(wxForward.getResponder().getId());//获得被请求达人
		
		//获取请求者的公众号信息
		String title = "亲，你申请的文章开白有回复了哦";
		if("account".equalsIgnoreCase(wxForward.getType())){
			title = "亲，你申请公众号全局开白有回复了哦";
		}
		
		String statusStr = "待开白";
		String statusTips = "点击进入文章列表，查看更多优质文章吧~~";
		if("approved".equalsIgnoreCase(wxForward.getStatus())){
			statusStr = "已开白";
			statusTips = "可以去公众号后台转载了哦~~";
		}else if("rejected".equalsIgnoreCase(wxForward.getStatus())){
			statusStr = "已拒绝";
			statusTips = "没关系哈，还有很多好文章可以转载的~~";
		}else {
			//do nothing
		}
		
		String remark = "";
		remark =   "我的公众号："+wxForward.getRequestAccount().getName()
				+"\n开白类型 ："+("article".equalsIgnoreCase(wxForward.getType())?"单篇文章开白":"全局开白")
				+"\n开白结果 ："+statusStr
				+"\n\n"+statusTips;
		
		WxArticle article = null;
		WxAccount account = null;
		String requestContent = "";
		if("article".equalsIgnoreCase(wxForward.getSubjectType())) {
			article = wxArticleService.get(wxForward.getArticle().getId());
			if(article==null) {
				result.put("success", false);
				result.put("msg", "cannot find article by id."+wxForward.getArticle().getId());
				return result;
			}
			requestContent = article.getTitle();
		}else if("account".equalsIgnoreCase(wxForward.getSubjectType())) {
			account = wxAccountService.get(wxForward.getAccount().getId());
			if(account==null) {
				result.put("success", false);
				result.put("msg", "cannot find account by id."+wxForward.getAccount().getId());
				return result;
			}
			requestContent = "公众号 "+ account.getName();
		}else {
			//do nothing
		}
		//发送回复消息 
		JSONObject json = new JSONObject();
		json.put("title", title);
		json.put("openid", broker.getOpenid());//发给请求者
		json.put("redirectUrl", "http://www.biglistoflittlethings.com/ilife-web-wx/publisher/forward.html?id="+id);
		json.put("request", requestContent);
		json.put("requestTime", sdf.format(new Date()));
		json.put("remark", remark);
		
   	    //准备发起HTTP请求：设置data server Authorization
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
		
		HttpClientHelper.getInstance().post(
				Global.getConfig("wechat.templateMessenge")+"/notify-mp-forward", 
				json,header);
		result.put("success", true);
		return result;
	}

}