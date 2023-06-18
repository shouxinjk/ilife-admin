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
import com.pcitech.iLife.modules.mod.entity.StoSalePackage;
import com.pcitech.iLife.modules.mod.service.StoSalePackageService;

/**
 * SaaS套餐Controller
 * @author ilife
 * @version 2023-06-18
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/stoSalePackage")
public class StoSalePackageController extends BaseController {

	@Autowired
	private StoSalePackageService stoSalePackageService;
	
	@ModelAttribute
	public StoSalePackage get(@RequestParam(required=false) String id) {
		StoSalePackage entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = stoSalePackageService.get(id);
		}
		if (entity == null){
			entity = new StoSalePackage();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:stoSalePackage:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoSalePackage stoSalePackage, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StoSalePackage> page = stoSalePackageService.findPage(new Page<StoSalePackage>(request, response), stoSalePackage); 
		model.addAttribute("page", page);
		return "modules/mod/stoSalePackageList";
	}

	@RequiresPermissions("mod:stoSalePackage:view")
	@RequestMapping(value = "form")
	public String form(StoSalePackage stoSalePackage, Model model) {
		model.addAttribute("stoSalePackage", stoSalePackage);
		return "modules/mod/stoSalePackageForm";
	}

	@RequiresPermissions("mod:stoSalePackage:edit")
	@RequestMapping(value = "save")
	public String save(StoSalePackage stoSalePackage, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stoSalePackage)){
			return form(stoSalePackage, model);
		}
		stoSalePackageService.save(stoSalePackage);
		addMessage(redirectAttributes, "保存SaaS套餐成功");
		return "redirect:"+Global.getAdminPath()+"/mod/stoSalePackage/?repage";
	}
	
	@RequiresPermissions("mod:stoSalePackage:edit")
	@RequestMapping(value = "delete")
	public String delete(StoSalePackage stoSalePackage, RedirectAttributes redirectAttributes) {
		stoSalePackageService.delete(stoSalePackage);
		addMessage(redirectAttributes, "删除SaaS套餐成功");
		return "redirect:"+Global.getAdminPath()+"/mod/stoSalePackage/?repage";
	}

}