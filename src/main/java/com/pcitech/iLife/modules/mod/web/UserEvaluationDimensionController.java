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
import com.pcitech.iLife.modules.mod.entity.UserEvaluationDimension;
import com.pcitech.iLife.modules.mod.service.UserEvaluationDimensionService;
import com.pcitech.iLife.modules.mod.service.UserEvaluationService;

/**
 * 用户主观评价-维度Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userEvaluationDimension")
public class UserEvaluationDimensionController extends BaseController {

	@Autowired
	private UserEvaluationDimensionService userEvaluationDimensionService;
	
	@Autowired
	private UserEvaluationService userEvaluationService;
	
	@ModelAttribute
	public UserEvaluationDimension get(@RequestParam(required=false) String id) {
		UserEvaluationDimension entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userEvaluationDimensionService.get(id);
		}
		if (entity == null){
			entity = new UserEvaluationDimension();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userEvaluationDimension:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserEvaluationDimension userEvaluationDimension,String evaluationId, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(userEvaluationDimension.getEvaluation() == null) {
			userEvaluationDimension.setEvaluation(userEvaluationService.get(evaluationId));
		}
		Page<UserEvaluationDimension> page = userEvaluationDimensionService.findPage(new Page<UserEvaluationDimension>(request, response), userEvaluationDimension); 
		model.addAttribute("page", page);
		model.addAttribute("evaluationId", userEvaluationDimension.getEvaluation().getId());
		return "modules/mod/userEvaluationDimensionList";
	}

	@RequiresPermissions("mod:userEvaluationDimension:view")
	@RequestMapping(value = "form")
	public String form(UserEvaluationDimension userEvaluationDimension, Model model) {
		if(userEvaluationDimension.getId() == null) {//对于新添加记录需要根据ID补充dimension
			userEvaluationDimension.setEvaluation(userEvaluationService.get(userEvaluationDimension.getEvaluation().getId()));
			userEvaluationDimension.setName(userEvaluationDimension.getEvaluation().getName());
		}
		model.addAttribute("userEvaluationDimension", userEvaluationDimension);
		return "modules/mod/userEvaluationDimensionForm";
	}

	@RequiresPermissions("mod:userEvaluationDimension:edit")
	@RequestMapping(value = "save")
	public String save(UserEvaluationDimension userEvaluationDimension, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userEvaluationDimension)){
			return form(userEvaluationDimension, model);
		}
		userEvaluationDimensionService.save(userEvaluationDimension);
		addMessage(redirectAttributes, "保存用户主观评价-维度成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluationDimension/?evaluation.id="+userEvaluationDimension.getEvaluation().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:userEvaluationDimension:edit")
	@RequestMapping(value = "delete")
	public String delete(UserEvaluationDimension userEvaluationDimension, RedirectAttributes redirectAttributes) {
		userEvaluationDimensionService.delete(userEvaluationDimension);
		addMessage(redirectAttributes, "删除用户主观评价-维度成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluationDimension/?evaluation.id="+userEvaluationDimension.getEvaluation().getId()+"&repage";
	}

}