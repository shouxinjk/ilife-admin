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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.ope.entity.HumanMarkedValue;
import com.pcitech.iLife.modules.ope.service.HumanMarkedValueService;
import com.pcitech.iLife.modules.ope.service.PerformanceService;
import com.pcitech.iLife.util.Util;

/**
 * 数据标注Controller
 * @author chenci
 * @version 2017-09-28
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/humanMarkedValue")
public class HumanMarkedValueController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(HumanMarkedValueController.class);
	@Autowired
	private HumanMarkedValueService humanMarkedValueService;
	@Autowired
	private MeasureService measureService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private PerformanceService performanceService;
	
	@ModelAttribute
	public HumanMarkedValue get(@RequestParam(required=false) String id) {
		HumanMarkedValue entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = humanMarkedValueService.get(id);
		}
		if (entity == null){
			entity = new HumanMarkedValue();
		}
		return entity;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/value", method = RequestMethod.POST)
	//增加或修改已有标注记录，字段包括measureId、personId、originalValue、value
	public Map<String,String> updateValuesByMeasureId( @RequestBody Map<String,Object> params, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,String> result = Maps.newHashMap();
		if(params.get("measureId")==null || params.get("personId")==null || params.get("originalValue")==null || params.get("value")==null) {
			result.put("result", "error");
			result.put("msg", "Both measureId/personId/originalValue/Value are required.");
			return result;
		}
		String id = Util.md5(""+params.get("measureId")+params.get("personId")+params.get("originalValue"));//构建唯一ID
		params.put("id", id);
		try {
			humanMarkedValueService.upsertMarkedValue(params);
		}catch(Exception ex) {
			result.put("result", "error.");
			result.put("msg", ex.getMessage());
			return result;
		}
		result.put("result", "succeed.");
		result.put("msg", "marked value updated.");
		return result;
	}
	
	@RequiresPermissions("ope:humanMarkedValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(HumanMarkedValue humanMarkedValue,String treeId,String treeModule,String topType,  HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeModule.equals("measure")){
			humanMarkedValue.setMeasure(new Measure(treeId));
		}else{//否则提示选择属性
			model.addAttribute("message","选择属性查看标注。");
			return "treeData/none";
		}
		Page<HumanMarkedValue> page = humanMarkedValueService.findPage(new Page<HumanMarkedValue>(request, response), humanMarkedValue); 
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/ope/humanMarkedValueList";
	}

	@RequiresPermissions("ope:humanMarkedValue:view")
	@RequestMapping(value = "form")
	public String form(HumanMarkedValue humanMarkedValue, String pid,String pType, Model model) {
		Measure measure=new Measure();
		if(pType.equals("measure")){
			measure = measureService.get(pid);
			humanMarkedValue.setMeasure(measure);
		}else {//否则提示选择属性
			model.addAttribute("message","选择属性查看标注。");
			return "treeData/none";
		}
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("humanMarkedValue", humanMarkedValue);
		return "modules/ope/humanMarkedValueForm";
	}

	@RequiresPermissions("ope:humanMarkedValue:edit")
	@RequestMapping(value = "save")
	public String save(HumanMarkedValue humanMarkedValue,String pid,String pType,  Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, humanMarkedValue)){
			return form(humanMarkedValue,pid,pType, model);
		}
		if(humanMarkedValue.getMeasure() == null){//不知道为啥，前端传进来的measure信息丢失了，手动补一次
			humanMarkedValue.setMeasure(measureService.get(pid));
		}
		try {//注意：一个用户仅能建立一条标注数据，这里有唯一性校验问题
			humanMarkedValueService.save(humanMarkedValue);
			addMessage(redirectAttributes, "保存数据标注成功");
		}catch(Exception ex) {
			logger.error("failed to save human marked value.",ex);
			addMessage(redirectAttributes, "保存标注失败。请确认一个用户对一个属性仅能添加一条记录。");
		}
		return "redirect:"+Global.getAdminPath()+"/ope/humanMarkedValue/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("ope:humanMarkedValue:edit")
	@RequestMapping(value = "delete")
	public String delete(HumanMarkedValue humanMarkedValue,String pid,String pType, RedirectAttributes redirectAttributes) {
		humanMarkedValueService.delete(humanMarkedValue);
		addMessage(redirectAttributes, "删除数据标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/humanMarkedValue/?treeId="+pid+"&treeModule="+pType+"&repage";
	}

	///////////tree data//////////////

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
	
	@RequiresPermissions("ope:humanMarkedValue:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","ope/humanMarkedValue");
		model.addAttribute("title","关键属性");
		return "treeData/index";
	}
	
	@RequiresPermissions("ope:humanMarkedValue:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","ope/humanMarkedValue");
		model.addAttribute("title","属性及分类");
		List<ItemCategory> itemCategoryTree = itemCategoryService.findTree();
		model.addAttribute("list", performanceService.getTree(itemCategoryTree));
		return "treeData/tree";
	}
	
	@RequiresPermissions("ope:humanMarkedValue:view")
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
				//查询该类别下的属性
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