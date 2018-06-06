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
import com.pcitech.iLife.modules.mod.entity.Capability;
import com.pcitech.iLife.modules.mod.service.CapabilityService;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 资本标注Controller
 * @author chenci
 * @version 2017-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/capability")
@ApiIgnore
public class CapabilityController extends BaseController {

	@Autowired
	private CapabilityService capabilityService;
	
	@ModelAttribute
	public Capability get(@RequestParam(required=false) String id) {
		Capability entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = capabilityService.get(id);
		}
		if (entity == null){
			entity = new Capability();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:capability:view")
	@RequestMapping(value = {"list", ""})
	public String list(Capability capability, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Capability> page = capabilityService.findPage(new Page<Capability>(request, response), capability); 
		model.addAttribute("page", page);
		return "modules/mod/capabilityList";
	}

	@RequiresPermissions("mod:capability:view")
	@RequestMapping(value = "form")
	public String form(Capability capability, Model model) {
		model.addAttribute("capability", capability);
		return "modules/mod/capabilityForm";
	}

	@RequiresPermissions("mod:capability:edit")
	@RequestMapping(value = "save")
	public String save(Capability capability, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, capability)){
			return form(capability, model);
		}
		capabilityService.save(capability);
		addMessage(redirectAttributes, "保存资本标注成功");
		return "redirect:"+Global.getAdminPath()+"/mod/capability/?repage";
	}
	
	@RequiresPermissions("mod:capability:edit")
	@RequestMapping(value = "delete")
	public String delete(Capability capability, RedirectAttributes redirectAttributes) {
		capabilityService.delete(capability);
		addMessage(redirectAttributes, "删除资本标注成功");
		return "redirect:"+Global.getAdminPath()+"/mod/capability/?repage";
	}

}