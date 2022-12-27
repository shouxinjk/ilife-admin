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
import com.pcitech.iLife.modules.mod.entity.Badge;
import com.pcitech.iLife.modules.mod.entity.Hierarchy;
import com.pcitech.iLife.modules.mod.service.BadgeService;

/**
 * 勋章Controller
 * @author ilife
 * @version 2022-12-27
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/badge")
public class BadgeController extends BaseController {

	@Autowired
	private BadgeService badgeService;
	
	@ModelAttribute
	public Badge get(@RequestParam(required=false) String id) {
		Badge entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = badgeService.get(id);
		}
		if (entity == null){
			entity = new Badge();
		}
		return entity;
	}
	
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Badge badge, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Badge> list =badgeService.findList(badge);
		for (int i=0; i<list.size(); i++){
			Badge e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
			mapList.add(map);
			
		}
		return mapList;
	}
	
	@RequiresPermissions("mod:badge:view")
	@RequestMapping(value = {"list", ""})
	public String list(Badge badge, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Badge> page = badgeService.findPage(new Page<Badge>(request, response), badge); 
		model.addAttribute("page", page);
		return "modules/mod/badgeList";
	}

	@RequiresPermissions("mod:badge:view")
	@RequestMapping(value = "form")
	public String form(Badge badge, Model model) {
		model.addAttribute("badge", badge);
		return "modules/mod/badgeForm";
	}

	@RequiresPermissions("mod:badge:edit")
	@RequestMapping(value = "save")
	public String save(Badge badge, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, badge)){
			return form(badge, model);
		}
		badgeService.save(badge);
		addMessage(redirectAttributes, "保存勋章成功");
		return "redirect:"+Global.getAdminPath()+"/mod/badge/?repage";
	}
	
	@RequiresPermissions("mod:badge:edit")
	@RequestMapping(value = "delete")
	public String delete(Badge badge, RedirectAttributes redirectAttributes) {
		badgeService.delete(badge);
		addMessage(redirectAttributes, "删除勋章成功");
		return "redirect:"+Global.getAdminPath()+"/mod/badge/?repage";
	}

}