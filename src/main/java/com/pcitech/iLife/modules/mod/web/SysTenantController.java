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
import com.pcitech.iLife.modules.mod.entity.SysTenant;
import com.pcitech.iLife.modules.mod.service.SysTenantService;

/**
 * 系统租户Controller
 * @author ilife
 * @version 2023-06-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/sysTenant")
public class SysTenantController extends BaseController {

	@Autowired
	private SysTenantService sysTenantService;
	
	@ModelAttribute
	public SysTenant get(@RequestParam(required=false) String id) {
		SysTenant entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysTenantService.get(id);
		}
		if (entity == null){
			entity = new SysTenant();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:sysTenant:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysTenant sysTenant, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysTenant> page = sysTenantService.findPage(new Page<SysTenant>(request, response), sysTenant); 
		model.addAttribute("page", page);
		return "modules/mod/sysTenantList";
	}

	@RequiresPermissions("mod:sysTenant:view")
	@RequestMapping(value = "form")
	public String form(SysTenant sysTenant, Model model) {
		model.addAttribute("sysTenant", sysTenant);
		return "modules/mod/sysTenantForm";
	}

	@RequiresPermissions("mod:sysTenant:edit")
	@RequestMapping(value = "save")
	public String save(SysTenant sysTenant, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysTenant)){
			return form(sysTenant, model);
		}
		sysTenantService.save(sysTenant);
		addMessage(redirectAttributes, "保存系统租户成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysTenant/?repage";
	}
	
	@RequiresPermissions("mod:sysTenant:edit")
	@RequestMapping(value = "delete")
	public String delete(SysTenant sysTenant, RedirectAttributes redirectAttributes) {
		sysTenantService.delete(sysTenant);
		addMessage(redirectAttributes, "删除系统租户成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysTenant/?repage";
	}

}