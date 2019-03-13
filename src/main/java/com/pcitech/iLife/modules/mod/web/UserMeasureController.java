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
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;

/**
 * 用户属性定义Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userMeasure")
public class UserMeasureController extends BaseController {

	@Autowired
	private UserMeasureService userMeasureService;
	
	@ModelAttribute
	public UserMeasure get(@RequestParam(required=false) String id) {
		UserMeasure entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userMeasureService.get(id);
		}
		if (entity == null){
			entity = new UserMeasure();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserMeasure userMeasure, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserMeasure> page = userMeasureService.findPage(new Page<UserMeasure>(request, response), userMeasure); 
		model.addAttribute("page", page);
		return "modules/mod/userMeasureList";
	}

	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = "form")
	public String form(UserMeasure userMeasure, Model model) {
		model.addAttribute("userMeasure", userMeasure);
		return "modules/mod/userMeasureForm";
	}

	@RequiresPermissions("mod:userMeasure:edit")
	@RequestMapping(value = "save")
	public String save(UserMeasure userMeasure, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userMeasure)){
			return form(userMeasure, model);
		}
		userMeasureService.save(userMeasure);
		addMessage(redirectAttributes, "保存用户属性定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userMeasure/?repage";
	}
	
	@RequiresPermissions("mod:userMeasure:edit")
	@RequestMapping(value = "delete")
	public String delete(UserMeasure userMeasure, RedirectAttributes redirectAttributes) {
		userMeasureService.delete(userMeasure);
		addMessage(redirectAttributes, "删除用户属性定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userMeasure/?repage";
	}

}