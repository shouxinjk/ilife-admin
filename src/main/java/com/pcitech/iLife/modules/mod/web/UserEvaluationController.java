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
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.mod.service.UserEvaluationService;

/**
 * 用户主观评价Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userEvaluation")
public class UserEvaluationController extends BaseController {

	@Autowired
	private UserEvaluationService userEvaluationService;
	
	@ModelAttribute
	public UserEvaluation get(@RequestParam(required=false) String id) {
		UserEvaluation entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userEvaluationService.get(id);
		}
		if (entity == null){
			entity = new UserEvaluation();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userEvaluation:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserEvaluation userEvaluation, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserEvaluation> page = userEvaluationService.findPage(new Page<UserEvaluation>(request, response), userEvaluation); 
		model.addAttribute("page", page);
		return "modules/mod/userEvaluationList";
	}

	@RequiresPermissions("mod:userEvaluation:view")
	@RequestMapping(value = "form")
	public String form(UserEvaluation userEvaluation, Model model) {
		model.addAttribute("userEvaluation", userEvaluation);
		return "modules/mod/userEvaluationForm";
	}

	@RequiresPermissions("mod:userEvaluation:edit")
	@RequestMapping(value = "save")
	public String save(UserEvaluation userEvaluation, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userEvaluation)){
			return form(userEvaluation, model);
		}
		userEvaluationService.save(userEvaluation);
		addMessage(redirectAttributes, "保存用户主观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluation/?repage";
	}
	
	@RequiresPermissions("mod:userEvaluation:edit")
	@RequestMapping(value = "delete")
	public String delete(UserEvaluation userEvaluation, RedirectAttributes redirectAttributes) {
		userEvaluationService.delete(userEvaluation);
		addMessage(redirectAttributes, "删除用户主观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluation/?repage";
	}

}