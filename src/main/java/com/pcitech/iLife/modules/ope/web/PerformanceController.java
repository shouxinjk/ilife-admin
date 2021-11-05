/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.web;

import java.util.Date;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.ope.service.PerformanceService;

/**
 * 标注Controller
 * @author chenci
 * @version 2017-09-28
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/performance")
public class PerformanceController extends BaseController {
	@Autowired
	private MeasureService measureService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private PerformanceService performanceService;
	
	/**
	@ResponseBody
	@RequestMapping(value = "rest/byMeasureId")
	//根据属性ID（系统标准属性ID）查询所有属性值，并进行标注。根据level等级、markedValue倒序排列
	public List<Performance> listCategoryByParentId( @RequestParam(required=true) String measureId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		return performanceService.findListByMeasureId(measureId);
	}
		//**/
	
	@ResponseBody
	@RequestMapping(value = "rest/values/{measureId}/{categoryId}", method = RequestMethod.GET)
	//根据属性ID（系统标准属性ID）查询所有属性值，并进行标注。根据level等级、controlvalue倒序排列
	public List<Performance> listValuesByMeasureIdAndCategoryId( @PathVariable String measureId,@PathVariable String categoryId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		//return performanceService.findListByMeasureId(measureId);
		Map<String,String> params = Maps.newHashMap();
		params.put("categoryId", categoryId);
		params.put("measureId", measureId);
		return performanceService.findListByMeasureAndCategory(params);
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/values", method = RequestMethod.GET)
	//根据measureId、categoryId查找在指定类目指定属性下的标注值。根据level等级、controlvalue倒序排列
	//参数：measureId,categoryId
	public List<Performance> listValuesByMeasureAndCategory( @RequestBody Map<String,String> params, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		return performanceService.findListByMeasureAndCategory(params);
	}
	
	//不工作。jsGrid单个记录更新时传递的是FormData
	/**
	@ResponseBody
	@RequestMapping(value = "rest/propvalue", method = RequestMethod.POST)
	//根据属性值ID更新指定数据值条目，传递参数包括id,level,controlValue
	public Map<String,String> updateValuesByMeasureId( @RequestBody Map<String,Object> params, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		params.put("updateDate", new Date());
		performanceService.updateControlValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "control value updated.");
		return result;
	}
	//**/
	@ResponseBody
	@RequestMapping(value = "rest/propvalue", method = RequestMethod.POST)
	//根据属性值ID更新指定数据值条目，传递参数包括id,level,controlValue
	public Performance updateValuesByMeasureId( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double controlValue, 
			@RequestParam(required=true) int level, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("level", level);
		params.put("controlValue", controlValue);
		params.put("updateDate", new Date());
		performanceService.updateControlValue(params);
		return performanceService.get(id);
//		Map<String,String> result = Maps.newHashMap();
//		result.put("result", "control value updated.");
//		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/updateMarkedValue")
	//更新属性值标注，包括level、markedvalue。自动添加更新日期
	public Map<String,String> updateMarkedValue( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double markedValue, 
			@RequestParam(required=true) int level, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("level", level);
		params.put("markedValue", markedValue);
		params.put("updateDate", new Date());
		performanceService.updateMarkedValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "marked value updated.");
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "rest/updateControlValue")
	//更新属性值标注，包括level、controlvalue。自动添加更新日期
	public Map<String,String> updateControlValue( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double controlValue, 
			@RequestParam(required=true) int level, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("level", level);
		params.put("controlValue", controlValue);
		params.put("updateDate", new Date());
		performanceService.updateControlValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "control value updated.");
		return result;
	}
	
	@ModelAttribute
	public Performance get(@RequestParam(required=false) String id) {
		Performance entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = performanceService.get(id);
		}
		if (entity == null){
			entity = new Performance();
		}
		return entity;
	}
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = {"list", ""})
	public String list(Performance performance,String treeId,String pId,String treeModule,String topType,  HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeModule.equals("measure")){
			performance.setMeasure(new Measure(treeId));
			performance.setCategory(new ItemCategory(pId));//增加类目过滤。注意：仅在选中节点为Measure时，其pId是CategoryId
		}else{//否则提示选择属性
			model.addAttribute("message","选择属性查看标注。");
			return "treeData/none";
		}
		Page<Performance> page = performanceService.findPage(new Page<Performance>(request, response), performance);
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);//记录当前选中的measureId
		model.addAttribute("pId", pId);//记录当前选中的categoryId
		model.addAttribute("pType", treeModule);
		return "modules/ope/performanceList";
	}

	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = "form")
	public String form(Performance performance,String treeId,String pId,String pType,  Model model) {
		Measure measure=new Measure();
		if(pType.equals("measure")){
			measure = measureService.get(treeId);//treeId记录当前选中的measureId
			performance.setMeasure(measure);
			performance.setCategory(itemCategoryService.get(pId));//pId记录当前选中的categoryId
			//model.addAttribute("categoryId", measure.getCategory().getId());//设置当前类目ID
		}else {//否则提示选择属性
			model.addAttribute("message","选择属性查看标注。");
			return "treeData/none";
		}
		model.addAttribute("treeId", treeId);//记录当前选中的measureId
		model.addAttribute("pId", pId);//记录当前选中的categoryId
		model.addAttribute("pType", pType);
		model.addAttribute("performance", performance);
		return "modules/ope/performanceForm";
	}

	@RequiresPermissions("ope:performance:edit")
	@RequestMapping(value = "save")
	public String save(Performance performance,String treeId,String pId,String pType,  Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, performance)){
			return form(performance,treeId,pId,pType,model);
		}
		if(performance.getMeasure() == null){//不知道为啥，前端传进来的measure信息丢失了，手动补一次
			performance.setMeasure(measureService.get(treeId));
		}
		if(performance.getCategory() == null){//不知道为啥，前端传进来的measure信息丢失了，手动补一次
			performance.setCategory(itemCategoryService.get(pId));
		}
		performanceService.save(performance);
		addMessage(redirectAttributes, "保存标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/performance/?treeId="+treeId+"&pId="+pId+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("ope:performance:edit")
	@RequestMapping(value = "delete")
	public String delete(Performance performance,String treeId,String pId,String pType,  RedirectAttributes redirectAttributes) {
		performanceService.delete(performance);
		addMessage(redirectAttributes, "删除标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/performance/?treeId="+treeId+"&pId="+pId+"&treeModule="+pType+"&repage";
	}
	

	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Measure measure, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Measure> list =measureService.findList(measure);
		for (int i=0; i<list.size(); i++){
			Measure e = list.get(i);
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
	
	@ResponseBody
	@RequestMapping(value = "performances")
	public List<Map<String, Object>> listMeasureByCategory(@RequestParam(required=true) String category,HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Measure> list =measureService.findByCategory(category);
		for (int i=0; i<list.size(); i++){
			Measure e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("category", e.getCategory().getId());
			map.put("name", e.getName());
			map.put("property", e.getProperty());
			mapList.add(map);
			
		}
		return mapList;
	}
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","ope/performance");
		model.addAttribute("title","关键属性");
		return "treeData/index";
	}
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","ope/performance");
		model.addAttribute("title","属性及分类");
		List<ItemCategory> itemCategoryTree = itemCategoryService.findTree();
		model.addAttribute("list", performanceService.getTree(itemCategoryTree));
		return "treeData/tree";
	}
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请选择属性节点。");
		return "treeData/none";
	}
	

	/**
	 * 查询属性分类及属性。返回树结构，其中属性作为叶子节点。
	 * @param extId
	 * @param response
	 * @return
	 */
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeDataWithLeaf(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ItemCategory> categories = itemCategoryService.findList(new ItemCategory());
		for (int i=0; i<categories.size(); i++){
			ItemCategory e = categories.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
				//查询该类别下的直接属性：属性定义及属性值均在当前类目下
				Measure query = new Measure();
				query.setCategory(e);
				List<Measure> props = measureService.findList(query);
				for(Measure prop:props) {
					Map<String, Object> leafNode = Maps.newHashMap();
					leafNode.put("id", prop.getId());
					leafNode.put("pId", e.getId());
					leafNode.put("name", prop.getName());
					mapList.add(leafNode);
				}
			}
		}
		return mapList;
	}
	
}