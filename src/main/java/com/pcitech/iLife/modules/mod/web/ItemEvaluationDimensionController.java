/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import com.pcitech.iLife.modules.mod.entity.ItemDimensionMeasure;
import com.pcitech.iLife.modules.mod.entity.ItemEvaluationDimension;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionService;
import com.pcitech.iLife.modules.mod.service.ItemEvaluationDimensionService;
import com.pcitech.iLife.modules.mod.service.ItemEvaluationService;

/**
 * 主观评价-维度Controller
 * @author qchzhu
 * @version 2018-12-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/itemEvaluationDimension")
public class ItemEvaluationDimensionController extends BaseController {

	@Autowired
	private ItemEvaluationService itemEvaluationService;
	@Autowired
	private ItemEvaluationDimensionService itemEvaluationDimensionService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	
	@ModelAttribute
	public ItemEvaluationDimension get(@RequestParam(required=false) String id) {
		ItemEvaluationDimension entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = itemEvaluationDimensionService.get(id);
		}
		if (entity == null){
			entity = new ItemEvaluationDimension();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:itemEvaluationDimension:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemEvaluationDimension itemEvaluationDimension, String categoryId,String evaluationId, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(itemEvaluationDimension.getCategory() == null) {
			itemEvaluationDimension.setCategory(itemCategoryService.get(categoryId));
		}
		if(itemEvaluationDimension.getEvaluation() == null) {
			itemEvaluationDimension.setEvaluation(itemEvaluationService.get(evaluationId));
		}
		Page<ItemEvaluationDimension> page = itemEvaluationDimensionService.findPage(new Page<ItemEvaluationDimension>(request, response), itemEvaluationDimension); 
		model.addAttribute("page", page);
		model.addAttribute("evaluationId", itemEvaluationDimension.getEvaluation().getId());
		model.addAttribute("categoryId", itemEvaluationDimension.getCategory().getId());
		return "modules/mod/itemEvaluationDimensionList";
	}

	@RequiresPermissions("mod:itemEvaluationDimension:view")
	@RequestMapping(value = "form")
	public String form(ItemEvaluationDimension itemEvaluationDimension, Model model) {
		if(itemEvaluationDimension.getId() == null) {//对于新添加记录需要根据ID补充dimension和category
			itemEvaluationDimension.setCategory(itemCategoryService.get(itemEvaluationDimension.getCategory().getId()));
			itemEvaluationDimension.setEvaluation(itemEvaluationService.get(itemEvaluationDimension.getEvaluation().getId()));
			itemEvaluationDimension.setName(itemEvaluationDimension.getCategory().getName()+"-"+itemEvaluationDimension.getEvaluation().getName());
		}
		model.addAttribute("itemEvaluationDimension", itemEvaluationDimension);
		return "modules/mod/itemEvaluationDimensionForm";
	}

	@RequiresPermissions("mod:itemEvaluationDimension:edit")
	@RequestMapping(value = "save")
	public String save(ItemEvaluationDimension itemEvaluationDimension, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, itemEvaluationDimension)){
			return form(itemEvaluationDimension, model);
		}
		itemEvaluationDimensionService.save(itemEvaluationDimension);
		addMessage(redirectAttributes, "保存主观评价-维度成功");
		//return "redirect:"+Global.getAdminPath()+"/mod/itemEvaluationDimension/?evaluation.id="+itemEvaluationDimension.getEvaluation().getId()+"&category.id="+itemEvaluationDimension.getCategory().getId()+"&repage";
		//返回维度列表界面
		return "redirect:"+Global.getAdminPath()+"/mod/itemEvaluation/?treeId="+itemEvaluationDimension.getCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:itemEvaluationDimension:edit")
	@RequestMapping(value = "delete")
	public String delete(ItemEvaluationDimension itemEvaluationDimension, RedirectAttributes redirectAttributes) {
		itemEvaluationDimensionService.delete(itemEvaluationDimension);
		addMessage(redirectAttributes, "删除主观评价-维度成功");
		//return "redirect:"+Global.getAdminPath()+"/mod/itemEvaluationDimension/?evaluation.id="+itemEvaluationDimension.getEvaluation().getId()+"&category.id="+itemEvaluationDimension.getCategory().getId()+"&repage";
		//返回维度列表界面
		return "redirect:"+Global.getAdminPath()+"/mod/itemEvaluation/?treeId="+itemEvaluationDimension.getCategory().getId()+"&repage";
	}

}