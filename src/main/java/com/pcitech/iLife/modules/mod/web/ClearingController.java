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
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.service.ClearingService;

/**
 * 清算Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/clearing")
public class ClearingController extends BaseController {

	@Autowired
	private ClearingService clearingService;
	
	@ModelAttribute
	public Clearing get(@RequestParam(required=false) String id) {
		Clearing entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = clearingService.get(id);
		}
		if (entity == null){
			entity = new Clearing();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:clearing:view")
	@RequestMapping(value = {"list", ""})
	public String list(Clearing clearing, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Clearing> page = clearingService.findPage(new Page<Clearing>(request, response), clearing); 
		model.addAttribute("page", page);
		return "modules/mod/clearingList";
	}

	@RequiresPermissions("mod:clearing:view")
	@RequestMapping(value = "form")
	public String form(Clearing clearing, Model model) {
		model.addAttribute("clearing", clearing);
		return "modules/mod/clearingForm";
	}

	@RequiresPermissions("mod:clearing:edit")
	@RequestMapping(value = "save")
	public String save(Clearing clearing, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, clearing)){
			return form(clearing, model);
		}
		clearingService.save(clearing);
		addMessage(redirectAttributes, "保存清算成功");
		return "redirect:"+Global.getAdminPath()+"/mod/clearing/?repage";
	}
	
	@RequiresPermissions("mod:clearing:edit")
	@RequestMapping(value = "delete")
	public String delete(Clearing clearing, RedirectAttributes redirectAttributes) {
		clearingService.delete(clearing);
		addMessage(redirectAttributes, "删除清算成功");
		return "redirect:"+Global.getAdminPath()+"/mod/clearing/?repage";
	}

}