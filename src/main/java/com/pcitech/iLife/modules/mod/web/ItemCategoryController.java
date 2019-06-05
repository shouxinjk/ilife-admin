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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.OccasionService;

/**
 * 商品分类Controller
 * @author chenci
 * @version 2017-09-22
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/itemCategory")
public class ItemCategoryController extends BaseController {

	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private MotivationService motivationService;
	@Autowired
	private OccasionService occasionService;
	
	@ModelAttribute
	public ItemCategory get(@RequestParam(required=false) String id) {
		ItemCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = itemCategoryService.get(id);
		}
		if (entity == null){
			entity = new ItemCategory();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:itemCategory:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemCategory itemCategory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<ItemCategory> list = Lists.newArrayList();
		List<ItemCategory> sourcelist = itemCategoryService.findTree();
		ItemCategory.sortList(list, sourcelist, "1",true);
		for(ItemCategory category:list){
			category.setMotivationNames(motivationService.getMotivationNames(category.getMotivationIds()));
			category.setOccasionNames(occasionService.getOccasionNames(category.getOccasionIds()));
		}
        model.addAttribute("list", list);
		return "modules/mod/itemCategoryList";
	}

	@RequiresPermissions("mod:itemCategory:view")
	@RequestMapping(value = "form")
	public String form(ItemCategory itemCategory, Model model) {
		if (itemCategory.getParent()==null||itemCategory.getParent().getId()==null){
			itemCategory.setParent(new ItemCategory("1"));
		}
		ItemCategory parent = itemCategoryService.get(itemCategory.getParent().getId());
		itemCategory.setParent(parent);
		String names=motivationService.getMotivationNames(itemCategory.getMotivationIds());
		itemCategory.setMotivationNames(names);
		itemCategory.setOccasionNames(occasionService.getOccasionNames(itemCategory.getOccasionIds()));
		// 获取排序号，最末节点排序号+30
		if (StringUtils.isBlank(itemCategory.getId())){
			List<ItemCategory> list = Lists.newArrayList();
			List<ItemCategory> sourcelist = itemCategoryService.findTree();
			ItemCategory.sortList(list, sourcelist, itemCategory.getParentId(), false);
			if (list.size() > 0){
				itemCategory.setSort(list.get(list.size()-1).getSort() + 30);
			}
		}
		model.addAttribute("itemCategory", itemCategory);
		return "modules/mod/itemCategoryForm";
	}

	@RequiresPermissions("mod:itemCategory:edit")
	@RequestMapping(value = "save")
	public String save(ItemCategory itemCategory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, itemCategory)){
			return form(itemCategory, model);
		}
		itemCategoryService.save(itemCategory);
		addMessage(redirectAttributes, "保存商品分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/itemCategory/?repage";
	}
	
	@RequiresPermissions("mod:itemCategory:edit")
	@RequestMapping(value = "delete")
	public String delete(ItemCategory itemCategory, RedirectAttributes redirectAttributes) {
		itemCategoryService.delete(itemCategory);
		addMessage(redirectAttributes, "删除商品分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/itemCategory/?repage";
	}
	
	/**
	 * 批量修改排序
	 */
	@RequiresPermissions("mod:itemCategory:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
    	int len = ids.length;
    	ItemCategory[] entitys = new ItemCategory[len];
    	for (int i = 0; i < len; i++) {
    		entitys[i] = itemCategoryService.get(ids[i]);
    		entitys[i].setSort(sorts[i]);
    		itemCategoryService.save(entitys[i]);
    	}
    	addMessage(redirectAttributes, "保存排序成功!");
		return "redirect:" + adminPath + "/mod/itemCategory/";
	}

	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ItemCategory> list = itemCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			ItemCategory e = list.get(i);
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
	
	@ResponseBody
	@RequestMapping(value = "categories")
	public List<Map<String, Object>> listCategoryByParentId( @RequestParam(required=false) String parentId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		//List<ItemCategory> list = itemCategoryService.findTree();
		
		if(parentId==null || parentId.trim().length()==0) parentId = "1";//默认查询根目录下的节点
		
		//注意：受限于jstree，本方法返回结果均根据jstree数据要求定制
		List<ItemCategory> list = itemCategoryService.findByParentId(parentId);
		for (int i=0; i<list.size(); i++){
			ItemCategory e = list.get(i);
			//if ( e.getParentId()==parentId){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId()=="1"?"#":e.getId());
				map.put("pId", (e.getParent()==null||e.getParentId()=="1")?"#":e.getParent().getId());
				map.put("text", e.getName());
				map.put("children", true);
				mapList.add(map);
			//}
		}
		return mapList;
	}
	
}