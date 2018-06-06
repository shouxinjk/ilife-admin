/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.web;

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
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.ope.service.PerformanceService;

/**
 * 标注Controller
 * @author chenci
 * @version 2017-09-28
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/performance")
public class PerformanceController extends BaseController {

	@Autowired
	private PerformanceService performanceService;
	
	@ModelAttribute
	public Performance get(@RequestParam(required=false) String id) {
		Performance entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = performanceService.get(id);
		}
		if (entity == null){
			entity = new Performance();
		}
		return entity;
	}
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = {"list", ""})
	public String list(Performance performance, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Performance> page = performanceService.findPage(new Page<Performance>(request, response), performance); 
		model.addAttribute("page", page);
		return "modules/ope/performanceList";
	}

	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = "form")
	public String form(Performance performance, Model model) {
		model.addAttribute("performance", performance);
		return "modules/ope/performanceForm";
	}

	@RequiresPermissions("ope:performance:edit")
	@RequestMapping(value = "save")
	public String save(Performance performance, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, performance)){
			return form(performance, model);
		}
		performanceService.save(performance);
		addMessage(redirectAttributes, "保存标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/performance/?repage";
	}
	
	@RequiresPermissions("ope:performance:edit")
	@RequestMapping(value = "delete")
	public String delete(Performance performance, RedirectAttributes redirectAttributes) {
		performanceService.delete(performance);
		addMessage(redirectAttributes, "删除标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/performance/?repage";
	}

}