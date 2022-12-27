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
import com.pcitech.iLife.modules.mod.entity.CategoryBroker;
import com.pcitech.iLife.modules.mod.service.CategoryBrokerService;

/**
 * 类目专家授权Controller
 * @author ilife
 * @version 2022-12-27
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/categoryBroker")
public class CategoryBrokerController extends BaseController {

	@Autowired
	private CategoryBrokerService categoryBrokerService;
	
	@ModelAttribute
	public CategoryBroker get(@RequestParam(required=false) String id) {
		CategoryBroker entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = categoryBrokerService.get(id);
		}
		if (entity == null){
			entity = new CategoryBroker();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:categoryBroker:view")
	@RequestMapping(value = {"list", ""})
	public String list(CategoryBroker categoryBroker, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CategoryBroker> page = categoryBrokerService.findPage(new Page<CategoryBroker>(request, response), categoryBroker); 
		model.addAttribute("page", page);
		return "modules/mod/categoryBrokerList";
	}

	@RequiresPermissions("mod:categoryBroker:view")
	@RequestMapping(value = "form")
	public String form(CategoryBroker categoryBroker, Model model) {
		model.addAttribute("categoryBroker", categoryBroker);
		return "modules/mod/categoryBrokerForm";
	}

	@RequiresPermissions("mod:categoryBroker:edit")
	@RequestMapping(value = "save")
	public String save(CategoryBroker categoryBroker, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, categoryBroker)){
			return form(categoryBroker, model);
		}
		categoryBrokerService.save(categoryBroker);
		addMessage(redirectAttributes, "保存类目专家授权成功");
		return "redirect:"+Global.getAdminPath()+"/mod/categoryBroker/?repage";
	}
	
	@RequiresPermissions("mod:categoryBroker:edit")
	@RequestMapping(value = "delete")
	public String delete(CategoryBroker categoryBroker, RedirectAttributes redirectAttributes) {
		categoryBrokerService.delete(categoryBroker);
		addMessage(redirectAttributes, "删除类目专家授权成功");
		return "redirect:"+Global.getAdminPath()+"/mod/categoryBroker/?repage";
	}

}