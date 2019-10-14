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
import com.pcitech.iLife.modules.mod.entity.TraceCode;
import com.pcitech.iLife.modules.mod.service.TraceCodeService;

/**
 * 推广位Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/traceCode")
public class TraceCodeController extends BaseController {

	@Autowired
	private TraceCodeService traceCodeService;
	
	@ModelAttribute
	public TraceCode get(@RequestParam(required=false) String id) {
		TraceCode entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = traceCodeService.get(id);
		}
		if (entity == null){
			entity = new TraceCode();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:traceCode:view")
	@RequestMapping(value = {"list", ""})
	public String list(TraceCode traceCode, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TraceCode> page = traceCodeService.findPage(new Page<TraceCode>(request, response), traceCode); 
		model.addAttribute("page", page);
		return "modules/mod/traceCodeList";
	}

	@RequiresPermissions("mod:traceCode:view")
	@RequestMapping(value = "form")
	public String form(TraceCode traceCode, Model model) {
		model.addAttribute("traceCode", traceCode);
		return "modules/mod/traceCodeForm";
	}

	@RequiresPermissions("mod:traceCode:edit")
	@RequestMapping(value = "save")
	public String save(TraceCode traceCode, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, traceCode)){
			return form(traceCode, model);
		}
		traceCodeService.save(traceCode);
		addMessage(redirectAttributes, "保存推广位成功");
		return "redirect:"+Global.getAdminPath()+"/mod/traceCode/?repage";
	}
	
	@RequiresPermissions("mod:traceCode:edit")
	@RequestMapping(value = "delete")
	public String delete(TraceCode traceCode, RedirectAttributes redirectAttributes) {
		traceCodeService.delete(traceCode);
		addMessage(redirectAttributes, "删除推广位成功");
		return "redirect:"+Global.getAdminPath()+"/mod/traceCode/?repage";
	}

}