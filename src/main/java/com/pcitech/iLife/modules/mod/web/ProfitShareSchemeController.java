/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Hierarchy;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.service.ProfitShareSchemeService;

/**
 * 分润规则Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/profitShareScheme")
public class ProfitShareSchemeController extends BaseController {

	@Autowired
	private ProfitShareSchemeService profitShareSchemeService;
	
	@ModelAttribute
	public ProfitShareScheme get(@RequestParam(required=false) String id) {
		ProfitShareScheme entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = profitShareSchemeService.get(id);
		}
		if (entity == null){
			entity = new ProfitShareScheme();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:profitShareScheme:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProfitShareScheme profitShareScheme, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProfitShareScheme> page = profitShareSchemeService.findPage(new Page<ProfitShareScheme>(request, response), profitShareScheme); 
		model.addAttribute("page", page);
		return "modules/mod/profitShareSchemeList";
	}

	@RequiresPermissions("mod:profitShareScheme:view")
	@RequestMapping(value = "form")
	public String form(ProfitShareScheme profitShareScheme, Model model) {
		model.addAttribute("profitShareScheme", profitShareScheme);
		return "modules/mod/profitShareSchemeForm";
	}

	@RequiresPermissions("mod:profitShareScheme:edit")
	@RequestMapping(value = "save")
	public String save(ProfitShareScheme profitShareScheme, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, profitShareScheme)){
			return form(profitShareScheme, model);
		}
		profitShareSchemeService.save(profitShareScheme);
		addMessage(redirectAttributes, "保存分润规则成功");
		return "redirect:"+Global.getAdminPath()+"/mod/profitShareScheme/?repage";
	}
	
	@RequiresPermissions("mod:profitShareScheme:edit")
	@RequestMapping(value = "delete")
	public String delete(ProfitShareScheme profitShareScheme, RedirectAttributes redirectAttributes) {
		profitShareSchemeService.delete(profitShareScheme);
		addMessage(redirectAttributes, "删除分润规则成功");
		return "redirect:"+Global.getAdminPath()+"/mod/profitShareScheme/?repage";
	}

	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(ProfitShareScheme scheme, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ProfitShareScheme> list =profitShareSchemeService.findList(scheme);
		for (int i=0; i<list.size(); i++){
			ProfitShareScheme e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
			mapList.add(map);
			
		}
		return mapList;
	}	
}