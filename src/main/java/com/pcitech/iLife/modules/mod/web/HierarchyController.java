/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
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
import com.pcitech.iLife.modules.mod.entity.Capital;
import com.pcitech.iLife.modules.mod.entity.Hierarchy;
import com.pcitech.iLife.modules.mod.service.HierarchyService;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 社会分层Controller
 * @author chenci
 * @version 2017-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/hierarchy")
@ApiIgnore
public class HierarchyController extends BaseController {

	@Autowired
	private HierarchyService hierarchyService;
	
	@ModelAttribute
	public Hierarchy get(@RequestParam(required=false) String id) {
		Hierarchy entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = hierarchyService.get(id);
		}
		if (entity == null){
			entity = new Hierarchy();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:hierarchy:view")
	@RequestMapping(value = {"list", ""})
	public String list(Hierarchy hierarchy, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Hierarchy> page = hierarchyService.findPage(new Page<Hierarchy>(request, response), hierarchy); 
		model.addAttribute("page", page);
		return "modules/mod/hierarchyList";
	}

	@RequiresPermissions("mod:hierarchy:view")
	@RequestMapping(value = "form")
	public String form(Hierarchy hierarchy, Model model) {
		model.addAttribute("hierarchy", hierarchy);
		return "modules/mod/hierarchyForm";
	}

	@RequiresPermissions("mod:hierarchy:edit")
	@RequestMapping(value = "save")
	public String save(Hierarchy hierarchy, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, hierarchy)){
			return form(hierarchy, model);
		}
		hierarchyService.save(hierarchy);
		addMessage(redirectAttributes, "保存社会分层成功");
		return "redirect:"+Global.getAdminPath()+"/mod/hierarchy/?repage";
	}
	
	@RequiresPermissions("mod:hierarchy:edit")
	@RequestMapping(value = "delete")
	public String delete(Hierarchy hierarchy, RedirectAttributes redirectAttributes) {
		hierarchyService.delete(hierarchy);
		addMessage(redirectAttributes, "删除社会分层成功");
		return "redirect:"+Global.getAdminPath()+"/mod/hierarchy/?repage";
	}

	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Hierarchy hierarchy, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Hierarchy> list =hierarchyService.findList(hierarchy);
		for (int i=0; i<list.size(); i++){
			Hierarchy e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
//			if (type != null && "3".equals(type)){
//				map.put("isParent", true);
//			}
			mapList.add(map);
			
		}
		return mapList;
	}

}