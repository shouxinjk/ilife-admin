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
import com.pcitech.iLife.modules.mod.entity.StoPricePlan;
import com.pcitech.iLife.modules.mod.service.StoPricePlanService;

/**
 * 产品订阅计划Controller
 * @author ilife
 * @version 2023-06-18
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/stoPricePlan")
public class StoPricePlanController extends BaseController {

	@Autowired
	private StoPricePlanService stoPricePlanService;
	
	@ModelAttribute
	public StoPricePlan get(@RequestParam(required=false) String id) {
		StoPricePlan entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = stoPricePlanService.get(id);
		}
		if (entity == null){
			entity = new StoPricePlan();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:stoPricePlan:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoPricePlan stoPricePlan, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StoPricePlan> page = stoPricePlanService.findPage(new Page<StoPricePlan>(request, response), stoPricePlan); 
		model.addAttribute("page", page);
		return "modules/mod/stoPricePlanList";
	}

	@RequiresPermissions("mod:stoPricePlan:view")
	@RequestMapping(value = "form")
	public String form(StoPricePlan stoPricePlan, Model model) {
		model.addAttribute("stoPricePlan", stoPricePlan);
		return "modules/mod/stoPricePlanForm";
	}

	@RequiresPermissions("mod:stoPricePlan:edit")
	@RequestMapping(value = "save")
	public String save(StoPricePlan stoPricePlan, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stoPricePlan)){
			return form(stoPricePlan, model);
		}
		stoPricePlanService.save(stoPricePlan);
		addMessage(redirectAttributes, "保存产品订阅计划成功");
		return "redirect:"+Global.getAdminPath()+"/mod/stoPricePlan/?repage";
	}
	
	@RequiresPermissions("mod:stoPricePlan:edit")
	@RequestMapping(value = "delete")
	public String delete(StoPricePlan stoPricePlan, RedirectAttributes redirectAttributes) {
		stoPricePlanService.delete(stoPricePlan);
		addMessage(redirectAttributes, "删除产品订阅计划成功");
		return "redirect:"+Global.getAdminPath()+"/mod/stoPricePlan/?repage";
	}

}