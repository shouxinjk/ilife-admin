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
import com.pcitech.iLife.modules.mod.entity.SysDepart;
import com.pcitech.iLife.modules.mod.service.SysDepartService;

/**
 * 租户部门Controller
 * @author ilife
 * @version 2023-06-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/sysDepart")
public class SysDepartController extends BaseController {

	@Autowired
	private SysDepartService sysDepartService;
	
	@ModelAttribute
	public SysDepart get(@RequestParam(required=false) String id) {
		SysDepart entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysDepartService.get(id);
		}
		if (entity == null){
			entity = new SysDepart();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:sysDepart:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysDepart sysDepart, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysDepart> page = sysDepartService.findPage(new Page<SysDepart>(request, response), sysDepart); 
		model.addAttribute("page", page);
		return "modules/mod/sysDepartList";
	}

	@RequiresPermissions("mod:sysDepart:view")
	@RequestMapping(value = "form")
	public String form(SysDepart sysDepart, Model model) {
		model.addAttribute("sysDepart", sysDepart);
		return "modules/mod/sysDepartForm";
	}

	@RequiresPermissions("mod:sysDepart:edit")
	@RequestMapping(value = "save")
	public String save(SysDepart sysDepart, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysDepart)){
			return form(sysDepart, model);
		}
		sysDepartService.save(sysDepart);
		addMessage(redirectAttributes, "保存租户部门成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysDepart/?repage";
	}
	
	@RequiresPermissions("mod:sysDepart:edit")
	@RequestMapping(value = "delete")
	public String delete(SysDepart sysDepart, RedirectAttributes redirectAttributes) {
		sysDepartService.delete(sysDepart);
		addMessage(redirectAttributes, "删除租户部门成功");
		return "redirect:"+Global.getAdminPath()+"/mod/sysDepart/?repage";
	}

}