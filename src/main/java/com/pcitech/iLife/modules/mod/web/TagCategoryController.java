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
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.TagCategory;
import com.pcitech.iLife.modules.mod.service.TagCategoryService;

/**
 * 标签分类Controller
 * @author chenci
 * @version 2017-09-26
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/tagCategory")
public class TagCategoryController extends BaseController {

	@Autowired
	private TagCategoryService tagCategoryService;
	
	@ModelAttribute
	public TagCategory get(@RequestParam(required=false) String id) {
		TagCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tagCategoryService.get(id);
		}
		if (entity == null){
			entity = new TagCategory();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:tagCategory:view")
	@RequestMapping(value = {"list", ""})
	public String list(TagCategory tagCategory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<TagCategory> list = Lists.newArrayList();
		List<TagCategory> sourcelist = tagCategoryService.findTree(); 
		TagCategory.sortList(list, sourcelist, "0",true);
		model.addAttribute("list", list);
		return "modules/mod/tagCategoryList";
	}

	@RequiresPermissions("mod:tagCategory:view")
	@RequestMapping(value = "form")
	public String form(TagCategory tagCategory, Model model) {
		if (tagCategory.getParent()!=null && StringUtils.isNotBlank(tagCategory.getParent().getId())){
			tagCategory.setParent(tagCategoryService.get(tagCategory.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(tagCategory.getId())){
				TagCategory tagCategoryChild = new TagCategory();
				tagCategoryChild.setParent(new TagCategory(tagCategory.getParent().getId()));
				List<TagCategory> list = tagCategoryService.findList(tagCategory); 
				if (list.size() > 0){
					tagCategory.setSort(list.get(list.size()-1).getSort());
					if (tagCategory.getSort() != null){
						tagCategory.setSort(tagCategory.getSort() + 30);
					}
				}
			}
		}
		if (tagCategory.getSort() == null){
			tagCategory.setSort(30);
		}
		model.addAttribute("tagCategory", tagCategory);
		return "modules/mod/tagCategoryForm";
	}

	@RequiresPermissions("mod:tagCategory:edit")
	@RequestMapping(value = "save")
	public String save(TagCategory tagCategory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, tagCategory)){
			return form(tagCategory, model);
		}
		tagCategoryService.save(tagCategory);
		addMessage(redirectAttributes, "保存标签分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/tagCategory/?repage";
	}
	
	@RequiresPermissions("mod:tagCategory:edit")
	@RequestMapping(value = "delete")
	public String delete(TagCategory tagCategory, RedirectAttributes redirectAttributes) {
		tagCategoryService.delete(tagCategory);
		addMessage(redirectAttributes, "删除标签分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/tagCategory/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<TagCategory> list = tagCategoryService.findList(new TagCategory());
		for (int i=0; i<list.size(); i++){
			TagCategory e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	/**
	 * 批量修改排序
	 */
	@RequiresPermissions("mod:phase:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
    	int len = ids.length;
    	TagCategory[] entitys = new TagCategory[len];
    	for (int i = 0; i < len; i++) {
    		entitys[i] = tagCategoryService.get(ids[i]);
    		entitys[i].setSort(sorts[i]);
    		tagCategoryService.save(entitys[i]);
    	}
    	addMessage(redirectAttributes, "保存排序成功!");
		return "redirect:" + adminPath + "/mod/tagCategory/";
	}
}