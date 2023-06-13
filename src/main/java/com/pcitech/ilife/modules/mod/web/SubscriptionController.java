/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.ilife.modules.mod.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.ilife.modules.mod.entity.Subscription;
import com.pcitech.ilife.modules.mod.service.SubscriptionService;

/**
 * SaaS订阅Controller
 * @author ilife
 * @version 2023-06-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/subscription")
public class SubscriptionController extends BaseController {

	@Autowired
	private SubscriptionService subscriptionService;
	
	@ModelAttribute
	public Subscription get(@RequestParam(required=false) String id) {
		Subscription entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = subscriptionService.get(id);
		}
		if (entity == null){
			entity = new Subscription();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:subscription:view")
	@RequestMapping(value = {"list", ""})
	public String list(Subscription subscription, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Subscription> page = subscriptionService.findPage(new Page<Subscription>(request, response), subscription); 
		model.addAttribute("page", page);
		return "modules/mod/subscriptionList";
	}

	@RequiresPermissions("mod:subscription:view")
	@RequestMapping(value = "form")
	public String form(Subscription subscription, Model model) {
		model.addAttribute("subscription", subscription);
		return "modules/mod/subscriptionForm";
	}

	@RequiresPermissions("mod:subscription:edit")
	@RequestMapping(value = "save")
	public String save(Subscription subscription, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, subscription)){
			return form(subscription, model);
		}
		subscriptionService.save(subscription);
		addMessage(redirectAttributes, "保存SaaS订阅成功");
		return "redirect:"+Global.getAdminPath()+"/mod/subscription/?repage";
	}
	
	@RequiresPermissions("mod:subscription:edit")
	@RequestMapping(value = "delete")
	public String delete(Subscription subscription, RedirectAttributes redirectAttributes) {
		subscriptionService.delete(subscription);
		addMessage(redirectAttributes, "删除SaaS订阅成功");
		return "redirect:"+Global.getAdminPath()+"/mod/subscription/?repage";
	}

}