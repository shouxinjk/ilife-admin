/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.CategoryNeed;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.entity.ItemDimensionMeasure;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionMeasureService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.util.Util;

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
	@Autowired
	private MeasureService measureService;
	
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


	/**
	 * 显示所有待添加属性，包括当前节点属性，以及可继承属性
	 * 
	 * 查询所有Measure并排除已经添加的
	 */
	@RequiresPermissions("mod:itemDimensionMeasure:edit")
	@RequestMapping(value = {"list2"})
	public String listPendingMeasures(String dimensionId, String categoryId,HttpServletRequest request, HttpServletResponse response, Model model) {
		//根据Category递归查询获取属性
		ItemCategory category = itemCategoryService.get(categoryId);
		List<Measure> pendingMeasures = Lists.newArrayList();
		
		//查询已经关联的属性，根据dimension查询获取
		ItemDimension itemDimension = itemDimensionService.get(dimensionId);//重新加载得到全部信息
		ItemDimensionMeasure query = new ItemDimensionMeasure();
		query.setDimension(itemDimension);
		List<ItemDimensionMeasure> existItemDimensionMeasures = itemDimensionMeasureService.findList(query);
		List<String> ids = Lists.newArrayList();//记录已经添加的itemDimensionMeasure ID列表
		for(ItemDimensionMeasure item:existItemDimensionMeasures) {
			ids.add(item.getMeasure().getId());
		}
		
		//递归查询获取属性
		//遍历获取所有节点的属性
		while(category!=null) {
			List<Measure> measures =measureService.findByCategory(category.getId());
			for (Measure item:measures){
				if(ids.indexOf(item.getId())<0)
					pendingMeasures.add(item);
			}
			category = itemCategoryService.get(category.getParent());//逐层获取继承属性
		}
				
		model.addAttribute("measures", pendingMeasures);
		model.addAttribute("dimensionId", dimensionId);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("treeId", categoryId);
		return "modules/mod/itemDimensionMeasureList2";
	}
	
	/**
	 * 批量保存Measure到Dimension上
	 * @param  {dimensionId:xxx, measures:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("mod:itemDimensionMeasure:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddMeasures(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String dimensionId = json.getString("dimensionId");
		JSONArray measureIds = json.getJSONArray("measureIds");
		logger.debug("got params.[dimensionId]"+dimensionId+" [measureIds]"+measureIds);
		for(int i=0;i<measureIds.size();i++) {
			String measureId = measureIds.getString(i);
			ItemDimensionMeasure dimensionMeasure = new ItemDimensionMeasure();
			ItemDimension dimension = itemDimensionService.get(dimensionId);
			Measure measure = measureService.get(measureId);
			dimensionMeasure.setName(dimension.getName()+"-"+measure.getName());
			dimensionMeasure.setDimension(dimension);
			dimensionMeasure.setMeasure(measure);
			dimensionMeasure.setCategory(dimension.getCategory());
			dimensionMeasure.setWeight(0);//默认占比为0，需要到评价首页调整
			dimensionMeasure.setCreateDate(new Date());
			dimensionMeasure.setUpdateDate(new Date());
			dimensionMeasure.setDescription("");
			try {
				itemDimensionMeasureService.save(dimensionMeasure);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("add need failed.[dimensionId]"+dimensionId+" [measureId]"+measureId);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("dimensionId", dimensionId);
		return result;
	}
	
	
	/**
	 * 显示所有待添加属性，包括当前节点属性，以及可继承属性
	 * 供移动端显示所有可添加属性列表
	 */
	@ResponseBody
	@RequestMapping(value = {"rest/pending-measures/{categoryId}/{dimensionId}"}, method = RequestMethod.GET)
	public JSONObject listPendingMeasures(@PathVariable String categoryId, @PathVariable String dimensionId) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		
		//根据Category递归查询获取属性
		ItemCategory category = itemCategoryService.get(categoryId);
		List<Measure> pendingMeasures = Lists.newArrayList();
		
		//查询已经关联的属性，根据dimension查询获取
		ItemDimension itemDimension = itemDimensionService.get(dimensionId);//重新加载得到全部信息
		ItemDimensionMeasure query = new ItemDimensionMeasure();
		query.setDimension(itemDimension);
		List<ItemDimensionMeasure> existItemDimensionMeasures = itemDimensionMeasureService.findList(query);
		result.put("addedMeasures", existItemDimensionMeasures);//已经添加的关键属性列表
		
		List<String> ids = Lists.newArrayList();//记录已经添加的itemDimensionMeasure ID列表
		for(ItemDimensionMeasure item:existItemDimensionMeasures) {
			ids.add(item.getMeasure().getId());
		}
		
		//递归查询获取属性
		//遍历获取所有节点的属性
		while(category!=null) {
			List<Measure> measures =measureService.findByCategory(category.getId());
			for (Measure item:measures){
				if(ids.indexOf(item.getId())<0)
					pendingMeasures.add(item);
			}
			category = itemCategoryService.get(category.getParent());//逐层获取继承属性
		}
		result.put("pendingMeasures", pendingMeasures);//可添加列表
				
		result.put("success", true);
		return result;
	}
	
	/**
	 * 从移动端直接删除
	 * 需要同时更新其他节点weight
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/dimension-measure/{id}", method = RequestMethod.DELETE)
	public JSONObject delete(@PathVariable String id) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		
		ItemDimensionMeasure itemDimensionMeasure = itemDimensionMeasureService.get(id);
		if( itemDimensionMeasure == null) {
			result.put("mgs", "无指定ID记录");
			return result;
		}
		
		result.put("data", itemDimensionMeasure);
		itemDimensionMeasureService.delete(itemDimensionMeasure);
		
		//自动重新计算其他节点权重：等比例放大
		double ratio = 1;
		if(itemDimensionMeasure.getWeight()<100 && itemDimensionMeasure.getWeight()>0)
			ratio = 100/(100-itemDimensionMeasure.getWeight());
		
		ItemDimensionMeasure q = new ItemDimensionMeasure();
		q.setDimension(itemDimensionMeasure.getDimension());
		List<ItemDimensionMeasure> nodes = itemDimensionMeasureService.findList(q);//查找所有属性节点
		
		ItemDimension q2 = new ItemDimension();
		q2.setParent(itemDimensionMeasure.getDimension());
		List<ItemDimension> dimensions = itemDimensionService.findList(q2);//查找所有指标节点
		
		//更新属性节点weight
		for(ItemDimensionMeasure node:nodes) {
			node.setWeight(node.getWeight()*ratio);
			node.setUpdateDate(new Date());
			itemDimensionMeasureService.save(node);
		}
		//更新指标节点weight
		for(ItemDimension dimension:dimensions) {
			dimension.setWeight(ratio*dimension.getWeight());
			dimension.setUpdateDate(new Date());
			itemDimensionService.save(dimension);
		}
		
		result.put("success", true);
		
		return result;
	}
	
	/**
	 * 从移动端直接新增或修改
	 * @param itemDimensionMeasure
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/dimension-measure", method = RequestMethod.POST)
	public JSONObject upsert(ItemDimensionMeasure itemDimensionMeasure) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(itemDimensionMeasure.getId()==null || itemDimensionMeasure.getId().trim().length()==0) {//新增需要设置ID
			itemDimensionMeasure.setId(Util.get32UUID());
			itemDimensionMeasure.setIsNewRecord(true);
			itemDimensionMeasure.setCreateDate(new Date());
		}
		
		itemDimensionMeasureService.save(itemDimensionMeasure);
		result.put("data", itemDimensionMeasure);
		
		//重新计算已有节点权重：等比例缩放
		double ratio = 1;
		if(itemDimensionMeasure.getWeight()<100 && itemDimensionMeasure.getWeight()>0)
			ratio = (100-itemDimensionMeasure.getWeight())/100;
		
		ItemDimensionMeasure q = new ItemDimensionMeasure();
		q.setDimension(itemDimensionMeasure.getDimension());
		List<ItemDimensionMeasure> nodes = itemDimensionMeasureService.findList(q);//查找所有属性节点
		
		ItemDimension q2 = new ItemDimension();
		q2.setParent(itemDimensionMeasure.getDimension());
		List<ItemDimension> dimensions = itemDimensionService.findList(q2);
		
		double total = 0;
		for(ItemDimensionMeasure node:nodes) {
			total += node.getWeight();
		}
		for(ItemDimension dimension:dimensions) {
			total += dimension.getWeight();
		}
		if(total==0) {
			result.put("msg", "re-calculate weight error due to total is 0.");
			result.put("success", true);
			return result;
		}
		
		for(ItemDimensionMeasure node:nodes) {
			node.setWeight(ratio*node.getWeight()/total);
			node.setUpdateDate(new Date());
			itemDimensionMeasureService.save(node);
		}
		for(ItemDimension dimension:dimensions) {
			dimension.setWeight(ratio*dimension.getWeight()/total);
			dimension.setUpdateDate(new Date());
			itemDimensionService.save(dimension);
		}
		result.put("success", true);
		return result;
	}
	
	@RequiresPermissions("mod:itemDimensionMeasure:view")
	@RequestMapping(value = "form")
	public String form(ItemDimensionMeasure itemDimensionMeasure, Model model) {
		if(itemDimensionMeasure.getId() == null) {//对于新添加记录需要根据ID补充dimension和category
			itemDimensionMeasure.setCategory(itemCategoryService.get(itemDimensionMeasure.getCategory().getId()));
			if(itemDimensionMeasure.getDimension()!=null && itemDimensionMeasure.getDimension().getId()!=null)//从标签页开始添加不带有dimension信息，只有从维度操作列添加带有该信息
				itemDimensionMeasure.setDimension(itemDimensionService.get(itemDimensionMeasure.getDimension().getId()));
			if(itemDimensionMeasure.getDimension()!=null && itemDimensionMeasure.getDimension().getId()!=null)//从标签页开始添加不带有dimension信息，只有从维度操作列添加带有该信息
				itemDimensionMeasure.setName(itemDimensionMeasure.getCategory().getName()+"-"+itemDimensionMeasure.getDimension().getName());
			else
				itemDimensionMeasure.setName(itemDimensionMeasure.getCategory().getName());
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
//		return "redirect:"+Global.getAdminPath()+"/mod/itemDimensionMeasure/?dimension.id="+itemDimensionMeasure.getDimension().getId()+"&category.id="+itemDimensionMeasure.getCategory().getId()+"&repage";
		//返回维度列表界面
		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?treeId="+itemDimensionMeasure.getCategory().getId()+"&repage";

	}
	
	@RequiresPermissions("mod:itemDimensionMeasure:edit")
	@RequestMapping(value = "delete")
	public String delete(ItemDimensionMeasure itemDimensionMeasure, RedirectAttributes redirectAttributes) {
//		redirectAttributes.addAttribute("dimensionId", itemDimensionMeasure.getDimension().getId());
//		redirectAttributes.addAttribute("categoryId", itemDimensionMeasure.getCategory().getId());
		itemDimensionMeasureService.delete(itemDimensionMeasure);
		addMessage(redirectAttributes, "删除客观评价明细成功");
//		return "redirect:"+Global.getAdminPath()+"/mod/itemDimensionMeasure/?dimension.id="+itemDimensionMeasure.getDimension().getId()+"&category.id="+itemDimensionMeasure.getCategory().getId()+"&repage";
		//返回维度列表界面
		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?treeId="+itemDimensionMeasure.getCategory().getId()+"&repage";
	}
	//*/
	
}