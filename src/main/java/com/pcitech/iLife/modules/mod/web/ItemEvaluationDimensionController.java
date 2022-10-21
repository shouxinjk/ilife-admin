/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adobe.xmp.impl.Utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.entity.ItemDimensionMeasure;
import com.pcitech.iLife.modules.mod.entity.ItemEvaluation;
import com.pcitech.iLife.modules.mod.entity.ItemEvaluationDimension;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionService;
import com.pcitech.iLife.modules.mod.service.ItemEvaluationDimensionService;
import com.pcitech.iLife.modules.mod.service.ItemEvaluationService;
import com.pcitech.iLife.util.Util;

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
	@Autowired
	private ItemDimensionService itemDimensionService;
	
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
	
	/**
	 * 显示待添加客观评价维度
	 * @param itemEvaluationDimension
	 * @param categoryId 类目，仅根据类目添加客观维度
	 * @param evaluationId 当前主观评价
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("mod:itemEvaluationDimension:view")
	@RequestMapping(value = {"list2"})
	public String listPendingDimensions(ItemEvaluationDimension itemEvaluationDimension, String categoryId,String evaluationId, HttpServletRequest request, HttpServletResponse response, Model model) {
		//根据Category递归查询获取属性
		ItemCategory category = itemCategoryService.get(categoryId);
		List<ItemDimension> pendingDimensions = Lists.newArrayList();
		
		//查询已经关联的客观评价维度
		ItemEvaluation itemEvaluation = itemEvaluationService.get(evaluationId);//重新加载得到全部信息
		ItemEvaluationDimension query = new ItemEvaluationDimension();
		query.setEvaluation(itemEvaluation);
		List<ItemEvaluationDimension> existItemEvaluationDimension = itemEvaluationDimensionService.findList(query);
		List<String> ids = Lists.newArrayList();//记录已经添加的itemDimensionMeasure ID列表
		for(ItemEvaluationDimension item:existItemEvaluationDimension) {
			ids.add(item.getDimension().getId());
		}
		
		//获取该类目下所有客观评价节点
		if(category!=null) {
			ItemDimension q = new ItemDimension();
			q.setCategory(category);
			List<ItemDimension> dimensions =itemDimensionService.findList(q);
			for (ItemDimension item:dimensions){
				if(ids.indexOf(item.getId())<0)
					pendingDimensions.add(item);
			}
		}
				
		model.addAttribute("dimensions", pendingDimensions);
		model.addAttribute("evaluationId", itemEvaluationDimension.getEvaluation().getId());
		model.addAttribute("categoryId", itemEvaluationDimension.getCategory().getId());
		return "modules/mod/itemEvaluationDimensionList2";
	}
	

	/**
	 * 批量保存dimension到evaluation上
	 * @param  {evaluationId:xxx, dimensions:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("mod:itemEvaluationDimension:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddDimensions(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String evaluationId = json.getString("evaluationId");
		JSONArray dimensionIds = json.getJSONArray("dimensionIds");
		logger.debug("got params.[evaluationId]"+evaluationId+" [dimensionIds]"+dimensionIds);
		for(int i=0;i<dimensionIds.size();i++) {
			String dimensionId = dimensionIds.getString(i);
			ItemEvaluationDimension evaluationDimension = new ItemEvaluationDimension();
			ItemEvaluation evaluation = itemEvaluationService.get(evaluationId);
			ItemDimension dimension = itemDimensionService.get(dimensionId);
			evaluationDimension.setName(evaluation.getName()+"-"+dimension.getName());
			evaluationDimension.setEvaluation(evaluation);
			evaluationDimension.setDimension(dimension);
			evaluationDimension.setCategory(dimension.getCategory());
			evaluationDimension.setWeight(0);//默认占比为0，需要到评价首页调整
			evaluationDimension.setCreateDate(new Date());
			evaluationDimension.setUpdateDate(new Date());
			evaluationDimension.setDescription("");
			//evaluation和dimension唯一
			evaluationDimension.setId(Util.md5(evaluationId+dimension.getId()));
			evaluationDimension.setIsNewRecord(true);
			try {
				itemEvaluationDimensionService.save(evaluationDimension);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("add need failed.[evaluationId]"+evaluationId+" [dimensionIds]"+dimensionIds);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("evaluationId", evaluationId);
		return result;
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