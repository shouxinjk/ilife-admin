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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.entity.ItemEvaluation;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.ItemEvaluationService;

/**
 * 主观评价Controller
 * @author qchzhu
 * @version 2018-12-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/itemEvaluation")
public class ItemEvaluationController extends BaseController {

	@Autowired
	private ItemEvaluationService itemEvaluationService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	
	@ModelAttribute
	public ItemEvaluation get(@RequestParam(required=false) String id) {
		ItemEvaluation entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = itemEvaluationService.get(id);
		}
		if (entity == null){
			entity = new ItemEvaluation();
		}
		return entity;
	}
	
	/*
	@RequiresPermissions("mod:itemEvaluation:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemEvaluation itemEvaluation, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<ItemEvaluation> list = itemEvaluationService.findList(itemEvaluation); 
		model.addAttribute("list", list);
		return "modules/mod/itemEvaluationList";
	}
	//*/
	@RequiresPermissions("mod:itemEvaluation:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemEvaluation itemEvaluation,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<ItemEvaluation> list = Lists.newArrayList();
		//检查顶级dimension节点是否存在，如果不存在则创建
		ItemCategory category = itemCategoryService.get(treeId);
		ItemEvaluation root = itemEvaluationService.get("1");
		ItemEvaluation q = new ItemEvaluation(); 
		q.setCategory(category);
		q.setParent(root);
		if(itemEvaluationService.findList(q).size()==0) {//查询当前选中category下是否已经建立主观评价指标。category为当前选中ID，并且其父节点为0
			//如果未建立则建立默认评价指标ROOT节点
			ItemEvaluation rootDimension = new ItemEvaluation();
			rootDimension.setParent(root);
			rootDimension.setName(category.getName());
			rootDimension.setCategory(category);
			rootDimension.setWeight("100");
			itemEvaluationService.save(rootDimension);
			//在ROOT下建立5个默认节点，可以通过字典配置
			String[] nodes = {"生存需求","安全需求","情感需求","尊重需求","价值实现"};
			root = itemEvaluationService.findList(q).get(0);
			int i=10;
			for(String node:nodes){
				ItemEvaluation evalNode = new ItemEvaluation();
				evalNode.setParent(root);
				evalNode.setName(node);
				evalNode.setSort(i);
				evalNode.setCategory(category);
				evalNode.setWeight(Double.toString(100/nodes.length));
				itemEvaluationService.save(evalNode);
				i += 10;
			}
		}
		List<ItemEvaluation> sourcelist = itemEvaluationService.findTree(category);
		ItemEvaluation.sortList(list, sourcelist, "1",true);
		model.addAttribute("list", list);
		model.addAttribute("treeId", treeId);
		return "modules/mod/itemEvaluationList";
	}
	
	@RequiresPermissions("mod:itemEvaluation:view")
	@RequestMapping(value = "form")
	public String form(ItemEvaluation itemEvaluation, Model model) {
        if (itemEvaluation.getParent()==null||itemEvaluation.getParent().getId()==null){
	    		ItemCategory category = itemCategoryService.get(itemEvaluation.getCategory().getId());
	    		ItemEvaluation rootParentEvaluation = itemEvaluationService.get("1");
	    		ItemEvaluation q = new ItemEvaluation(); 
	    		q.setCategory(category);
	    		q.setParent(rootParentEvaluation);
        		List<ItemEvaluation> rootDimensions = itemEvaluationService.findList(q);
        		ItemEvaluation root = rootDimensions.get(0);
        		itemEvaluation.setParent(root);
        }
		if (itemEvaluation.getParent()!=null && StringUtils.isNotBlank(itemEvaluation.getParent().getId())){
			itemEvaluation.setParent(itemEvaluationService.get(itemEvaluation.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(itemEvaluation.getId())){
				ItemEvaluation itemEvaluationChild = new ItemEvaluation();
				itemEvaluationChild.setParent(new ItemEvaluation(itemEvaluation.getParent().getId()));
				List<ItemEvaluation> list = itemEvaluationService.findList(itemEvaluation); 
				if (list.size() > 0){
					itemEvaluation.setSort(list.get(list.size()-1).getSort());
					if (itemEvaluation.getSort() != null){
						itemEvaluation.setSort(itemEvaluation.getSort() + 30);
					}
				}
			}
		}
		if (itemEvaluation.getSort() == null){
			itemEvaluation.setSort(30);
		}
		model.addAttribute("itemEvaluation", itemEvaluation);
		return "modules/mod/itemEvaluationForm";
	}

	@RequiresPermissions("mod:itemEvaluation:edit")
	@RequestMapping(value = "save")
	public String save(ItemEvaluation itemEvaluation, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, itemEvaluation)){
			return form(itemEvaluation, model);
		}
		itemEvaluationService.save(itemEvaluation);
		addMessage(redirectAttributes, "保存主观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/itemEvaluation/?treeId="+itemEvaluation.getCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:itemEvaluation:edit")
	@RequestMapping(value = "delete")
	public String delete(ItemEvaluation itemEvaluation, RedirectAttributes redirectAttributes) {
		itemEvaluationService.delete(itemEvaluation);
		addMessage(redirectAttributes, "删除主观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/itemEvaluation/?treeId="+itemEvaluation.getCategory().getId()+"&repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ItemEvaluation> list = itemEvaluationService.findList(new ItemEvaluation());
		for (int i=0; i<list.size(); i++){
			ItemEvaluation e = list.get(i);
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
	
	@RequiresPermissions("mod:itemEvaluation:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/itemEvaluation");
		model.addAttribute("title","主观评价维度");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:itemEvaluation:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/itemEvaluation");
		model.addAttribute("title","商品类型");
		List<ItemCategory> itemCategoryTree = itemCategoryService.findTree();
		model.addAttribute("list", itemCategoryTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:itemEvaluation:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个类别。");
		return "treeData/none";
	}
	
}