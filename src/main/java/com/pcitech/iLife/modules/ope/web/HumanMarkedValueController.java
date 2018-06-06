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
import com.pcitech.iLife.modules.ope.entity.HumanMarkedValue;
import com.pcitech.iLife.modules.ope.service.HumanMarkedValueService;

/**
 * 数据标注Controller
 * @author chenci
 * @version 2017-09-28
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/humanMarkedValue")
public class HumanMarkedValueController extends BaseController {

	@Autowired
	private HumanMarkedValueService humanMarkedValueService;
	
	@ModelAttribute
	public HumanMarkedValue get(@RequestParam(required=false) String id) {
		HumanMarkedValue entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = humanMarkedValueService.get(id);
		}
		if (entity == null){
			entity = new HumanMarkedValue();
		}
		return entity;
	}
	
	@RequiresPermissions("ope:humanMarkedValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(HumanMarkedValue humanMarkedValue, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<HumanMarkedValue> page = humanMarkedValueService.findPage(new Page<HumanMarkedValue>(request, response), humanMarkedValue); 
		model.addAttribute("page", page);
		return "modules/ope/humanMarkedValueList";
	}

	@RequiresPermissions("ope:humanMarkedValue:view")
	@RequestMapping(value = "form")
	public String form(HumanMarkedValue humanMarkedValue, Model model) {
		model.addAttribute("humanMarkedValue", humanMarkedValue);
		return "modules/ope/humanMarkedValueForm";
	}

	@RequiresPermissions("ope:humanMarkedValue:edit")
	@RequestMapping(value = "save")
	public String save(HumanMarkedValue humanMarkedValue, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, humanMarkedValue)){
			return form(humanMarkedValue, model);
		}
		humanMarkedValueService.save(humanMarkedValue);
		addMessage(redirectAttributes, "保存数据标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/humanMarkedValue/?repage";
	}
	
	@RequiresPermissions("ope:humanMarkedValue:edit")
	@RequestMapping(value = "delete")
	public String delete(HumanMarkedValue humanMarkedValue, RedirectAttributes redirectAttributes) {
		humanMarkedValueService.delete(humanMarkedValue);
		addMessage(redirectAttributes, "删除数据标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/humanMarkedValue/?repage";
	}

}