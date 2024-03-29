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
import com.pcitech.iLife.modules.mod.entity.IntTenantSoftware;
import com.pcitech.iLife.modules.mod.service.IntTenantSoftwareService;

/**
 * 租户订阅产品Controller
 * @author ilife
 * @version 2023-06-18
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/intTenantSoftware")
public class IntTenantSoftwareController extends BaseController {

	@Autowired
	private IntTenantSoftwareService intTenantSoftwareService;
	
	@ModelAttribute
	public IntTenantSoftware get(@RequestParam(required=false) String id) {
		IntTenantSoftware entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = intTenantSoftwareService.get(id);
		}
		if (entity == null){
			entity = new IntTenantSoftware();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:intTenantSoftware:view")
	@RequestMapping(value = {"list", ""})
	public String list(IntTenantSoftware intTenantSoftware, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<IntTenantSoftware> page = intTenantSoftwareService.findPage(new Page<IntTenantSoftware>(request, response), intTenantSoftware); 
		model.addAttribute("page", page);
		return "modules/mod/intTenantSoftwareList";
	}

	@RequiresPermissions("mod:intTenantSoftware:view")
	@RequestMapping(value = "form")
	public String form(IntTenantSoftware intTenantSoftware, Model model) {
		model.addAttribute("intTenantSoftware", intTenantSoftware);
		return "modules/mod/intTenantSoftwareForm";
	}

	@RequiresPermissions("mod:intTenantSoftware:edit")
	@RequestMapping(value = "save")
	public String save(IntTenantSoftware intTenantSoftware, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, intTenantSoftware)){
			return form(intTenantSoftware, model);
		}
		intTenantSoftwareService.save(intTenantSoftware);
		addMessage(redirectAttributes, "保存租户订阅产品成功");
		return "redirect:"+Global.getAdminPath()+"/mod/intTenantSoftware/?repage";
	}
	
	@RequiresPermissions("mod:intTenantSoftware:edit")
	@RequestMapping(value = "delete")
	public String delete(IntTenantSoftware intTenantSoftware, RedirectAttributes redirectAttributes) {
		intTenantSoftwareService.delete(intTenantSoftware);
		addMessage(redirectAttributes, "删除租户订阅产品成功");
		return "redirect:"+Global.getAdminPath()+"/mod/intTenantSoftware/?repage";
	}

}