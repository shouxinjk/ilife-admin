/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.Date;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.entity.ItemDimensionMeasure;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionMeasureService;
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
	@Autowired
	private ItemDimensionMeasureService itemDimensionMeasureService;
	
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
	 * 查询 分类-维度-属性 树结构数据
	 * @param id：其中以dim-打头为维度定义数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/standard-dimensions", method = RequestMethod.GET)
	public List<Map<String, Object>> listCategoriesAndDimensionsTree(String id) {
		Map<String,String> icon = Maps.newHashMap();
		icon.put("folder","fas fa-book");
		icon.put("openFolder","fas fa-book-open");
		icon.put("file","fas fa-file");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		
		if(id.startsWith("dim-")) {//如果展开的是维度节点则加载 子维度 
			//添加维度节点定义：根据父维度ID查找子级维度列表
			ItemDimension parentDimension = itemDimensionService.get(id.replace("dim-", ""));//以当前维度为父节点查询
			ItemDimension q = new ItemDimension(); 
			q.setParent(parentDimension);
			for(ItemDimension node:itemDimensionService.findList(q)) {//组装dimension节点列表
				Map<String, Object> map = Maps.newHashMap();
				map.put("parent", id);
				map.put("value", node.getName());
				map.put("id", "dim-"+node.getId());//对于维度节点使用 dim- 前缀进行区分
				map.put("opened", false);
				map.put("items", true);//默认都认为有下级
				map.put("icon", icon);//设置图标
				mapList.add(map);
			}
		}else {//否则认为是分类节点，加载子分类及子分类下的维度
			String categoryId = id;//默认id接受前端传递的id值，但对于根节点，前端传递的是div的id，固定为 tree-source ，需要进行映射
			if(id==null || id.trim().length()==0 || "tree-source".equalsIgnoreCase(id)) 
				categoryId = "0";//默认查询根目录下的节点 
	
			//加载子分类
			List<ItemCategory> list = itemCategoryService.findByParentId(categoryId);
			for (int i=0; i<list.size(); i++){
				ItemCategory e = list.get(i);
				Map<String, Object> map = Maps.newHashMap();
	//			if(!"-1".equalsIgnoreCase(id))//根节点不能加
				map.put("parent", id);
				map.put("value", e.getName());
				map.put("id", e.getId());
				map.put("opened", false);
				map.put("items", true);//默认都认为有下级目录
	//			map.put("icon", icon);//设置图标
				mapList.add(map);
			}
			
			//添加维度节点定义：根据分类ID查找对应分类下的维度定义节点
			ItemCategory category = itemCategoryService.get(categoryId);
			ItemDimension rootParentDimension = itemDimensionService.get("1");
			ItemDimension q = new ItemDimension(); 
			q.setCategory(category);
			q.setParent(rootParentDimension);
			//查询分类下的客观维度根节点：category为当前选中ID，并且其父节点为1。
			List<ItemDimension> dimensionNodes = itemDimensionService.findList(q);
			if(dimensionNodes.size()>0) {//当前在每一个分类下默认会建立一个dimension根节点，有且仅有一个，这个节点要被过滤掉。
				ItemDimension rootDimension = dimensionNodes.get(0);//只取第一个，并开始查找子级dimension
				q.setParent(rootDimension);
				dimensionNodes = itemDimensionService.findList(q);
				for(ItemDimension node:dimensionNodes) {//组装dimension节点列表
					Map<String, Object> map = Maps.newHashMap();
					map.put("parent", id);
					map.put("value", node.getName());
					map.put("id", "dim-"+node.getId());//对于维度节点使用 dim- 前缀进行区分
					map.put("opened", false);
					map.put("items", true);//默认都认为有下级
					mapList.add(map);
				}
			}else {//表示这个分类下没有定义任何客观评价维度，直接不管了
				//do nothing
			}
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/dimension/{id}", method = RequestMethod.GET)
	//查询指定维度下的 子维度及 属性列表，用于进行占比标注
	public List<Map<String, Object>> listValuesByMeasureId( @PathVariable String id, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		
		if(id.startsWith("dim-")) {//如果展开的是维度节点则加载 子维度 
			//查询子维度并添加
			ItemDimension dimension = itemDimensionService.get(id.replace("dim-",""));
			ItemDimension q = new ItemDimension();
			q.setParent(dimension);
			for(ItemDimension node:itemDimensionService.findList(q)) {//查询子维度列表
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", "dim-"+node.getId());//对于维度节点使用 dim- 前缀进行区分
				map.put("type", "维度");
				map.put("name", node.getName());
				map.put("weight", node.getWeight());
				mapList.add(map);
			}
			//查询 维度-属性 并添加
			ItemDimensionMeasure itemDimensionMeasure = new ItemDimensionMeasure();
			itemDimensionMeasure.setDimension(dimension);
			for(ItemDimensionMeasure node:itemDimensionMeasureService.findList(itemDimensionMeasure)) {//查询维度下的属性列表
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", "prop-"+node.getId());//对于维度节点使用 prop- 前缀进行区分
				map.put("type", "属性");
				map.put("name", node.getName());
				map.put("weight", node.getWeight());
				mapList.add(map);
			}
		}else {//是category，则直接加载level1 维度列表
			//添加维度节点定义：根据分类ID查找对应分类下的维度定义节点
			ItemCategory category = itemCategoryService.get(id);
			if(category != null) {//不对树节点根目录进行操作，仅对有意义节点操作
				ItemDimension rootParentDimension = itemDimensionService.get("1");
				ItemDimension q = new ItemDimension(); 
				q.setCategory(category);
				q.setParent(rootParentDimension);
				//查询分类下的客观维度根节点：category为当前选中ID，并且其父节点为1。
				List<ItemDimension> dimensionNodes = itemDimensionService.findList(q);
				if(dimensionNodes.size()>0) {//当前在每一个分类下默认会建立一个dimension根节点，有且仅有一个，这个节点要被过滤掉。
					ItemDimension rootDimension = dimensionNodes.get(0);//只取第一个，并开始查找子级dimension
					q = new ItemDimension(); 
					q.setParent(rootDimension);//仅根据父节点查询
					dimensionNodes = itemDimensionService.findList(q);
					for(ItemDimension node:dimensionNodes) {//组装dimension节点列表
						Map<String, Object> map = Maps.newHashMap();
						map.put("id", "dim-"+node.getId());//对于维度节点使用 dim- 前缀进行区分
						map.put("type", "维度");
						map.put("name", node.getName());
						map.put("weight", node.getWeight());
						mapList.add(map);
					}
				}
			}
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/weight", method = RequestMethod.POST)
	//更新维度占比 或者 维度下属性 占比。注意：需要根据id类型进行区分
	public Map<String, Object> updateValuesByMeasureId( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double weight,  
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", id);//保持原始id
		if(id.startsWith("dim-")) {//对维度占比进行更新
			ItemDimension itemDimension = itemDimensionService.get(id.replace("dim-", ""));
			itemDimension.setWeight(""+weight);
			itemDimensionService.save(itemDimension);
			itemDimension = itemDimensionService.get(id.replace("dim-", ""));
			map.put("type", "维度");
			map.put("name", itemDimension.getName());
			map.put("weight", itemDimension.getWeight());
		}else if(id.startsWith("prop-")) {//对维度-属性占比进行更新
			ItemDimensionMeasure itemDimensionMeasure = itemDimensionMeasureService.get(id.replace("prop-", ""));
			itemDimensionMeasure.setWeight(""+weight);
			itemDimensionMeasureService.save(itemDimensionMeasure);
			itemDimensionMeasure = itemDimensionMeasureService.get(id.replace("prop-", ""));
			map.put("type", "属性");
			map.put("name", itemDimensionMeasure.getName());
			map.put("weight", itemDimensionMeasure.getWeight());
		}else {//出错了
			//do nothing
		}
		return map;
	}
	
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