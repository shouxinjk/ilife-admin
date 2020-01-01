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
import com.pcitech.iLife.modules.mod.entity.BrokerPerformance;
import com.pcitech.iLife.modules.mod.service.BrokerPerformanceService;

/**
 * 推广效果Controller
 * @author qchzhu
 * @version 2019-12-31
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/performance")
public class BrokerPerformanceController extends BaseController {

	@Autowired
	private BrokerPerformanceService brokerPerformanceService;
	
	@ModelAttribute
	public BrokerPerformance get(@RequestParam(required=false) String id) {
		BrokerPerformance entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = brokerPerformanceService.get(id);
		}
		if (entity == null){
			entity = new BrokerPerformance();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:performance:view")
	@RequestMapping(value = {"list", ""})
	public String list(BrokerPerformance performance, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BrokerPerformance> page = brokerPerformanceService.findPage(new Page<BrokerPerformance>(request, response), performance); 
		model.addAttribute("page", page);
		return "modules/mod/performanceList";
	}

	@RequiresPermissions("mod:performance:view")
	@RequestMapping(value = "form")
	public String form(BrokerPerformance performance, Model model) {
		model.addAttribute("performance", performance);
		return "modules/mod/performanceForm";
	}

	@RequiresPermissions("mod:performance:edit")
	@RequestMapping(value = "save")
	public String save(BrokerPerformance performance, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, performance)){
			return form(performance, model);
		}
		brokerPerformanceService.save(performance);
		addMessage(redirectAttributes, "保存推广效果管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/performance/?repage";
	}
	
	@RequiresPermissions("mod:performance:edit")
	@RequestMapping(value = "delete")
	public String delete(BrokerPerformance performance, RedirectAttributes redirectAttributes) {
		brokerPerformanceService.delete(performance);
		addMessage(redirectAttributes, "删除推广效果管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/performance/?repage";
	}

}