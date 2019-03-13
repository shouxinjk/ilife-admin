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
import com.pcitech.iLife.modules.mod.entity.UserDimensionMeasure;
import com.pcitech.iLife.modules.mod.service.UserDimensionMeasureService;

/**
 * 用户客观评价-属性Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userDimensionMeasure")
public class UserDimensionMeasureController extends BaseController {

	@Autowired
	private UserDimensionMeasureService userDimensionMeasureService;
	
	@ModelAttribute
	public UserDimensionMeasure get(@RequestParam(required=false) String id) {
		UserDimensionMeasure entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userDimensionMeasureService.get(id);
		}
		if (entity == null){
			entity = new UserDimensionMeasure();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userDimensionMeasure:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserDimensionMeasure userDimensionMeasure, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserDimensionMeasure> page = userDimensionMeasureService.findPage(new Page<UserDimensionMeasure>(request, response), userDimensionMeasure); 
		model.addAttribute("page", page);
		return "modules/mod/userDimensionMeasureList";
	}

	@RequiresPermissions("mod:userDimensionMeasure:view")
	@RequestMapping(value = "form")
	public String form(UserDimensionMeasure userDimensionMeasure, Model model) {
		model.addAttribute("userDimensionMeasure", userDimensionMeasure);
		return "modules/mod/userDimensionMeasureForm";
	}

	@RequiresPermissions("mod:userDimensionMeasure:edit")
	@RequestMapping(value = "save")
	public String save(UserDimensionMeasure userDimensionMeasure, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userDimensionMeasure)){
			return form(userDimensionMeasure, model);
		}
		userDimensionMeasureService.save(userDimensionMeasure);
		addMessage(redirectAttributes, "保存用户客观评价-属性成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userDimensionMeasure/?repage";
	}
	
	@RequiresPermissions("mod:userDimensionMeasure:edit")
	@RequestMapping(value = "delete")
	public String delete(UserDimensionMeasure userDimensionMeasure, RedirectAttributes redirectAttributes) {
		userDimensionMeasureService.delete(userDimensionMeasure);
		addMessage(redirectAttributes, "删除用户客观评价-属性成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userDimensionMeasure/?repage";
	}

}