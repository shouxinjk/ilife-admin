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
import com.pcitech.iLife.modules.mod.entity.SysRole;
import com.pcitech.iLife.modules.mod.service.SysRoleService;

/**
 * 系统角色Controller
 * @author ilife
 * @version 2023-06-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/sysRole")
public class SysRoleController extends BaseController {

	@Autowired
	private SysRoleService sysRoleService;
	
	@ModelAttribute
	public SysRole get(@RequestParam(required=false) String id) {
		SysRole entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysRoleService.get(id);
		}
		if (entity == null){
			entity = new SysRole();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:sysRole:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysRole sysRole, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysRole> page = sysRoleService.findPage(new Page<SysRole>(request, response), sysRole); 
		model.addAttribute("page", page);
		return "modules/mod/sysRoleList";
	}

	@RequiresPermissions("mod:sysRole:view")
	@RequestMapping(value = "form")
	public String form(SysRole sysRole, Model model) {
		model.addAttribute("sysRole", sysRole);
		return "modules/mod/sysRoleForm";
	}

	@RequiresPermissions("mod:sysRole:edit")
	@RequestMapping(value = "save")
	public String save(SysRole sysRole, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysRole)){
			return form(sysRole, model);
		}
		sysRoleService.save(sysRole);
		addMessage(redirectAttributes, "保存系统角色成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysRole/?repage";
	}
	
	@RequiresPermissions("mod:sysRole:edit")
	@RequestMapping(value = "delete")
	public String delete(SysRole sysRole, RedirectAttributes redirectAttributes) {
		sysRoleService.delete(sysRole);
		addMessage(redirectAttributes, "删除系统角色成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysRole/?repage";
	}

}