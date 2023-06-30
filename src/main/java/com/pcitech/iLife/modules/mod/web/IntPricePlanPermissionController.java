/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import com.pcitech.iLife.modules.mod.entity.IntPricePlanPermission;
import com.pcitech.iLife.modules.mod.service.IntPricePlanPermissionService;

/**
 * 订阅计划授权Controller
 * @author ilife
 * @version 2023-06-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/intPricePlanPermission")
public class IntPricePlanPermissionController extends BaseController {

	@Autowired
	private IntPricePlanPermissionService intPricePlanPermissionService;
	
	@ModelAttribute
	public IntPricePlanPermission get(@RequestParam(required=false) String id) {
		IntPricePlanPermission entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = intPricePlanPermissionService.get(id);
		}
		if (entity == null){
			entity = new IntPricePlanPermission();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:intPricePlanPermission:view")
	@RequestMapping(value = {"list", ""})
	public String list(IntPricePlanPermission intPricePlanPermission, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<IntPricePlanPermission> page = intPricePlanPermissionService.findPage(new Page<IntPricePlanPermission>(request, response), intPricePlanPermission); 
		model.addAttribute("page", page);
		return "modules/mod/intPricePlanPermissionList";
	}

	@RequiresPermissions("mod:intPricePlanPermission:view")
	@RequestMapping(value = "form")
	public String form(IntPricePlanPermission intPricePlanPermission, Model model) {
		model.addAttribute("intPricePlanPermission", intPricePlanPermission);
		return "modules/mod/intPricePlanPermissionForm";
	}

	@RequiresPermissions("mod:intPricePlanPermission:edit")
	@RequestMapping(value = "save")
	public String save(IntPricePlanPermission intPricePlanPermission, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, intPricePlanPermission)){
			return form(intPricePlanPermission, model);
		}
		intPricePlanPermissionService.save(intPricePlanPermission);
		addMessage(redirectAttributes, "保存订阅计划授权成功");
		return "redirect:"+Global.getAdminPath()+"/mod/intPricePlanPermission/?repage";
	}
	
	@RequiresPermissions("mod:intPricePlanPermission:edit")
	@RequestMapping(value = "delete")
	public String delete(IntPricePlanPermission intPricePlanPermission, RedirectAttributes redirectAttributes) {
		intPricePlanPermissionService.delete(intPricePlanPermission);
		addMessage(redirectAttributes, "删除订阅计划授权成功");
		return "redirect:"+Global.getAdminPath()+"/mod/intPricePlanPermission/?repage";
	}

}