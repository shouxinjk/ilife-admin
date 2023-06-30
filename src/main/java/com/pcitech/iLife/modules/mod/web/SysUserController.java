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
import com.pcitech.iLife.modules.mod.entity.SysUser;
import com.pcitech.iLife.modules.mod.service.SysUserService;

/**
 * SaaS用户Controller
 * @author ilife
 * @version 2023-06-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/sysUser")
public class SysUserController extends BaseController {

	@Autowired
	private SysUserService sysUserService;
	
	@ModelAttribute
	public SysUser get(@RequestParam(required=false) String id) {
		SysUser entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysUserService.get(id);
		}
		if (entity == null){
			entity = new SysUser();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:sysUser:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysUser sysUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysUser> page = sysUserService.findPage(new Page<SysUser>(request, response), sysUser); 
		model.addAttribute("page", page);
		return "modules/mod/sysUserList";
	}

	@RequiresPermissions("mod:sysUser:view")
	@RequestMapping(value = "form")
	public String form(SysUser sysUser, Model model) {
		model.addAttribute("sysUser", sysUser);
		return "modules/mod/sysUserForm";
	}

	@RequiresPermissions("mod:sysUser:edit")
	@RequestMapping(value = "save")
	public String save(SysUser sysUser, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysUser)){
			return form(sysUser, model);
		}
		sysUserService.save(sysUser);
		addMessage(redirectAttributes, "保存SaaS用户成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysUser/?repage";
	}
	
	@RequiresPermissions("mod:sysUser:edit")
	@RequestMapping(value = "delete")
	public String delete(SysUser sysUser, RedirectAttributes redirectAttributes) {
		sysUserService.delete(sysUser);
		addMessage(redirectAttributes, "删除SaaS用户成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysUser/?repage";
	}

}