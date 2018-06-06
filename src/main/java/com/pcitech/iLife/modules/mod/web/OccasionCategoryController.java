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
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.OccasionCategoryService;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 外部诱因类别Controller
 * @author chenci
 * @version 2017-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/occasionCategory")
@ApiIgnore
public class OccasionCategoryController extends BaseController {

	@Autowired
	private OccasionCategoryService occasionCategoryService;
	
	@ModelAttribute
	public OccasionCategory get(@RequestParam(required=false) String id) {
		OccasionCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = occasionCategoryService.get(id);
		}
		if (entity == null){
			entity = new OccasionCategory();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:occasionCategory:view")
	@RequestMapping(value = {"list", ""})
	public String list(OccasionCategory occasionCategory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<OccasionCategory> list = Lists.newArrayList();
		List<OccasionCategory> sourcelist = occasionCategoryService.findTree();
		OccasionCategory.sortList(list, sourcelist, "1",true);
        model.addAttribute("list", list);
		return "modules/mod/occasionCategoryList";
	}

	@RequiresPermissions("mod:occasionCategory:view")
	@RequestMapping(value = "form")
	public String form(OccasionCategory occasionCategory, Model model) {
		if (occasionCategory.getParent()==null||occasionCategory.getParent().getId()==null){
			occasionCategory.setParent(new OccasionCategory("1"));
		}
		OccasionCategory parent = occasionCategoryService.get(occasionCategory.getParent().getId());
		occasionCategory.setParent(parent);
		// 获取排序号，最末节点排序号+30
		if (StringUtils.isBlank(occasionCategory.getId())){
			List<OccasionCategory> list = Lists.newArrayList();
			List<OccasionCategory> sourcelist = occasionCategoryService.findTree();
			OccasionCategory.sortList(list, sourcelist, occasionCategory.getParentId(), false);
			if (list.size() > 0){
				occasionCategory.setSort(list.get(list.size()-1).getSort() + 30);
			}
		}
		model.addAttribute("occasionCategory", occasionCategory);
		return "modules/mod/occasionCategoryForm";
	}

	@RequiresPermissions("mod:occasionCategory:edit")
	@RequestMapping(value = "save")
	public String save(OccasionCategory occasionCategory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, occasionCategory)){
			return form(occasionCategory, model);
		}
		occasionCategoryService.save(occasionCategory);
		addMessage(redirectAttributes, "保存外部诱因类别成功");
		return "redirect:"+Global.getAdminPath()+"/mod/occasionCategory/?repage";
	}
	
	@RequiresPermissions("mod:occasionCategory:edit")
	@RequestMapping(value = "delete")
	public String delete(OccasionCategory occasionCategory, RedirectAttributes redirectAttributes) {
		occasionCategoryService.delete(occasionCategory);
		addMessage(redirectAttributes, "删除外部诱因类别成功");
		return "redirect:"+Global.getAdminPath()+"/mod/occasionCategory/?repage";
	}

	/**
	 * 批量修改排序
	 */
	@RequiresPermissions("mod:occasionCategory:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
    	int len = ids.length;
    	OccasionCategory[] entitys = new OccasionCategory[len];
    	for (int i = 0; i < len; i++) {
    		entitys[i] = occasionCategoryService.get(ids[i]);
    		entitys[i].setSort(sorts[i]);
    		occasionCategoryService.save(entitys[i]);
    	}
    	addMessage(redirectAttributes, "保存排序成功!");
		return "redirect:" + adminPath + "/mod/occasionCategory/";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<OccasionCategory> list = occasionCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			OccasionCategory e = list.get(i);
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