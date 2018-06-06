/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.web;

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
import com.pcitech.iLife.modules.ope.entity.Behavior;
import com.pcitech.iLife.modules.ope.service.BehaviorService;

/**
 * 用户行为Controller
 * @author chenci
 * @version 2017-09-28
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/behavior")
public class BehaviorController extends BaseController {

	@Autowired
	private BehaviorService behaviorService;
	
	@ModelAttribute
	public Behavior get(@RequestParam(required=false) String id) {
		Behavior entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = behaviorService.get(id);
		}
		if (entity == null){
			entity = new Behavior();
		}
		return entity;
	}
	
	@RequiresPermissions("ope:behavior:view")
	@RequestMapping(value = {"list", ""})
	public String list(Behavior behavior, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Behavior> page = behaviorService.findPage(new Page<Behavior>(request, response), behavior); 
		model.addAttribute("page", page);
		return "modules/ope/behaviorList";
	}

	@RequiresPermissions("ope:behavior:view")
	@RequestMapping(value = "form")
	public String form(Behavior behavior, Model model) {
		model.addAttribute("behavior", behavior);
		return "modules/ope/behaviorForm";
	}

	@RequiresPermissions("ope:behavior:edit")
	@RequestMapping(value = "save")
	public String save(Behavior behavior, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, behavior)){
			return form(behavior, model);
		}
		behaviorService.save(behavior);
		addMessage(redirectAttributes, "保存用户行为成功");
		return "redirect:"+Global.getAdminPath()+"/ope/behavior/?repage";
	}
	
	@RequiresPermissions("ope:behavior:edit")
	@RequestMapping(value = "delete")
	public String delete(Behavior behavior, RedirectAttributes redirectAttributes) {
		behaviorService.delete(behavior);
		addMessage(redirectAttributes, "删除用户行为成功");
		return "redirect:"+Global.getAdminPath()+"/ope/behavior/?repage";
	}

}