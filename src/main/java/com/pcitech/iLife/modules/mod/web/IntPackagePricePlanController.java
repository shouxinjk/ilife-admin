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
import com.pcitech.iLife.modules.mod.entity.IntPackagePricePlan;
import com.pcitech.iLife.modules.mod.service.IntPackagePricePlanService;

/**
 * 订阅套餐明细Controller
 * @author ilife
 * @version 2023-06-18
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/intPackagePricePlan")
public class IntPackagePricePlanController extends BaseController {

	@Autowired
	private IntPackagePricePlanService intPackagePricePlanService;
	
	@ModelAttribute
	public IntPackagePricePlan get(@RequestParam(required=false) String id) {
		IntPackagePricePlan entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = intPackagePricePlanService.get(id);
		}
		if (entity == null){
			entity = new IntPackagePricePlan();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:intPackagePricePlan:view")
	@RequestMapping(value = {"list", ""})
	public String list(IntPackagePricePlan intPackagePricePlan, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<IntPackagePricePlan> page = intPackagePricePlanService.findPage(new Page<IntPackagePricePlan>(request, response), intPackagePricePlan); 
		model.addAttribute("page", page);
		return "modules/mod/intPackagePricePlanList";
	}

	@RequiresPermissions("mod:intPackagePricePlan:view")
	@RequestMapping(value = "form")
	public String form(IntPackagePricePlan intPackagePricePlan, Model model) {
		model.addAttribute("intPackagePricePlan", intPackagePricePlan);
		return "modules/mod/intPackagePricePlanForm";
	}

	@RequiresPermissions("mod:intPackagePricePlan:edit")
	@RequestMapping(value = "save")
	public String save(IntPackagePricePlan intPackagePricePlan, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, intPackagePricePlan)){
			return form(intPackagePricePlan, model);
		}
		intPackagePricePlanService.save(intPackagePricePlan);
		addMessage(redirectAttributes, "保存订阅套餐明细成功");
		return "redirect:"+Global.getAdminPath()+"/mod/intPackagePricePlan/?repage";
	}
	
	@RequiresPermissions("mod:intPackagePricePlan:edit")
	@RequestMapping(value = "delete")
	public String delete(IntPackagePricePlan intPackagePricePlan, RedirectAttributes redirectAttributes) {
		intPackagePricePlanService.delete(intPackagePricePlan);
		addMessage(redirectAttributes, "删除订阅套餐明细成功");
		return "redirect:"+Global.getAdminPath()+"/mod/intPackagePricePlan/?repage";
	}

}