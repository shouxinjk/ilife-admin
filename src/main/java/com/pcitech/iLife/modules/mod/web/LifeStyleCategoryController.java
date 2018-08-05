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
import com.pcitech.iLife.modules.mod.entity.LifeStyleCategory;
import com.pcitech.iLife.modules.mod.service.LifeStyleCategoryService;

/**
 * vals分类Controller
 * @author chenci
 * @version 2018-08-05
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/lifeStyleCategory")
public class LifeStyleCategoryController extends BaseController {

	@Autowired
	private LifeStyleCategoryService lifeStyleCategoryService;
	
	@ModelAttribute
	public LifeStyleCategory get(@RequestParam(required=false) String id) {
		LifeStyleCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = lifeStyleCategoryService.get(id);
		}
		if (entity == null){
			entity = new LifeStyleCategory();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:lifeStyleCategory:view")
	@RequestMapping(value = {"list", ""})
	public String list(LifeStyleCategory lifeStyleCategory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<LifeStyleCategory> list = Lists.newArrayList();
		List<LifeStyleCategory> sourcelist = lifeStyleCategoryService.findTree();
		LifeStyleCategory.sortList(list, sourcelist, "1",true);
		model.addAttribute("list", list);
		return "modules/mod/lifeStyleCategoryList";
	}

	@RequiresPermissions("mod:lifeStyleCategory:view")
	@RequestMapping(value = "form")
	public String form(LifeStyleCategory lifeStyleCategory, Model model) {
		if (lifeStyleCategory.getParent()==null||lifeStyleCategory.getParent().getId()==null){
			lifeStyleCategory.setParent(new LifeStyleCategory("1"));
		}
		lifeStyleCategory.setParent(lifeStyleCategoryService.get(lifeStyleCategory.getParent().getId()));
		// 获取排序号，最末节点排序号+30
		if (StringUtils.isBlank(lifeStyleCategory.getId())){
			LifeStyleCategory lifeStyleCategoryChild = new LifeStyleCategory();
			lifeStyleCategoryChild.setParent(new LifeStyleCategory(lifeStyleCategory.getParent().getId()));
			List<LifeStyleCategory> list = lifeStyleCategoryService.findList(lifeStyleCategory);
			if (list.size() > 0){
				lifeStyleCategory.setSort(list.get(list.size()-1).getSort());
				if (lifeStyleCategory.getSort() != null){
					lifeStyleCategory.setSort(lifeStyleCategory.getSort() + 30);
				}
			}
		}
		if (lifeStyleCategory.getSort() == null){
			lifeStyleCategory.setSort(30);
		}
		model.addAttribute("lifeStyleCategory", lifeStyleCategory);
		return "modules/mod/lifeStyleCategoryForm";
	}

	@RequiresPermissions("mod:lifeStyleCategory:edit")
	@RequestMapping(value = "save")
	public String save(LifeStyleCategory lifeStyleCategory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, lifeStyleCategory)){
			return form(lifeStyleCategory, model);
		}
		lifeStyleCategoryService.save(lifeStyleCategory);
		addMessage(redirectAttributes, "保存vals分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/lifeStyleCategory/?repage";
	}
	
	@RequiresPermissions("mod:lifeStyleCategory:edit")
	@RequestMapping(value = "delete")
	public String delete(LifeStyleCategory lifeStyleCategory, RedirectAttributes redirectAttributes) {
		lifeStyleCategoryService.delete(lifeStyleCategory);
		addMessage(redirectAttributes, "删除vals分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/lifeStyleCategory/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<LifeStyleCategory> list = lifeStyleCategoryService.findList(new LifeStyleCategory());
		for (int i=0; i<list.size(); i++){
			LifeStyleCategory e = list.get(i);
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
	
}