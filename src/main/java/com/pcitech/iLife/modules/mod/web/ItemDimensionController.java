/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pcitech.iLife.modules.mod.entity.ItemCategory;
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
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionService;

/**
 * 商品维度Controller
 * @author chenci
 * @version 2018-06-22
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/itemDimension")
public class ItemDimensionController extends BaseController {

	@Autowired
	private ItemDimensionService itemDimensionService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	
	@ModelAttribute
	public ItemDimension get(@RequestParam(required=false) String id) {
		ItemDimension entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = itemDimensionService.get(id);
		}
		if (entity == null){
			entity = new ItemDimension();
		}
		return entity;
	}
	/**
	@RequiresPermissions("mod:itemDimension:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemDimension itemDimension, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<ItemDimension> list = Lists.newArrayList();
		List<ItemDimension> sourcelist = itemDimensionService.findTree();
		ItemDimension.sortList(list, sourcelist, "1",true);
		model.addAttribute("list", list);
		return "modules/mod/itemDimensionList";
	}
	//**/
	
	@RequiresPermissions("mod:itemDimension:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemDimension itemDimension,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<ItemDimension> list = Lists.newArrayList();
		//检查顶级dimension节点是否存在，如果不存在则创建
		ItemCategory category = itemCategoryService.get(treeId);
		ItemDimension rootParentDimension = itemDimensionService.get("1");
		ItemDimension q = new ItemDimension(); 
		q.setCategory(category);
		q.setParent(rootParentDimension);
		if(itemDimensionService.findList(q).size()==0) {//查询category为当前选中ID，并且其父节点为0
			ItemDimension rootDimension = new ItemDimension();
			rootDimension.setParent(rootParentDimension);
			rootDimension.setName(category.getName());
			rootDimension.setDescription("root dimension");
			rootDimension.setCategory(category);
			rootDimension.setWeight("100");
			itemDimensionService.save(rootDimension);
		}
		List<ItemDimension> sourcelist = itemDimensionService.findTree(category);
		ItemDimension.sortList(list, sourcelist, "1",true);
		model.addAttribute("list", list);
		model.addAttribute("treeId", treeId);
		return "modules/mod/itemDimensionList";
	}

	@RequiresPermissions("mod:itemDimension:view")
	@RequestMapping(value = "form")
	public String form(ItemDimension itemDimension, Model model) {
        if (itemDimension.getParent()==null||itemDimension.getParent().getId()==null){
	    		ItemCategory category = itemCategoryService.get(itemDimension.getCategory().getId());
	    		ItemDimension rootParentDimension = itemDimensionService.get("1");
	    		ItemDimension q = new ItemDimension(); 
	    		q.setCategory(category);
	    		q.setParent(rootParentDimension);
        		List<ItemDimension> rootDimensions = itemDimensionService.findList(q);
        		ItemDimension root = rootDimensions.get(0);
            itemDimension.setParent(root);
        }
		if (itemDimension.getParent()!=null && StringUtils.isNotBlank(itemDimension.getParent().getId())){
			itemDimension.setParent(itemDimensionService.get(itemDimension.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(itemDimension.getId())){
				ItemDimension itemDimensionChild = new ItemDimension();
				itemDimensionChild.setParent(new ItemDimension(itemDimension.getParent().getId()));
				itemDimensionChild.setCategory(itemDimension.getCategory());
				List<ItemDimension> list = itemDimensionService.findList(itemDimension); 
				if (list.size() > 0){
					itemDimension.setSort(list.get(list.size()-1).getSort());
					if (itemDimension.getSort() != null){
						itemDimension.setSort(itemDimension.getSort() + 30);
					}
				}
			}
		}
		if (itemDimension.getSort() == null){
			itemDimension.setSort(30);
		}
		model.addAttribute("itemDimension", itemDimension);
		return "modules/mod/itemDimensionForm";
	}

	@RequiresPermissions("mod:itemDimension:edit")
	@RequestMapping(value = "save")
	public String save(ItemDimension itemDimension, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, itemDimension)){
			return form(itemDimension, model);
		}
		//itemDimension.setCategory(itemDimension.getParent().getCategory());//使用父目录的分类
		itemDimensionService.save(itemDimension);
		addMessage(redirectAttributes, "保存维度成功");
//		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?repage";
		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?treeId="+itemDimension.getCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:itemDimension:edit")
	@RequestMapping(value = "delete")
	public String delete(ItemDimension itemDimension, RedirectAttributes redirectAttributes) {
		itemDimensionService.delete(itemDimension);
		addMessage(redirectAttributes, "删除维度成功");
//		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?repage";
		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?treeId="+itemDimension.getCategory().getId()+"&repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ItemDimension> list = itemDimensionService.findTree(null);
		for (int i=0; i<list.size(); i++){
			ItemDimension e = list.get(i);
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
	
	
	@RequiresPermissions("mod:itemDimension:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/itemDimension");
		model.addAttribute("title","评价维度");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:itemDimension:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/itemDimension");
		model.addAttribute("title","商品类型");
		List<ItemCategory> itemCategoryTree = itemCategoryService.findTree();
		model.addAttribute("list", itemCategoryTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:itemDimension:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个类别。");
		return "treeData/none";
	}
}