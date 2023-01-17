/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Badge;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.CategoryBroker;
import com.pcitech.iLife.modules.mod.service.BadgeService;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.CategoryBrokerService;
import com.pcitech.iLife.modules.wx.entity.WxAccount;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.entity.WxForward;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;

/**
 * 类目专家授权Controller
 * @author ilife
 * @version 2022-12-27
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/categoryBroker")
public class CategoryBrokerController extends BaseController {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Autowired
	private CategoryBrokerService categoryBrokerService;
	
	@Autowired
	private BrokerService brokerService;
	
	@Autowired
	private BadgeService badgeService;
	
	@ModelAttribute
	public CategoryBroker get(@RequestParam(required=false) String id) {
		CategoryBroker entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = categoryBrokerService.get(id);
		}
		if (entity == null){
			entity = new CategoryBroker();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:categoryBroker:view")
	@RequestMapping(value = {"list", ""})
	public String list(CategoryBroker categoryBroker, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CategoryBroker> page = categoryBrokerService.findPage(new Page<CategoryBroker>(request, response), categoryBroker); 
		model.addAttribute("page", page);
		return "modules/mod/categoryBrokerList";
	}

	@RequiresPermissions("mod:categoryBroker:view")
	@RequestMapping(value = "form")
	public String form(CategoryBroker categoryBroker, Model model) {
		model.addAttribute("categoryBroker", categoryBroker);
		return "modules/mod/categoryBrokerForm";
	}

	@RequiresPermissions("mod:categoryBroker:edit")
	@RequestMapping(value = "save")
	public String save(CategoryBroker categoryBroker, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, categoryBroker)){
			return form(categoryBroker, model);
		}
		categoryBrokerService.save(categoryBroker);
		addMessage(redirectAttributes, "保存类目专家授权成功");
		return "redirect:"+Global.getAdminPath()+"/mod/categoryBroker/?repage";
	}
	
	@RequiresPermissions("mod:categoryBroker:edit")
	@RequestMapping(value = "delete")
	public String delete(CategoryBroker categoryBroker, RedirectAttributes redirectAttributes) {
		categoryBrokerService.delete(categoryBroker);
		addMessage(redirectAttributes, "删除类目专家授权成功");
		return "redirect:"+Global.getAdminPath()+"/mod/categoryBroker/?repage";
	}

	/**
	 * 授权。针对某一个达人进行。支持传递badge及broker
	 * {
	 * 	broker:{id:xxx},
	 *  badge: {id:xxx}
	 * }
	 * 或者
	 * {
	 * 	broker:{id:xxx},
	 *  badge: {key:xxx}
	 * }
	 */
	@ResponseBody
	@RequestMapping(value = "rest/badge", method = RequestMethod.POST)
	public JSONObject addBadges(@RequestBody CategoryBroker categoryBroker) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		//检查数据
		Badge badge = badgeService.get(categoryBroker.getBadge());
		//如果为空则尝试根据key查询
		if(badge == null && categoryBroker.getBadge().getKey()!=null && categoryBroker.getBadge().getKey().trim().length()>0) {
			List<Badge> badges = badgeService.findList(categoryBroker.getBadge());
			if(badges.size()>0)
				badge = badges.get(0);
			
		}
		Broker broker = brokerService.get(categoryBroker.getBroker());
		if(badge == null || broker == null) {
			result.put("msg", "bot badge and broker are required.");
			return result;
		}
		categoryBroker.setBadge(badge);
		categoryBroker.setBroker(broker);
		//否则设置数据：保证唯一性:brokerId+badgeId+categoryId
		if(categoryBroker.getId()==null || categoryBroker.getId().trim().length()==0) {
			categoryBroker.setId(Util.md5(broker.getId()+badge.getId()+(categoryBroker.getCategory()!=null?categoryBroker.getCategory().getId():"")));
			categoryBroker.setIsNewRecord(true);
		}
		//保存勋章
		try {
			categoryBrokerService.save(categoryBroker);
		}catch(Exception ex) {
			result.put("error", "save badge to broker failed.");
			return result;
		}
		//根据勋章修改达人等级：需要验证，仅对低级别进行修改
		if("ready".equalsIgnoreCase(categoryBroker.getStatus()) && broker.getLevel()<badge.getLevel()) {
			broker.setLevel(badge.getLevel());
			brokerService.save(broker);
		}
		result.put("data", categoryBroker);
		result.put("success", true);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/badge/{id}", method = RequestMethod.GET)
	public CategoryBroker getCategoryBrokerById(@PathVariable String id) {
		return categoryBrokerService.get(id);
	}
	

	/**
	 * 响应申请，修改状态后需要同步推送模板消息
	 * {
	 * 	id: xxx,
	 *  status: xxx
	 * }
	 */
	@ResponseBody
	@RequestMapping(value = "rest/status", method = RequestMethod.POST)
	public JSONObject changeBadgeStatus(@RequestBody JSONObject msg) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		
		if(msg.getString("id")==null || msg.getString("id").trim().length()==0 || 
				msg.getString("status")==null || msg.getString("status").trim().length()==0) {
			result.put("msg", "id and status are required.");
			return result;
		}
		
		//检查数据并更新
		CategoryBroker categoryBroker = categoryBrokerService.get(msg.getString("id"));
		if(categoryBroker == null) {
			result.put("msg", "cannot find categoryBroker by id:"+msg.getString("id"));
			return result;
		}
		categoryBroker.setStatus(msg.getString("status"));
		categoryBrokerService.save(categoryBroker);
		
		//获取Badge及Broker，更新level
		Broker broker = brokerService.get(categoryBroker.getBroker());
		Badge badge = badgeService.get(categoryBroker.getBadge());
		//同时准备推送消息
		JSONObject json = new JSONObject();
		json.put("openid", broker.getOpenid());//发给请求者
		String remark = "";
		if("ready".equalsIgnoreCase(msg.getString("status"))) {
			json.put("title", badge.getName()+"审核完成");
			if(broker.getLevel()<badge.getLevel()) {
				broker.setLevel(badge.getLevel());
				brokerService.save(broker);
			}
			remark = "申请已通过，点击卡片开始"+badge.getDescription();
		}else if("rejected".equalsIgnoreCase(msg.getString("status"))) {
			json.put("title", badge.getName()+"审核失败");
			remark = "抱歉，审核未能通过。可修改补充优势领域描述，再次发起申请，也可直接与客服联系~~";
		}

		//发送回复消息 
		json.put("redirectUrl", "https://www.biglistoflittlethings.com/ilife-web-wx/candidate.html?id="+categoryBroker.getId());
		json.put("request", badge.getName());
		json.put("requestTime", sdf.format(categoryBroker.getCreateDate()));
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