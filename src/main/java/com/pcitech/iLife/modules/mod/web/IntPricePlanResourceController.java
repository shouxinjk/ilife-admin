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
import com.pcitech.iLife.modules.mod.entity.IntPricePlanResource;
import com.pcitech.iLife.modules.mod.service.IntPricePlanResourceService;

/**
 * 订阅计划资源Controller
 * @author ilife
 * @version 2023-06-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/intPricePlanResource")
public class IntPricePlanResourceController extends BaseController {

	@Autowired
	private IntPricePlanResourceService intPricePlanResourceService;
	
	@ModelAttribute
	public IntPricePlanResource get(@RequestParam(required=false) String id) {
		IntPricePlanResource entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = intPricePlanResourceService.get(id);
		}
		if (entity == null){
			entity = new IntPricePlanResource();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:intPricePlanResource:view")
	@RequestMapping(value = {"list", ""})
	public String list(IntPricePlanResource intPricePlanResource, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<IntPricePlanResource> page = intPricePlanResourceService.findPage(new Page<IntPricePlanResource>(request, response), intPricePlanResource); 
		model.addAttribute("page", page);
		return "modules/mod/intPricePlanResourceList";
	}

	@RequiresPermissions("mod:intPricePlanResource:view")
	@RequestMapping(value = "form")
	public String form(IntPricePlanResource intPricePlanResource, Model model) {
		model.addAttribute("intPricePlanResource", intPricePlanResource);
		return "modules/mod/intPricePlanResourceForm";
	}

	@RequiresPermissions("mod:intPricePlanResource:edit")
	@RequestMapping(value = "save")
	public String save(IntPricePlanResource intPricePlanResource, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, intPricePlanResource)){
			return form(intPricePlanResource, model);
		}
		intPricePlanResourceService.save(intPricePlanResource);
		addMessage(redirectAttributes, "保存订阅计划资源成功");
		return "redirect:"+Global.getAdminPath()+"/mod/intPricePlanResource/?repage";
	}
	
	@RequiresPermissions("mod:intPricePlanResource:edit")
	@RequestMapping(value = "delete")
	public String delete(IntPricePlanResource intPricePlanResource, RedirectAttributes redirectAttributes) {
		intPricePlanResourceService.delete(intPricePlanResource);
		addMessage(redirectAttributes, "删除订阅计划资源成功");
		return "redirect:"+Global.getAdminPath()+"/mod/intPricePlanResource/?repage";
	}

}