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
import com.pcitech.iLife.modules.mod.entity.LinkTemplate;
import com.pcitech.iLife.modules.mod.service.LinkTemplateService;

/**
 * 链接模板Controller
 * @author qchzhu
 * @version 2021-09-01
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/linkTemplate")
public class LinkTemplateController extends BaseController {

	@Autowired
	private LinkTemplateService linkTemplateService;
	
	@ModelAttribute
	public LinkTemplate get(@RequestParam(required=false) String id) {
		LinkTemplate entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = linkTemplateService.get(id);
		}
		if (entity == null){
			entity = new LinkTemplate();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:linkTemplate:view")
	@RequestMapping(value = {"list", ""})
	public String list(LinkTemplate linkTemplate, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<LinkTemplate> page = linkTemplateService.findPage(new Page<LinkTemplate>(request, response), linkTemplate); 
		model.addAttribute("page", page);
		return "modules/mod/linkTemplateList";
	}

	@RequiresPermissions("mod:linkTemplate:view")
	@RequestMapping(value = "form")
	public String form(LinkTemplate linkTemplate, Model model) {
		model.addAttribute("linkTemplate", linkTemplate);
		return "modules/mod/linkTemplateForm";
	}

	@RequiresPermissions("mod:linkTemplate:edit")
	@RequestMapping(value = "save")
	public String save(LinkTemplate linkTemplate, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, linkTemplate)){
			return form(linkTemplate, model);
		}
		linkTemplateService.save(linkTemplate);
		addMessage(redirectAttributes, "保存链接模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/linkTemplate/?repage";
	}
	
	@RequiresPermissions("mod:linkTemplate:edit")
	@RequestMapping(value = "delete")
	public String delete(LinkTemplate linkTemplate, RedirectAttributes redirectAttributes) {
		linkTemplateService.delete(linkTemplate);
		addMessage(redirectAttributes, "删除链接模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/linkTemplate/?repage";
	}

}