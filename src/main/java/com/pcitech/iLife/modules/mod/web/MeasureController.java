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
import com.pcitech.iLife.modules.mod.entity.Hierarchy;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;

/**
 * 关键属性Controller
 * @author chenci
 * @version 2017-09-22
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/measure")
public class MeasureController extends BaseController {

	@Autowired
	private MeasureService measureService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	
	@ModelAttribute
	public Measure get(@RequestParam(required=false) String id) {
		Measure entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = measureService.get(id);
		}
		if (entity == null){
			entity = new Measure();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:measure:view")
	@RequestMapping(value = {"list", ""})
	public String list(Measure measure,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		measure.setCategory(new ItemCategory(treeId));
		Page<Measure> page = measureService.findPage(new Page<Measure>(request, response), measure); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/measureList";
	}

	@RequiresPermissions("mod:measure:view")
	@RequestMapping(value = "form")
	public String form(Measure measure, Model model) {
		if(measure.getCategory()!=null&&StringUtils.isNoneBlank(measure.getCategory().getId())){
			measure.setCategory(itemCategoryService.get(measure.getCategory().getId()));
		}
		model.addAttribute("measure", measure);
		return "modules/mod/measureForm";
	}

	@RequiresPermissions("mod:measure:edit")
	@RequestMapping(value = "save")
	public String save(Measure measure, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, measure)){
			return form(measure, model);
		}
		measureService.save(measure);
		addMessage(redirectAttributes, "保存商品属性成功");
		return "redirect:"+Global.getAdminPath()+"/mod/measure/?treeId="+measure.getCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:measure:edit")
	@RequestMapping(value = "delete")
	public String delete(Measure measure, RedirectAttributes redirectAttributes) {
		measureService.delete(measure);
		addMessage(redirectAttributes, "删除商品属性成功");
		return "redirect:"+Global.getAdminPath()+"/mod/measure/?treeId="+measure.getCategory().getId()+"&repage";
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
	
	@RequiresPermissions("mod:measure:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/measure");
		model.addAttribute("title","关键属性");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:measure:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/measure");
		model.addAttribute("title","商品类型");
		List<ItemCategory> itemCategoryTree = itemCategoryService.findTree();
		model.addAttribute("list", itemCategoryTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:measure:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个类型。");
		return "treeData/none";
	}
}