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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.IdGen;
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
	private ItemDimensionService itemDimensionService;
	@Autowired
	private ItemEvaluationDimensionService itemEvaluationDimensionService;
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
	
	/**
	 * 根据categoryId获取可标记的特征维度定义。返回abcdexyz8个节点供标注
	 * 参数：
	 * categoryId：类目ID
	 */
	@ResponseBody
	@RequestMapping(value = "rest/markable-featured-evaluation", method = RequestMethod.GET)
	public List<ItemEvaluation> listFeaturedDimensionByCategoryId(String categoryId) {
		ItemCategory category = itemCategoryService.get(categoryId);
		if(category==null)//如果目录没有则直接返回空
			return Lists.newArrayList();
		ItemEvaluation q = new ItemEvaluation(); 
		q.setCategory(category);
		q.setFeatured(true);//仅返回featured节点
		List<ItemEvaluation> result = itemEvaluationService.findList(q);
		List<ItemEvaluation> list = Lists.newArrayList();
		for(ItemEvaluation item:result) {
			if(",a,b,c,d,e,x,y,z".indexOf(item.getType())>0) {
				list.add(item);
			}
		}
		return list;
	}
	
	/**
	 * 根据categoryId获取所有特征维度定义。返回维度列表
	 * 参数：
	 * categoryId：类目ID
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "rest/featured-evaluation", method = RequestMethod.GET)
	public List<ItemEvaluation> listFeaturedEvaluationByCategoryId(String categoryId) {
		ItemCategory category = itemCategoryService.get(categoryId);
		ItemEvaluation q = new ItemEvaluation(); 
		q.setCategory(category);
		q.setFeatured(true);//仅返回featured节点
		return itemEvaluationService.findList(q);
	}

	@ResponseBody
	@RequestMapping(value = "rest/weight", method = RequestMethod.POST)
	//更新维度占比 或者 维度下属性 占比。注意：需要根据id类型进行区分
	public Map<String, Object> updateWeightsByMeasureId( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double weight,  
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		ItemEvaluation parentEvaluation = null;
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", id);//保持原始id
		if(id.startsWith("eval-")) {//对主观评价维度占比进行更新
			ItemEvaluation itemEvaluation = itemEvaluationService.get(id.replace("eval-", ""));
			parentEvaluation = itemEvaluationService.get(itemEvaluation.getParent().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
			itemEvaluation.setWeight(weight);
			itemEvaluationService.save(itemEvaluation);
			itemEvaluation = itemEvaluationService.get(id.replace("eval-", ""));
			map.put("type", "主观评价");
			map.put("name", itemEvaluation.getName());
			map.put("weight", itemEvaluation.getWeight());
		}else if(id.startsWith("dim-")) {//对客观评价占比进行更新
			ItemEvaluationDimension itemEvaluationDimension = itemEvaluationDimensionService.get(id.replace("dim-", ""));
			parentEvaluation = itemEvaluationService.get(itemEvaluationDimension.getEvaluation().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
			itemEvaluationDimension.setWeight(weight);
			itemEvaluationDimensionService.save(itemEvaluationDimension);
			itemEvaluationDimension = itemEvaluationDimensionService.get(id.replace("dim-", ""));
			map.put("type", "客观评价");
			map.put("name", itemEvaluationDimension.getName());
			map.put("weight", itemEvaluationDimension.getWeight());
		}else if(id.startsWith("prop-")) {//对维度-属性占比进行更新
			//TODO：需要对主观评价关联的客观评价及属性权重进行处理
//			ItemEvaluationMeasure itemEvaluationMeasure = itemEvaluationMeasureService.get(id.replace("prop-", ""));
//			parentEvaluation = itemEvaluationService.get(itemEvaluationMeasure.getEvaluation().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
//			itemEvaluationMeasure.setWeight(weight);
//			itemEvaluationMeasureService.save(itemEvaluationMeasure);
//			itemEvaluationMeasure = itemEvaluationMeasureService.get(id.replace("prop-", ""));
//			map.put("type", "属性");
//			map.put("name", itemEvaluationMeasure.getName());
//			map.put("weight", itemEvaluationMeasure.getWeight());
		}else {//出错了
			//do nothing
		}
		//更新所在维度节点的自动脚本
		if(parentEvaluation!=null)
			saveWithScript(parentEvaluation);
		else
			logger.warn("cannot find parent dimension.");
		return map;
	}

	//动态计算脚本并保存
	private void saveWithScript(ItemEvaluation itemEvaluation) {
		logger.error("try to save with script.[itemEvaluation.id]"+itemEvaluation.getId(),itemEvaluation);
		//预生成脚本：对于weighted-sum脚本，自动查询下级节点，并生成
		if( "auto".equalsIgnoreCase(itemEvaluation.getScriptType()) ) {
			//先获取关联的客观维度列表
			ItemEvaluationDimension itemEvaluationDimension = new ItemEvaluationDimension();
			itemEvaluationDimension.setEvaluation(itemEvaluation);
			itemEvaluationDimension.setDelFlag("0");
			List<ItemEvaluationDimension> measures = itemEvaluationDimensionService.findList(itemEvaluationDimension);
			String script = "";
			String scriptMemo = "";
			int index = 0;
			for(ItemEvaluationDimension measure:measures) {
				if(measure.getDimension().getPropKey()==null||measure.getDimension().getPropKey().trim().length()==0)
					continue;//未设置属性propKey则不作处理
				if(index>0) {
					script += "+";
					scriptMemo += "+";
				}
				double weight = measure.getWeight()*0.01;
				weight = (double) Math.round(weight * 100) / 100;//仅保留2位小数
				script += measure.getDimension().getPropKey()+ "*" + weight;
				scriptMemo += measure.getDimension().getName()+ "*" + weight;
				index++;
			}
			//然后获取下级主观维度列表
			ItemEvaluation query = new ItemEvaluation();
			query.setParent(itemEvaluation);
			query.setDelFlag("0");
			List<ItemEvaluation> subDimensions = itemEvaluationService.findList(query);
			index = 0;
			for(ItemEvaluation dimension:subDimensions) {
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
			itemEvaluation.setScript(script);
			itemEvaluation.setScriptMemo(scriptMemo);
		}
		itemEvaluationService.save(itemEvaluation);
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
//		List<ItemEvaluation> list = Lists.newArrayList();
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
			rootDimension.setWeight(100);
			rootDimension.setFeatured(false);
			rootDimension.setPropKey("e"+Util.get6bitCode(category.getName()));
			rootDimension.setType("ignore");
			rootDimension.setScript("no-script");
			rootDimension.setScriptType("auto");
			rootDimension.setScriptMemo("加权汇总");
			itemEvaluationService.save(rootDimension);
			root = itemEvaluationService.findList(q).get(0);
			//ROOT下建立默认顶级节点：
			//String[] parentNodes = {"效益","成本","约束","风格"};
			//String[] parentDesc = {"能够带来的效益","需要的成本","时间空间限制等","适用情境以及设计风格"};
			//----效益（生存、安全、情感、尊重、价值）
			String[] nodes1 = {"生存需求","安全需求","情感需求","尊重需求","价值实现"};
			String[] descs1 = {"满足功能及可用性需求","满足安全、持续及保障可用需求","购买过程及使用过程服务需求","品牌及用户群体等","代言及形象投射"};
			String[] scripts1 = {"weighted-sum","weighted-sum","weighted-sum","weighted-sum","weighted-sum"};
			String[] types1 = {"a","b","c","d","e"};
			createDefaultNodes(root,50,"效益","能够带来的收益",100,nodes1,descs1,scripts1,types1,category);
			//----成本（经济、社会、文化）、
			String[] nodes2 = {"经济","社会","文化"};
			String[] descs2 = {"对经济的要求","对社会资源的要求","对文化方面的要求"};
			String[] scripts2 = {"weighted-sum","weighted-sum","weighted-sum"};
			String[] types2 = {"x","y","z"};
			createDefaultNodes(root,30,"成本","使用时需要付出的成本",200,nodes2,descs2,scripts2,types2,category);
			//----约束（时间、空间）、
			String[] nodes3 = {"时间","空间"};
			String[] descs3 = {"可用时间","可用的范围"};
			String[] scripts3 = {"script","script"};
			String[] types3 = {"when","where"};
			createDefaultNodes(root,10,"约束","使用限制",300,nodes3,descs3,scripts3,types3,category);
			//----其他（情境满足度、偏好满足度）
			String[] nodes4 = {"需求满足度","情境满足度","风格偏好"};
			String[] descs4 = {"对需求的满足","对情境的满足","颜色款式设计等风格"};
			String[] scripts4 = {"system","system","script"};
			String[] types4 = {"demands","occasions","style"};
			createDefaultNodes(root,10,"偏好","风格偏好及满足度",400,nodes4,descs4,scripts4,types4,category);
		}
//		List<ItemEvaluation> sourcelist = itemEvaluationService.findTree(category);
//		ItemEvaluation.sortList(list, sourcelist, "1",true);
		
		List<JSONObject> list = findEvaluationAndDimension(category);
		
		model.addAttribute("list", list);
		model.addAttribute("treeId", treeId);
		return "modules/mod/itemEvaluationList";
	}
	

	/**
	 * 装载主观评价维度及关联的客观评价维度
	 * @param category
	 * @return
	 */
	private List<JSONObject> findEvaluationAndDimension(ItemCategory category){
		List<JSONObject> nodes = Lists.newArrayList();
		//先查出root evaluation
		ItemEvaluation rootEvaluation = itemEvaluationService.get(category.getId());
		if(rootEvaluation == null) {//如果根据cagtegory ID查询不存在，则查询该类目下ID为1的记录
			logger.debug("no root dimension found by category id.[category id]"+category.getId());
			ItemEvaluation parent = new ItemEvaluation();
			parent.setId("1");
			ItemEvaluation q = new ItemEvaluation();
			q.setCategory(category);
			q.setParent(parent);
			List<ItemEvaluation> roots = itemEvaluationService.findList(q);
			if(roots== null || roots.size()==0) {
				logger.debug("no root evaluation found by category and root=1.[category]"+category.getId());
				return nodes;
			}else {
				logger.debug("got root evaluation by category and id=1.[category]"+category.getId());
				rootEvaluation = roots.get(0);
			}
		}
		//添加本级节点
		JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(rootDimension);
		node.put("type", "dimension");
		node.put("id", rootEvaluation.getId());
		node.put("parent", rootEvaluation.getParent());
		node.put("category", category);
		node.put("name", rootEvaluation.getName());
		node.put("weight", rootEvaluation.getWeight());
		node.put("description", rootEvaluation.getDescription());
		node.put("propKey", rootEvaluation.getPropKey());
		node.put("featured", rootEvaluation.isFeatured());
		node.put("script", rootEvaluation.getScript());
		nodes.add(node);
		//递归遍历子节点
		loadEvaluationAndDimensionCascade(category,rootEvaluation,nodes);
		return nodes;
	}
	
	//递归获取所有下级节点与关联属性
	private void loadEvaluationAndDimensionCascade(ItemCategory category,ItemEvaluation evaluation, List<JSONObject> nodes) {
		//查询所有下级节点
		ItemEvaluation q = new ItemEvaluation();
		q.setParent(evaluation);
		q.setCategory(category);
		List<ItemEvaluation> evaluations = itemEvaluationService.findList(q);
		for(ItemEvaluation item:evaluations) {
			//添加本级节点
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(item);
			node.put("type", "evaluation");
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
			loadEvaluationAndDimensionCascade(category,item,nodes);//递归遍历
		}
		
		//查询所有关联的客观评价
		ItemEvaluationDimension evaluationDimension = new ItemEvaluationDimension();
		evaluationDimension.setEvaluation(evaluation);
		List<ItemEvaluationDimension> evaluationDimensions = itemEvaluationDimensionService.findList(evaluationDimension);
		for(ItemEvaluationDimension item:evaluationDimensions) {
			ItemDimension dimension = itemDimensionService.get(item.getDimension().getId());
			//添加客观评价
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(item);
			node.put("type", "dimension");
			node.put("id", item.getId());
			node.put("parent", evaluation);//将当前evaluation节点作为parent设置
			node.put("category", category);
			if(dimension==null) {
				node.put("name", "-"+item.getName());//表示dimension在建立后被删除
			}else {
				node.put("name", "△"+dimension.getName());
			}
			node.put("weight", item.getWeight());
			node.put("featured", false);
			node.put("description", item.getDescription());
			node.put("propKey", dimension==null?"":dimension.getPropKey());
			nodes.add(node);
		}
		
		//查询所有关联属性
		/**
		//TODO：当前主观评价未考虑对属性直接进行。待定。
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
		//**/
	}

	
	private void createDefaultNodes(ItemEvaluation root,double weight,String parent,String parentDesc,int parentSort,String[] nodes,String[] nodeDesc,String[] nodeScript,String[] types, ItemCategory category) {
		//创建父节点
		String id = IdGen.uuid();
		ItemEvaluation parentNode = new ItemEvaluation();
		//parentNode.setId(id);
		parentNode.setParent(root);
		parentNode.setName(parent);
		parentNode.setSort(parentSort);
		parentNode.setDescription(parentDesc);
		parentNode.setCategory(category);
		parentNode.setWeight(weight);
		parentNode.setFeatured(false);
		parentNode.setType("ignore");
		parentNode.setPropKey("e"+Util.get6bitCode(parent));
		parentNode.setScript("no-script");
		parentNode.setScriptMemo("");
		itemEvaluationService.save(parentNode);
		//query node
		ItemEvaluation queryNode = new ItemEvaluation();
		queryNode.setName(parent);
		queryNode.setDescription(parentDesc);
		queryNode.setCategory(category);
		queryNode.setType("ignore");
		queryNode.setScript("no-script");
		
		if(itemEvaluationService.findList(queryNode).size()>0)
			parentNode = itemEvaluationService.findList(queryNode).get(0);//得到已建立的根节点
		else
			parentNode = root;//否则直接放到根节点下
		//创建子节点
		int i=10,k=0;
		for(String node:nodes){
			ItemEvaluation evalNode = new ItemEvaluation();
			evalNode.setParent(parentNode);
			evalNode.setName(node);
			evalNode.setPropKey("e"+Util.get6bitCode(node));
			evalNode.setSort(i);
			evalNode.setDescription(nodeDesc[k]);
			evalNode.setCategory(category);
			evalNode.setWeight(100/nodes.length);
			//避免100/3的情况，对最后一个节点进行修正
			if(k==nodes.length-1) {
				evalNode.setWeight(100-100/nodes.length*(nodes.length-1));
			}
			evalNode.setFeatured(true);
			evalNode.setType(types[k]);
			evalNode.setScript(nodeScript[k]);
			evalNode.setScriptMemo(nodeScript[k]);//默认节点与script相同
			itemEvaluationService.save(evalNode);
			i += 10;
			k++;
		}
	}
	
	//根据category获取相应的主观评价树。
	//自动根据category及parentId=1查询得到根节点
	@ResponseBody
	@RequestMapping(value = "rest/dim-tree-by-category", method = RequestMethod.GET)
	public List<Map<String, Object>> listDimensionTreeForSunburstChartByCategory(String categoryId) {
		ItemCategory category = itemCategoryService.get(categoryId);
		ItemEvaluation root = new ItemEvaluation(); 
		root.setId("1");//指定该目录下ID为1的记录为根节点
		ItemEvaluation q = new ItemEvaluation(); 
		q.setCategory(category);
		q.setParent(root);
		
		List<ItemEvaluation> nodes = itemEvaluationService.findList(q);
		if(nodes !=null && nodes.size()>0)
			return listDimensionTreeForSunburstChart(categoryId,nodes.get(0).getId());
		else {//否则尝试查询root.id=categoryId的记录
			return listDimensionTreeForSunburstChart(categoryId,categoryId);
		}
	}

	/**
	 * 获取指定节点下的维度树。用于主观评价图形化显示。sunburst。
	 * 输入为父维度ID（顶级维度与所属类目ID相同），输出为所有子维度name及weight。嵌套输出。
	 */
	@ResponseBody
	@RequestMapping(value = "rest/dim-tree", method = RequestMethod.GET)
	public List<Map<String, Object>> listDimensionTreeForSunburstChart(String categoryId,String parentId) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		ItemEvaluation parentDimension = itemEvaluationService.get(parentId);//以当前维度为父节点查询
		ItemCategory category = itemCategoryService.get(categoryId);
		ItemEvaluation q = new ItemEvaluation(); 
		q.setParent(parentDimension);
		q.setCategory(category);
		for(ItemEvaluation node:itemEvaluationService.findList(q)) {//组装dimension节点列表
			Map<String, Object> map = Maps.newHashMap();
			map.put("name", node.getName());
			map.put("weight", node.getWeight());
			map.put("children",listDimensionTreeForSunburstChart(node.getCategory().getId(),node.getId()));//迭代获取所有下级维度
			mapList.add(map);
		}
		//获取关联的客观评价节点
		ItemEvaluationDimension evaluationDimension = new ItemEvaluationDimension();
		evaluationDimension.setEvaluation(parentDimension);
		List<ItemEvaluationDimension> evaluationDimensions = itemEvaluationDimensionService.findList(evaluationDimension);
		for(ItemEvaluationDimension item:evaluationDimensions) {
			//需要判定dimension是否存在
			ItemDimension dimension = itemDimensionService.get(item.getDimension());
			//添加属性
			Map<String, Object> node = Maps.newHashMap();
			if(dimension==null) {
				node.put("name", "-"+item.getName());//表示measure在建立后被删除
			}else {
				node.put("name", "△"+dimension.getName());
			}
			node.put("weight", item.getWeight());
			mapList.add(node);
		}
		/**
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
		//**/
		return mapList;
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
        		if(rootDimensions.size()>0) {
	        		ItemEvaluation root = rootDimensions.get(0);
	        		itemEvaluation.setParent(root);
        		}
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
		//itemEvaluation.setFeatured(false);
		//检查默认key值，如果没有则随机设置
		if(itemEvaluation.getPropKey()==null || itemEvaluation.getPropKey().trim().length()==0)
			itemEvaluation.setPropKey("e"+Util.get6bitCode(itemEvaluation.getName()));//以p打头的7位字符串，大小写区分。保存时如果重复将报错
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
		//更新脚本
		if(itemEvaluation.getId() != null && itemEvaluation.getId().trim().length()>0)
			saveWithScript(itemEvaluation);
		//递归更新父节点
		if(itemEvaluation.getParent()!=null && itemEvaluation.getParent().getId()!=null)
			saveWithScript(itemEvaluation.getParent());
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