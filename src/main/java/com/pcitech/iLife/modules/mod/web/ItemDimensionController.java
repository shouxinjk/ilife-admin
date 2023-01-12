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
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.entity.ItemDimensionMeasure;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionMeasureService;
import com.pcitech.iLife.modules.mod.service.ItemDimensionService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.util.Util;

import groovy.util.Node;

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
	private MeasureService measureService;
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


	//根据category获取相应的客观评价树。
	//自动根据category及parentId=1查询得到根节点
	@ResponseBody
	@RequestMapping(value = "rest/link-tree-by-category", method = RequestMethod.GET)
	public List<Map<String, Object>> listDiemensionTreeForSankeyByCategory(String categoryId) {
		ItemCategory category = itemCategoryService.get(categoryId);
		if(category == null || category.getId() == null || category.getId().trim().length()==0) //必须有category，否则返回空白列表
			return Lists.newArrayList();
		ItemDimension root = new ItemDimension(); 
		root.setId("1");//指定该目录下ID为1的记录为根节点
		ItemDimension q = new ItemDimension(); 
		q.setCategory(category);
		q.setParent(root);
		
		List<ItemDimension> nodes = itemDimensionService.findList(q);
		if(nodes !=null && nodes.size()>0) {
			List<Map<String, Object>> links = Lists.newArrayList();
			//listDiemensionTreeForSunkey(links,categoryId,nodes.get(0).getId());
			//丢弃顶级节点，直接获取下级节点
			q.setParent(nodes.get(0));
			nodes = itemDimensionService.findList(q);
			for(ItemDimension node:nodes) {
				listDiemensionTreeForSunkey(links,categoryId,node.getId());
			}
			return links;
		}else {//否则尝试查询root.id=categoryId的记录
			return Lists.newArrayList();
		}
	}

	/**
	 * 获取sankey节点，包含：
	 * source: dimension
	 * target: dimension / measure
	 * weight: xxx
	 */
	@ResponseBody
//	@RequestMapping(value = "rest/dim-link-tree", method = RequestMethod.GET)
	private void listDiemensionTreeForSunkey(List<Map<String, Object>> links, String categoryId,String parentId) {
		ItemDimension parentDimension = itemDimensionService.get(parentId);//以当前维度为父节点查询
//		ItemCategory category = itemCategoryService.get(categoryId);
		ItemDimension q = new ItemDimension(); 
		q.setParent(parentDimension);
//		q.setCategory(category);
		for(ItemDimension node:itemDimensionService.findList(q)) {//组装link列表:source为父节点，target为子节点
			Map<String, Object> map = Maps.newHashMap();
			map.put("source", node);//当前节点为source
			map.put("target", parentDimension);//父节点为target
			map.put("weight", node.getWeight());
			map.put("value", node.getWeight()*0.75);//设置默认值
			links.add(map);
			listDiemensionTreeForSunkey(links, node.getCategory().getId(),node.getId());//迭代获取所有下级维度
		}
		//获取关联的属性节点
		ItemDimensionMeasure dimensionMeasure = new ItemDimensionMeasure();
		dimensionMeasure.setDimension(parentDimension);
		List<ItemDimensionMeasure> dimensionMeasures = itemDimensionMeasureService.findList(dimensionMeasure);
		for(ItemDimensionMeasure item:dimensionMeasures) {
			//需要判定measure是否是继承得到
			Measure measure = measureService.get(item.getMeasure());
			//添加属性
			Map<String, Object> node = Maps.newHashMap();
			if(measure==null) {
				//ignore 表示measure在建立后被删除
			}else {
				node.put("source", measure);
			}
			node.put("target", parentDimension);
			node.put("weight", item.getWeight());
			node.put("value", item.getWeight()*0.75);//设置默认值
			links.add(node);
		}
	}
	
	
	//根据category获取相应的客观评价树。
	//自动根据category及parentId=1查询得到根节点
	@ResponseBody
	@RequestMapping(value = "rest/dim-tree-by-category", method = RequestMethod.GET)
	public List<Map<String, Object>> listDiemensionTreeForSunburstChartByCategory(String categoryId) {
		ItemCategory category = itemCategoryService.get(categoryId);
		if(category == null || category.getId() == null || category.getId().trim().length()==0) //必须有category，否则返回空白列表
			return Lists.newArrayList();
		ItemDimension root = new ItemDimension(); 
		root.setId("1");//指定该目录下ID为1的记录为根节点
		ItemDimension q = new ItemDimension(); 
		q.setCategory(category);
		q.setParent(root);
		
		List<ItemDimension> nodes = itemDimensionService.findList(q);
		if(nodes !=null && nodes.size()>0)
			return listDiemensionTreeForSunburstChart(categoryId,nodes.get(0).getId());
		else {//否则尝试查询root.id=categoryId的记录
			//return listDiemensionTreeForSunburstChart(categoryId,categoryId);
			return Lists.newArrayList();
		}
	}

	/**
	 * 获取指定节点下的维度树。用于客观评价图形化显示。sunburst。
	 * 输入为父维度ID（顶级维度与所属类目ID相同），输出为所有子维度name及weight。嵌套输出。
	 */
	@ResponseBody
	@RequestMapping(value = "rest/dim-tree", method = RequestMethod.GET)
	public List<Map<String, Object>> listDiemensionTreeForSunburstChart(String categoryId,String parentId) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		ItemCategory category = itemCategoryService.get(categoryId);
		ItemDimension parentDimension = itemDimensionService.get(parentId);//以当前维度为父节点查询
		if(category ==null)//如果category不存在则返回空白列表
			return Lists.newArrayList();
		if(parentDimension==null) {//对于维度根节点ID与类目ID不同的情况，parentDimension可能为空，需要另外查询
			//设置类目维度根节点
			ItemDimension categoryRootDimension = new ItemDimension();
			categoryRootDimension.setCategory(category);
			categoryRootDimension.setId("1");//指定为1
			//设置查询条件：查询根节点下级节点，并且取第一个
			ItemDimension q = new ItemDimension();
			q.setCategory(category);
			q.setParent(categoryRootDimension);
			List<ItemDimension> rootDimension = itemDimensionService.findList(q);
			
			if(rootDimension.size()==0) {//如果没有则表示数据错误，直接返回空白列表
				return Lists.newArrayList();
			}else {
				parentDimension = rootDimension.get(0);
			}
		}
		
		ItemDimension q = new ItemDimension(); 
		q.setParent(parentDimension);
		q.setCategory(category);
		for(ItemDimension node:itemDimensionService.findList(q)) {//组装dimension节点列表
			Map<String, Object> map = Maps.newHashMap();
			map.put("name", node.getName());
			map.put("weight", node.getWeight());
			map.put("children",listDiemensionTreeForSunburstChart(node.getCategory().getId(),node.getId()));//迭代获取所有下级维度
			mapList.add(map);
		}
		//获取关联的属性节点
		ItemDimensionMeasure dimensionMeasure = new ItemDimensionMeasure();
		dimensionMeasure.setDimension(parentDimension);
		List<ItemDimensionMeasure> dimensionMeasures = itemDimensionMeasureService.findList(dimensionMeasure);
		for(ItemDimensionMeasure item:dimensionMeasures) {
			//需要判定measure是否是继承得到
			Measure measure = measureService.get(item.getMeasure());
			//添加属性
			Map<String, Object> node = Maps.newHashMap();
			if(measure==null) {
				node.put("name", "-"+item.getName());//表示measure在建立后被删除
			}else if(measure.getCategory().getId().equalsIgnoreCase(category.getId())) {//是继承属性
				node.put("name", "๏"+measure.getName());
			}else {
				node.put("name", "○"+measure.getName());
			}
			node.put("weight", item.getWeight());
			mapList.add(node);
		}
		
		return mapList;
	}
	
	/**
	 * 根据categoryId获取所有特征维度定义。返回维度列表
	 * 参数：
	 * categoryId：类目ID
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "rest/featured-dimension", method = RequestMethod.GET)
	public List<ItemDimension> listFeaturedDimensionByCategoryId(@RequestParam String categoryId) {
		ItemCategory category = itemCategoryService.get(categoryId);
		if(category==null)//如果目录没有则直接返回空
			return Lists.newArrayList();
		ItemDimension q = new ItemDimension(); 
		q.setCategory(category);
		q.setFeatured(true);//仅返回featured节点
//		ItemDimension parentDimension = new ItemDimension();
//		parentDimension.setId("1");//指定根节点下级节点
//		q.setParent(parentDimension);
		return itemDimensionService.findList(q);
	}
	
	/**
	 * 根据dimensionId获取所有下级节点。包含指标节点及属性节点
	 * 如果为根节点，并且未建立评价体系，则默认从父级类目复制
	 * 参数：
	 * dimensionId：维度ID
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "rest/child", method = RequestMethod.GET)
	public JSONObject listChildDimensionByDimensionId(String dimensionId) {
		JSONObject json = new JSONObject();
		json.put("success", false);
		ItemDimension dimension = itemDimensionService.get(dimensionId);
		if(dimension==null)//如果没有则直接返回空
			return json;
		ItemDimension q = new ItemDimension(); 
		q.setParent(dimension);
		List<ItemDimension> result = itemDimensionService.findList(q);
		//如果当前节点为根节点，且当前节点没有定义评价体系则默认克隆上级节点评价体系
		if(result == null || result.size()==0) {
			ItemCategory category = itemCategoryService.get(dimensionId);//根据维度ID获取类目，用于判定是否是根节点
			if(category!=null) {//如果为根节点，则默认从父节点复制
				inheritFromParent(dimensionId);
				result = itemDimensionService.findList(q);//复制完成后重新得到节点
			}
		}
		json.put("dimensions", result);
		
		//查询属性节点
		ItemDimensionMeasure q2 = new ItemDimensionMeasure();
		q2.setDimension(dimension);
		json.put("measures", itemDimensionMeasureService.findList(q2));
		
		json.put("success", true);
		
		return json;
	}
	
	/**
	 * 从移动端直接新增或修改
	 * @param itemDimension
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/dimension", method = RequestMethod.POST)
	public JSONObject upsert(@RequestBody ItemDimension itemDimension) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(itemDimension.getId()==null || itemDimension.getId().trim().length()==0) {//新增需要设置ID
			itemDimension.setId(Util.get32UUID());
			itemDimension.setIsNewRecord(true);
			itemDimension.setCreateDate(new Date());
			itemDimension.setUpdateDate(new Date());
			itemDimension.setSort(10);//默认随便设置
			//设置默认字段
			itemDimension.setPropKey("m"+Util.get6bitCodeRandom());
		}
		itemDimensionService.save(itemDimension);//先直接保存
		//查询ItemDimension作为后续使用，由于需要重新计算脚本，对于新提交节点，需要重新查询，避免导致duplicate记录错误
		itemDimension = itemDimensionService.get(itemDimension);
		if(itemDimension == null) {
			result.put("msg", "save dimension failed.");
			return result;
		}

		result.put("data", itemDimension);
		
		/**
		//自动重新计算其他节点权重：等比例压缩。注意要包含子节点及属性节点共同重新计算
		ItemDimension q = new ItemDimension();
		q.setParent(itemDimension.getParent());
		List<ItemDimension> nodes = itemDimensionService.findList(q); //获取节点列表
		ItemDimensionMeasure q2 = new ItemDimensionMeasure();
		q2.setDimension(itemDimension.getParent());
		List<ItemDimensionMeasure> measures = itemDimensionMeasureService.findList(q2);
		
		double ratio = 1;
		if(itemDimension.getWeight()<100 && itemDimension.getWeight()>0)
			ratio = (100-itemDimension.getWeight())/100;
		
		double total = 0;
		for(ItemDimension node:nodes) {
			total += node.getWeight();
		}
		for(ItemDimensionMeasure measure:measures) {
			total += measure.getWeight();
		}
		if(total==0) {
			result.put("msg", "re-calculate weight error due to total is 0.");
			result.put("success", true);
			return result;
		}
		
		for(ItemDimension node:nodes) { //更新节点权重
			if(node.getId().equalsIgnoreCase(itemDimension.getId())) {
				//当前节点，忽略
			}else {//否则更新权重
				node.setWeight(ratio*node.getWeight()/total);
				node.setUpdateDate(new Date());
				itemDimensionService.save(node);
			}
		}
		for(ItemDimensionMeasure measure:measures) {//更新属性权重
			measure.setWeight(ratio*measure.getWeight()/total);
			measure.setUpdateDate(new Date());
			itemDimensionMeasureService.save(measure);
		}
		//**/
		
		//更新脚本
		if(itemDimension.getId() != null && itemDimension.getId().trim().length()>0)
			saveWithScript(itemDimension);
		//递归更新父节点
		if(itemDimension.getParent()!=null && itemDimension.getParent().getId()!=null)
			saveWithScript(itemDimension.getParent());
		result.put("msg",  "保存维度成功");
		result.put("success", true);
		return result;
	}
	
	/**
	 * 从移动端查询 维度信息，包括当前维度节点、上级、上上级。直到根目录
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/full-dimension/{id}", method = RequestMethod.GET)
	public List<ItemDimension> getById(@PathVariable String id) {
		List<ItemDimension> result = Lists.newArrayList();
		boolean hasMore = true;
		while(hasMore) {
			ItemDimension itemDimension = itemDimensionService.get(id);
			if(itemDimension != null) {
				result.add(itemDimension);
				if(itemDimension.getParent()!=null) {
					id = itemDimension.getParent().getId();
				}else {
					hasMore = false;
				}
			}else {
				hasMore = false;
			}
		}
		return result;
	}
	
	/**
	 * 从移动端直接删除
	 * 需要同时更新其他节点weight
	 * @param itemDimension
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/dimension/{id}", method = RequestMethod.PUT)
	public JSONObject delete(@PathVariable String id) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		ItemDimension itemDimension = itemDimensionService.get(id);
		if(itemDimension ==null || itemDimension.getId()==null) {//没有则直接忽略
			result.put("msg", "指定的指标节点不存在。");
			return result;
		}
		result.put("data", itemDimension);
		itemDimensionService.delete(itemDimension);//先直接删除
		
		/**
		//自动重新计算其他节点权重：等比例压缩
		double ratio = 1;
		if(itemDimension.getWeight()<100 && itemDimension.getWeight()>0)
			ratio = 100/(100-itemDimension.getWeight());
		ItemDimension q = new ItemDimension();
		q.setParent(itemDimension.getParent());
		List<ItemDimension> nodes = itemDimensionService.findList(q);
		
		ItemDimensionMeasure q2 = new ItemDimensionMeasure();
		q2.setDimension(itemDimension.getParent());
		List<ItemDimensionMeasure> measures = itemDimensionMeasureService.findList(q2);
		
		for(ItemDimension node:nodes) {//更新节点权重
			if(node.getId().equalsIgnoreCase(itemDimension.getId())) {
				//当前节点，忽略
			}else {//否则更新权重
				node.setWeight(node.getWeight()*ratio);
				itemDimensionService.save(node);
			}
		}
		for(ItemDimensionMeasure measure:measures) {//更新属性权重
			measure.setWeight(ratio*measure.getWeight());
			measure.setUpdateDate(new Date());
			itemDimensionMeasureService.save(measure);
		}
		//**/
		
		//更新脚本
		if(itemDimension.getId() != null && itemDimension.getId().trim().length()>0)
			saveWithScript(itemDimension);
		//递归更新父节点
		if(itemDimension.getParent()!=null && itemDimension.getParent().getId()!=null)
			saveWithScript(itemDimension.getParent());
		result.put("msg",  "删除维度成功");
		result.put("success", true);
		return result;
	}
	
	/**
	 * 用于从移动端复制父节点评价体系
	 * 获取父节点的评价维度及评价属性，并更新到当前节点
	 * @param id 维度ID，顶级节点为类目ID
	 * @return
	 */
	public JSONObject inheritFromParent(String id) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		logger.error("got dimension by category id. "+id);

		//获取上级节点：直接根据dimension对应的category查找上级category
		ItemCategory itemCategory = itemCategoryService.get(id);
		if(itemCategory == null) {
			result.put("msg", "无法获取父节点及其评价维度，忽略");
		}else {//尝试获取上级ItemCategory
			ItemCategory parentItemCategory = itemCategory.getParent();
			if(parentItemCategory == null) {
				result.put("msg", "没有父节点，忽略");
			}else {
				logger.error("try get current node by category id. "+id);
				ItemDimension itemDimension = itemDimensionService.get(id);//根据当前类目ID查询维度节点
				if(itemDimension == null) {//如果根据cagtegory ID查询不存在，则查询该类目下ID为1的记录
					logger.debug("no root dimension found by category id. try query.[category id]"+id);
					ItemDimension parent = new ItemDimension();
					parent.setId("1");
					ItemDimension q = new ItemDimension();
					q.setCategory(itemCategory);
					q.setParent(parent);
					List<ItemDimension> roots = itemDimensionService.findList(q);
					if(roots.size()>0) {
						itemDimension = roots.get(0);
					}
				}
				
				logger.error("try get parent node by category id. "+parentItemCategory.getId());
				ItemDimension parentDimension = itemDimensionService.get(parentItemCategory.getId());//根节点ID与category相同
				if(parentDimension == null) {//如果根据cagtegory ID查询不存在，则查询该类目下ID为1的记录
					logger.debug("no root dimension found by category id. try query.[category id]"+parentItemCategory.getId());
					ItemDimension parent = new ItemDimension();
					parent.setId("1");
					ItemDimension q = new ItemDimension();
					q.setCategory(parentItemCategory);
					q.setParent(parent);
					List<ItemDimension> roots = itemDimensionService.findList(q);
					if(roots.size()>0) {
						parentDimension = roots.get(0);
					}
				}
				
				if(parentDimension!=null && itemDimension!=null) {
					copyDimensionAndMeauser(parentDimension, itemDimension);//递归复制所有评价节点及属性
					result.put("msg", "根据目录复制父节点维度成功");
					result.put("success", true);
				}else {
					result.put("msg", "不能根据类目ID获取维度节点，忽略");
				}
			}
		}
		return result;
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
				map.put("name", node.getName()+"-"+node.getMeasure().getName());
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
	public Map<String, Object> updateWeightsByMeasureId( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double weight,  
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		ItemDimension parentDimension = null;
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", id);//保持原始id
		if(id.startsWith("dim-")) {//对维度占比进行更新
			ItemDimension itemDimension = itemDimensionService.get(id.replace("dim-", ""));
			parentDimension = itemDimensionService.get(itemDimension.getParent().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
			itemDimension.setWeight(weight);
			itemDimensionService.save(itemDimension);
			itemDimension = itemDimensionService.get(id.replace("dim-", ""));
			map.put("type", "维度");
			map.put("name", itemDimension.getName());
			map.put("weight", itemDimension.getWeight());
		}else if(id.startsWith("prop-")) {//对维度-属性占比进行更新
			ItemDimensionMeasure itemDimensionMeasure = itemDimensionMeasureService.get(id.replace("prop-", ""));
			parentDimension = itemDimensionService.get(itemDimensionMeasure.getDimension().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
			itemDimensionMeasure.setWeight(weight);
			itemDimensionMeasureService.save(itemDimensionMeasure);
			itemDimensionMeasure = itemDimensionMeasureService.get(id.replace("prop-", ""));
			map.put("type", "属性");
			map.put("name", itemDimensionMeasure.getName());
			map.put("weight", itemDimensionMeasure.getWeight());
		}else {//出错了
			//do nothing
		}
		//更新所在维度节点的自动脚本
		if(parentDimension!=null)
			saveWithScript(parentDimension);
		else
			logger.warn("cannot find parent dimension.");
		return map;
	}
	
	@RequiresPermissions("mod:itemDimension:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemDimension itemDimension,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
//		List<ItemDimension> list = Lists.newArrayList();
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
			rootDimension.setPropKey("e"+Util.get6bitCode(category.getName()));
			rootDimension.setScript("weighted-sum");
			rootDimension.setScriptType("auto");
			rootDimension.setScriptMemo("加权汇总");
			rootDimension.setCategory(category);
			rootDimension.setId(treeId);//默认顶级节点ID与category保持一致
			rootDimension.setIsNewRecord(true);
			rootDimension.setFeatured(false);
			rootDimension.setWeight(100);
			try {
				//对于顶级节点，由于根节点与默认顶级节点ID均为1，会导致错误。直接忽略
				itemDimensionService.save(rootDimension);
			}catch(Exception ex) {
				//do nothing
				//logger.debug("failed create root dimension.[category]"+treeId,ex);
			}
		}
//		List<ItemDimension> sourcelist = itemDimensionService.findTree(category);
//		ItemDimension.sortList(list, sourcelist, "1",true);
		
		List<JSONObject> list = findDimensionAndMeasure(category);
		model.addAttribute("list", list);
		model.addAttribute("treeId", treeId);
		return "modules/mod/itemDimensionList";
	}
	
	/**
	 * 装载客观评价维度及关联的属性
	 * @param category
	 * @return
	 */
	private List<JSONObject> findDimensionAndMeasure(ItemCategory category){
		List<JSONObject> nodes = Lists.newArrayList();
		//先查出root dimension
		ItemDimension rootDimension = itemDimensionService.get(category.getId());
		if(rootDimension == null) {//如果根据cagtegory ID查询不存在，则查询该类目下ID为1的记录
			logger.debug("no root dimension found by category id.[category id]"+category.getId());
			ItemDimension parent = new ItemDimension();
			parent.setId("1");
			ItemDimension q = new ItemDimension();
			q.setCategory(category);
			q.setParent(parent);
			List<ItemDimension> roots = itemDimensionService.findList(q);
			if(roots== null || roots.size()==0) {
				logger.debug("no root dimension found by category and root=1.[category]"+category.getId());
				return nodes;
			}else {
				logger.debug("got root dimension by category and id=1.[category]"+category.getId());
				rootDimension = roots.get(0);
			}
		}
		//添加本级节点，注意不添加根节点 
		if(!"1".equalsIgnoreCase(rootDimension.getId())) {
			logger.debug("find root dimension by category id.[category id]"+category.getId());
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(rootDimension);
			node.put("type", "dimension");
			node.put("id", rootDimension.getId());
			node.put("parent", rootDimension.getParent());
			node.put("category", category);
			node.put("name", rootDimension.getName());
			node.put("weight", rootDimension.getWeight());
			node.put("description", rootDimension.getDescription());
			node.put("propKey", rootDimension.getPropKey());
			node.put("featured", rootDimension.isFeatured());
			node.put("script", rootDimension.getScript());
			node.put("scriptType", rootDimension.getScriptType());
			node.put("scriptMemo", rootDimension.getScriptMemo());
			nodes.add(node);
		}
		//递归遍历子节点
		loadDimensionAndMeasureCascade(category,rootDimension,nodes);
		return nodes;
	}
	
	//递归获取所有下级节点与关联属性
	private void loadDimensionAndMeasureCascade(ItemCategory category,ItemDimension dimension, List<JSONObject> nodes) {
		//查询所有下级节点
		ItemDimension q = new ItemDimension();
		q.setParent(dimension);
		q.setCategory(category);
		List<ItemDimension> dimensions = itemDimensionService.findList(q);
		logger.debug("try find sub dimension.[dimension]"+dimension.getId()+"[category]"+category.getId());
		for(ItemDimension item:dimensions) {
			//添加本级节点
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(item);
			node.put("type", "dimension");
			node.put("id", item.getId());
			node.put("parent", item.getParent());
			node.put("category", category);
			node.put("name", item.getName());
			node.put("weight", item.getWeight());
			node.put("description", item.getDescription());
			node.put("propKey", item.getPropKey());
			node.put("featured", item.isFeatured());
			node.put("script", item.getScript());
			node.put("scriptType", item.getScriptType());
			node.put("scriptMemo", item.getScriptMemo());
			nodes.add(node);
			loadDimensionAndMeasureCascade(category,item,nodes);//递归遍历
		}
		//查询所有关联属性
		logger.debug("try find measure.[dimension]"+dimension.getId()+"[category]"+category.getId());
		ItemDimensionMeasure dimensionMeasure = new ItemDimensionMeasure();
		dimensionMeasure.setDimension(dimension);
		List<ItemDimensionMeasure> dimensionMeasures = itemDimensionMeasureService.findList(dimensionMeasure);
		for(ItemDimensionMeasure item:dimensionMeasures) {
			//需要判定measure是否是继承得到
			Measure measure = measureService.get(item.getMeasure());
			//添加属性
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(item);
			node.put("type", "measure");
			node.put("id", item.getId());
			node.put("parent", dimension);//将dimension作为parent设置
			node.put("category", category);
			logger.debug("[measure.category.id]"+measure.getCategory().getId()+"[dimension.category.id]"+category.getId());
			if(measure==null) {
				node.put("name", "-"+item.getName());//表示measure在建立后被删除
			}else if(measure.getCategory().getId().equalsIgnoreCase(category.getId())) {//是继承属性
				node.put("name", "๏"+measure.getName());
			}else {
				node.put("name", "○"+measure.getName());
			}
			node.put("weight", item.getWeight());
			node.put("description", item.getDescription());
			node.put("propKey", measure.getProperty());
			nodes.add(node);
		}
	}

	@RequiresPermissions("mod:itemDimension:view")
	@RequestMapping(value = "form")
	public String form(ItemDimension itemDimension, Model model) {
        if (itemDimension.getParent()==null||itemDimension.getParent().getId()==null){
	    		ItemCategory category = itemCategoryService.get(itemDimension.getCategory().getId());
	    		ItemDimension parentDimension = itemDimensionService.get(itemDimension.getCategory().getId());
	    		if(parentDimension!=null) { //默认放到与category对应的节点下则直接放到顶级维度下
	    			itemDimension.setParent(parentDimension);
	    		}else { //否则根据顶级节点查找 
	    			ItemDimension rootParentDimension = itemDimensionService.get("1");
		    		ItemDimension q = new ItemDimension(); 
		    		q.setCategory(category);
		    		q.setParent(rootParentDimension);
	        		List<ItemDimension> rootDimensions = itemDimensionService.findList(q);
	        		if(rootDimensions.size()>0) {
	        			ItemDimension root = rootDimensions.get(0);
	        			itemDimension.setParent(root);
	        		}else{//如果是顶级节点，则设置为根维度，即 id=1
	    				ItemDimension root = new ItemDimension();
	    				root.setId("1");
	    				itemDimension.setParent(root);
	    			}
	    		}
        }
		if (itemDimension.getParent()!=null && StringUtils.isNotBlank(itemDimension.getParent().getId())) { 
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
		if(itemDimension.getScript()==null||itemDimension.getScript().trim().length()==0) {
			itemDimension.setScript("weighted-sum");
			itemDimension.setScriptType("auto");
			itemDimension.setScriptMemo("加权汇总");
		}
		//itemDimension.setFeatured(false);
		//检查默认key值，如果没有则随机设置
		if(itemDimension.getPropKey()==null || itemDimension.getPropKey().trim().length()==0)
			itemDimension.setPropKey("m"+Util.get6bitCode(itemDimension.getName()));//以p打头的7位字符串，大小写区分。保存时如果重复将报错
		model.addAttribute("itemDimension", itemDimension);
		return "modules/mod/itemDimensionForm";
	}

	@RequiresPermissions("mod:itemDimension:edit")
	@RequestMapping(value = "save")
	public String save(ItemDimension itemDimension, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, itemDimension)){
			return form(itemDimension, model);
		}
		itemDimensionService.save(itemDimension);
		//更新脚本
		if(itemDimension.getId() != null && itemDimension.getId().trim().length()>0)
			saveWithScript(itemDimension);
		//递归更新父节点
		if(itemDimension.getParent()!=null && itemDimension.getParent().getId()!=null)
			saveWithScript(itemDimension.getParent());
		addMessage(redirectAttributes, "保存维度成功");
//		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?repage";
		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?treeId="+itemDimension.getCategory().getId()+"&repage";
	}
	
	//动态计算脚本并保存
	private void saveWithScript(ItemDimension itemDimension) {
		logger.error("try to save with script.[itemDimension.id]"+itemDimension.getId(),itemDimension);
		//预生成脚本：对于weighted-sum脚本，自动查询下级节点，并生成
		if( "auto".equalsIgnoreCase(itemDimension.getScriptType()) ) {
			//先获取属性列表
			ItemDimensionMeasure itemDimensionMeasure = new ItemDimensionMeasure();
			itemDimensionMeasure.setDimension(itemDimension);
			itemDimensionMeasure.setDelFlag("0");
			List<ItemDimensionMeasure> measures = itemDimensionMeasureService.findList(itemDimensionMeasure);
			String script = "";
			String scriptMemo = "";
			int index = 0;
			for(ItemDimensionMeasure measure:measures) {
				if(measure.getMeasure().getProperty()==null||measure.getMeasure().getProperty().trim().length()==0)
					continue;//未设置属性propKey则不作处理
				if(index>0) {
					script += "+";
					scriptMemo += "+";
				}
				double weight = measure.getWeight()*0.01;
				weight = (double) Math.round(weight * 100) / 100;//仅保留2位小数
				script += measure.getMeasure().getProperty()+ "*" + weight;
				scriptMemo += measure.getMeasure().getName()+ "*" + weight;
				index++;
			}
			//然后获取下级维度列表
			ItemDimension query = new ItemDimension();
			query.setParent(itemDimension);
			query.setDelFlag("0");
			List<ItemDimension> subDimensions = itemDimensionService.findList(query);
			index = 0;
			for(ItemDimension dimension:subDimensions) {
				if(dimension.getPropKey()==null || dimension.getPropKey().trim().length()==0)
					continue;//未设置属性propKey则不作处理
				if(script.trim().length()>0 || (script.trim().length()==0 && index>0)) {
					script += "+";
					scriptMemo += "+";
				}
				double weight = dimension.getWeight()*0.01;
				weight = (double) Math.round(weight * 100) / 100;//仅保留2位小数
				script += dimension.getPropKey()+ "*" + weight;
				scriptMemo += dimension.getName()+ "*" + weight;
				index++;
			}
			itemDimension.setScript(script);
			itemDimension.setScriptMemo(scriptMemo);
			
			itemDimensionService.save(itemDimension);
		}
		
	}
	
	@RequiresPermissions("mod:itemDimension:edit")
	@RequestMapping(value = "delete")
	public String delete(ItemDimension itemDimension, RedirectAttributes redirectAttributes) {
		itemDimensionService.delete(itemDimension);
		addMessage(redirectAttributes, "删除维度成功");
//		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?repage";
		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?treeId="+itemDimension.getCategory().getId()+"&repage";
	}
	
	/**
	 * 获取父节点的评价维度及评价属性，并更新到当前节点
	 * @param id 维度ID，即类目ID
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("mod:itemDimension:edit")
	@RequestMapping(value = "inherit")
	public String inheritFromParent(String id, RedirectAttributes redirectAttributes) {
		logger.error("got dimension by category id. "+id);

		//获取上级节点：直接根据dimension对应的category查找上级category
		ItemCategory itemCategory = itemCategoryService.get(id);
		if(itemCategory == null) {
			addMessage(redirectAttributes, "无法获取父节点及其评价维度，忽略");
		}else {//尝试获取上级ItemCategory
			ItemCategory parentItemCategory = itemCategory.getParent();
			if(parentItemCategory == null) {
				addMessage(redirectAttributes, "没有父节点，忽略");
			}else {
				logger.error("try get current node by category id. "+id);
				ItemDimension itemDimension = itemDimensionService.get(id);//根据当前类目ID查询维度节点
				if(itemDimension == null) {//如果根据cagtegory ID查询不存在，则查询该类目下ID为1的记录
					logger.debug("no root dimension found by category id. try query.[category id]"+id);
					ItemDimension parent = new ItemDimension();
					parent.setId("1");
					ItemDimension q = new ItemDimension();
					q.setCategory(itemCategory);
					q.setParent(parent);
					List<ItemDimension> roots = itemDimensionService.findList(q);
					if(roots.size()>0) {
						itemDimension = roots.get(0);
					}
				}
				
				logger.error("try get parent node by category id. "+parentItemCategory.getId());
				ItemDimension parentDimension = itemDimensionService.get(parentItemCategory.getId());//根节点ID与category相同
				if(parentDimension == null) {//如果根据cagtegory ID查询不存在，则查询该类目下ID为1的记录
					logger.debug("no root dimension found by category id. try query.[category id]"+parentItemCategory.getId());
					ItemDimension parent = new ItemDimension();
					parent.setId("1");
					ItemDimension q = new ItemDimension();
					q.setCategory(parentItemCategory);
					q.setParent(parent);
					List<ItemDimension> roots = itemDimensionService.findList(q);
					if(roots.size()>0) {
						parentDimension = roots.get(0);
					}
				}
				
				if(parentDimension!=null && itemDimension!=null) {
					copyDimensionAndMeauser(parentDimension, itemDimension);//递归复制所有评价节点及属性
					addMessage(redirectAttributes, "根据目录复制父节点维度成功");
				}else {
					addMessage(redirectAttributes, "不能根据类目ID获取维度节点，忽略");
				}
			}
		}
		return "redirect:"+Global.getAdminPath()+"/mod/itemDimension/?treeId="+id+"&repage";
	}
	
	/**
	 * 层递复制维度及属性
	 * @param itemDimension 父节点
	 */
	private void copyDimensionAndMeauser(ItemDimension fromNode, ItemDimension toNode) {
		logger.debug("start copy from "+fromNode.getName()+":"+fromNode.getId()+" to "+toNode.getName()+":"+toNode.getId());
		//复制节点下的属性
		ItemDimensionMeasure itemDimensionMeasure = new ItemDimensionMeasure();
		itemDimensionMeasure.setDimension(fromNode);
		itemDimensionMeasure.setDelFlag("0");
		List<ItemDimensionMeasure> measures = itemDimensionMeasureService.findList(itemDimensionMeasure);
		for(ItemDimensionMeasure measure:measures) {//逐条添加到当前节点下，需要判断是否存在，仅在不存在的时候添加
			ItemDimensionMeasure query = new ItemDimensionMeasure();
			query.setDimension(toNode);
			query.setMeasure(measure.getMeasure());
			List<ItemDimensionMeasure> nodes = itemDimensionMeasureService.findList(query);
			if(nodes.size()==0) {//仅在没有的时候才添加
				query.setName(measure.getName().replaceAll("○", "๏"));//继承而来，全部为继承属性
				query.setDescription(measure.getDescription());
				query.setWeight(measure.getWeight());
				query.setCreateDate(new Date());
				query.setUpdateDate(new Date());
				itemDimensionMeasureService.save(query);
			}
		}
		
		//获取下级维度节点
		ItemDimension query = new ItemDimension();

		//注意，需要判定是否是根节点。当前所有类目下的root维度均为根节点（id=1）的子节点，这与根节点自身的维度节点混淆，需要区分
		if("1".equalsIgnoreCase(fromNode.getId())) {//如果维度节点ID不为1则直接获取其下子节点，额外设置category进行过滤
			ItemCategory rootCategory = new ItemCategory();
			rootCategory.setId("1");
			query.setCategory(rootCategory);
		}
		query.setParent(fromNode);
		query.setDelFlag("0");//仅支持活跃的维度
		List<ItemDimension> subDimensions = itemDimensionService.findList(query);

		for(ItemDimension dimension:subDimensions) {//逐个新建子节点
			//仅在没有同名维度的情况下建立
			ItemDimension node = new ItemDimension();
			node.setCategory(toNode.getCategory());
			node.setParent(toNode);
			node.setName(dimension.getName());//根据名称查找，如果有重名则不创建
			List<ItemDimension> nodes = itemDimensionService.findList(node);
			ItemDimension newNode = null;
			if(nodes.size()==0) {//新建节点
				String id = Util.md5(Util.get32UUID()+dimension.getId());//随机生成ID
				node.setParent(toNode);
				node.setDescription(dimension.getDescription());
				node.setFeatured(dimension.isFeatured());
				node.setPropKey("m"+Util.get6bitCode(dimension.getName()));
				node.setWeight(dimension.getWeight());
				node.setCreateDate(new Date());
				node.setUpdateDate(new Date());
				node.setId(id);
				node.setIsNewRecord(true);
				try {
					itemDimensionService.save(node);
				}catch(Exception ex) {
					logger.warn("error while save item dimension.",ex);
				}
				newNode = itemDimensionService.get(id);
			}else {
				newNode = nodes.get(0);//否则以找到的节点建立
			}
			
			//递归
			copyDimensionAndMeauser(dimension, newNode);//递归建立下级节点及属性
		}
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		//ItemDimension dimension = itemDimensionService.get(extId);//得到当前发起操作的dimension
		//List<ItemDimension> list = itemDimensionService.findTree(dimension==null?null:dimension.getCategory());
		ItemCategory category = itemCategoryService.get(extId);//传递的extId为categoryId
		List<ItemDimension> list = itemDimensionService.findTree(category);
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