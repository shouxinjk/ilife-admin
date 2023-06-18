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
import com.pcitech.iLife.modules.mod.entity.StoSoftware;
import com.pcitech.iLife.modules.mod.service.StoSoftwareService;

/**
 * SaaS产品Controller
 * @author ilife
 * @version 2023-06-18
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/stoSoftware")
public class StoSoftwareController extends BaseController {

	@Autowired
	private StoSoftwareService stoSoftwareService;
	
	@ModelAttribute
	public StoSoftware get(@RequestParam(required=false) String id) {
		StoSoftware entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = stoSoftwareService.get(id);
		}
		if (entity == null){
			entity = new StoSoftware();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:stoSoftware:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoSoftware stoSoftware, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StoSoftware> page = stoSoftwareService.findPage(new Page<StoSoftware>(request, response), stoSoftware); 
		model.addAttribute("page", page);
		return "modules/mod/stoSoftwareList";
	}

	@RequiresPermissions("mod:stoSoftware:view")
	@RequestMapping(value = "form")
	public String form(StoSoftware stoSoftware, Model model) {
		model.addAttribute("stoSoftware", stoSoftware);
		return "modules/mod/stoSoftwareForm";
	}

	@RequiresPermissions("mod:stoSoftware:edit")
	@RequestMapping(value = "save")
	public String save(StoSoftware stoSoftware, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stoSoftware)){
			return form(stoSoftware, model);
		}
		stoSoftwareService.save(stoSoftware);
		addMessage(redirectAttributes, "保存SaaS产品成功");
		return "redirect:"+Global.getAdminPath()+"/mod/stoSoftware/?repage";
	}
	
	@RequiresPermissions("mod:stoSoftware:edit")
	@RequestMapping(value = "delete")
	public String delete(StoSoftware stoSoftware, RedirectAttributes redirectAttributes) {
		stoSoftwareService.delete(stoSoftware);
		addMessage(redirectAttributes, "删除SaaS产品成功");
		return "redirect:"+Global.getAdminPath()+"/mod/stoSoftware/?repage";
	}

}