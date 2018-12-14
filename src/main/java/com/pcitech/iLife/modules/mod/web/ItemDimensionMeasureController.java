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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.entity.ItemDimensionMeasure;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionMeasureService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionService;

/**
 * 客观评价明细Controller
 * @author qchzhu
 * @version 2018-12-12
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/itemDimensionMeasure")
public class ItemDimensionMeasureController extends BaseController {

	@Autowired
	private ItemDimensionService itemDimensionService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private ItemDimensionMeasureService itemDimensionMeasureService;
	
	@ModelAttribute
	public ItemDimensionMeasure get(@RequestParam(required=false) String id) {
		ItemDimensionMeasure entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = itemDimensionMeasureService.get(id);
		}
		if (entity == null){
			entity = new ItemDimensionMeasure();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:itemDimensionMeasure:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemDimensionMeasure itemDimensionMeasure, String categoryId,String dimensionId, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(itemDimensionMeasure.getCategory() == null) {
			itemDimensionMeasure.setCategory(itemCategoryService.get(categoryId));
		}
		if(itemDimensionMeasure.getDimension() == null) {
			itemDimensionMeasure.setDimension(itemDimensionService.get(dimensionId));
		}
		Page<ItemDimensionMeasure> page = itemDimensionMeasureService.findPage(new Page<ItemDimensionMeasure>(request, response), itemDimensionMeasure); 
		model.addAttribute("page", page);
		model.addAttribute("dimensionId", itemDimensionMeasure.getDimension().getId());
		model.addAttribute("categoryId", itemDimensionMeasure.getCategory().getId());
		return "modules/mod/itemDimensionMeasureList";
	}

	@RequiresPermissions("mod:itemDimensionMeasure:view")
	@RequestMapping(value = "form")
	public String form(ItemDimensionMeasure itemDimensionMeasure, Model model) {
		if(itemDimensionMeasure.getId() == null) {//对于新添加记录需要根据ID补充dimension和category
			itemDimensionMeasure.setCategory(itemCategoryService.get(itemDimensionMeasure.getCategory().getId()));
			itemDimensionMeasure.setDimension(itemDimensionService.get(itemDimensionMeasure.getDimension().getId()));
			itemDimensionMeasure.setName(itemDimensionMeasure.getCategory().getName()+"-"+itemDimensionMeasure.getDimension().getName());
		}
		model.addAttribute("itemDimensionMeasure", itemDimensionMeasure);
		return "modules/mod/itemDimensionMeasureForm";
	}
	
	@RequiresPermissions("mod:itemDimensionMeasure:edit")
	@RequestMapping(value = "save")
	public String save(ItemDimensionMeasure itemDimensionMeasure, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, itemDimensionMeasure)){
			return form(itemDimensionMeasure, model);
		}
		itemDimensionMeasureService.save(itemDimensionMeasure);
//		redirectAttributes.addAttribute("dimensionId", itemDimensionMeasure.getDimension().getId());
//		redirectAttributes.addAttribute("categoryId", itemDimensionMeasure.getCategory().getId());
		addMessage(redirectAttributes, "保存客观评价明细成功");
		return "redirect:"+Global.getAdminPath()+"/mod/itemDimensionMeasure/?dimension.id="+itemDimensionMeasure.getDimension().getId()+"&category.id="+itemDimensionMeasure.getCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:itemDimensionMeasure:edit")
	@RequestMapping(value = "delete")
	public String delete(ItemDimensionMeasure itemDimensionMeasure, RedirectAttributes redirectAttributes) {
//		redirectAttributes.addAttribute("dimensionId", itemDimensionMeasure.getDimension().getId());
//		redirectAttributes.addAttribute("categoryId", itemDimensionMeasure.getCategory().getId());
		itemDimensionMeasureService.delete(itemDimensionMeasure);
		addMessage(redirectAttributes, "删除客观评价明细成功");
		return "redirect:"+Global.getAdminPath()+"/mod/itemDimensionMeasure/?dimension.id="+itemDimensionMeasure.getDimension().getId()+"&category.id="+itemDimensionMeasure.getCategory().getId()+"&repage";
	}
	//*/
	
}