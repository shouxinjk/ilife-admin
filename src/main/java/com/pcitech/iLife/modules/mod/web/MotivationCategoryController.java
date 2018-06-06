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
import com.pcitech.iLife.modules.mod.entity.MotivationCategory;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.service.MotivationCategoryService;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 内部动机类别Controller
 * @author chenci
 * @version 2017-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/motivationCategory")
@ApiIgnore
public class MotivationCategoryController extends BaseController {

	@Autowired
	private MotivationCategoryService motivationCategoryService;
	
	@ModelAttribute
	public MotivationCategory get(@RequestParam(required=false) String id) {
		MotivationCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = motivationCategoryService.get(id);
		}
		if (entity == null){
			entity = new MotivationCategory();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:motivationCategory:view")
	@RequestMapping(value = {"list", ""})
	public String list(MotivationCategory motivationCategory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<MotivationCategory> list = Lists.newArrayList();
		List<MotivationCategory> sourcelist = motivationCategoryService.findTree();
		MotivationCategory.sortList(list, sourcelist, "1",true);
        model.addAttribute("list", list);
		return "modules/mod/motivationCategoryList";
	}

	@RequiresPermissions("mod:motivationCategory:view")
	@RequestMapping(value = "form")
	public String form(MotivationCategory motivationCategory, Model model) {
		if (motivationCategory.getParent()==null||motivationCategory.getParent().getId()==null){
			motivationCategory.setParent(new MotivationCategory("1"));
		}
		MotivationCategory parent = motivationCategoryService.get(motivationCategory.getParent().getId());
		motivationCategory.setParent(parent);
		// 获取排序号，最末节点排序号+30
		if (StringUtils.isBlank(motivationCategory.getId())){
			List<MotivationCategory> list = Lists.newArrayList();
			List<MotivationCategory> sourcelist = motivationCategoryService.findTree();
			MotivationCategory.sortList(list, sourcelist, motivationCategory.getParentId(), false);
			if (list.size() > 0){
				motivationCategory.setSort(list.get(list.size()-1).getSort() + 30);
			}
		}
		model.addAttribute("motivationCategory", motivationCategory);
		return "modules/mod/motivationCategoryForm";
	}

	@RequiresPermissions("mod:motivationCategory:edit")
	@RequestMapping(value = "save")
	public String save(MotivationCategory motivationCategory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, motivationCategory)){
			return form(motivationCategory, model);
		}
		motivationCategoryService.save(motivationCategory);
		addMessage(redirectAttributes, "保存内部动机类别成功");
		return "redirect:"+Global.getAdminPath()+"/mod/motivationCategory/?repage";
	}
	
	@RequiresPermissions("mod:motivationCategory:edit")
	@RequestMapping(value = "delete")
	public String delete(MotivationCategory motivationCategory, RedirectAttributes redirectAttributes) {
		motivationCategoryService.delete(motivationCategory);
		addMessage(redirectAttributes, "删除内部动机类别成功");
		return "redirect:"+Global.getAdminPath()+"/mod/motivationCategory/?repage";
	}

	@RequiresPermissions("mod:occasionCategory:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
    	int len = ids.length;
    	MotivationCategory[] entitys = new MotivationCategory[len];
    	for (int i = 0; i < len; i++) {
    		entitys[i] = motivationCategoryService.get(ids[i]);
    		entitys[i].setSort(sorts[i]);
    		motivationCategoryService.save(entitys[i]);
    	}
    	addMessage(redirectAttributes, "保存排序成功!");
		return "redirect:" + adminPath + "/mod/motivationCategory/";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<MotivationCategory> list = motivationCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			MotivationCategory e = list.get(i);
			if (extId == null || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():0);
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
}