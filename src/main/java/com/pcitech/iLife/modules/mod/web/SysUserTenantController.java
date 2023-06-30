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
import com.pcitech.iLife.modules.mod.entity.SysUserTenant;
import com.pcitech.iLife.modules.mod.service.SysUserTenantService;

/**
 * 用户租户关联Controller
 * @author ilife
 * @version 2023-06-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/sysUserTenant")
public class SysUserTenantController extends BaseController {

	@Autowired
	private SysUserTenantService sysUserTenantService;
	
	@ModelAttribute
	public SysUserTenant get(@RequestParam(required=false) String id) {
		SysUserTenant entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysUserTenantService.get(id);
		}
		if (entity == null){
			entity = new SysUserTenant();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:sysUserTenant:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysUserTenant sysUserTenant, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysUserTenant> page = sysUserTenantService.findPage(new Page<SysUserTenant>(request, response), sysUserTenant); 
		model.addAttribute("page", page);
		return "modules/mod/sysUserTenantList";
	}

	@RequiresPermissions("mod:sysUserTenant:view")
	@RequestMapping(value = "form")
	public String form(SysUserTenant sysUserTenant, Model model) {
		model.addAttribute("sysUserTenant", sysUserTenant);
		return "modules/mod/sysUserTenantForm";
	}

	@RequiresPermissions("mod:sysUserTenant:edit")
	@RequestMapping(value = "save")
	public String save(SysUserTenant sysUserTenant, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysUserTenant)){
			return form(sysUserTenant, model);
		}
		sysUserTenantService.save(sysUserTenant);
		addMessage(redirectAttributes, "保存用户租户关联成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysUserTenant/?repage";
	}
	
	@RequiresPermissions("mod:sysUserTenant:edit")
	@RequestMapping(value = "delete")
	public String delete(SysUserTenant sysUserTenant, RedirectAttributes redirectAttributes) {
		sysUserTenantService.delete(sysUserTenant);
		addMessage(redirectAttributes, "删除用户租户关联成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysUserTenant/?repage";
	}

}