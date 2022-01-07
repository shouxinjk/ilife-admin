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
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
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
			rootDimension.setWeight(100);
			rootDimension.setFeatured(false);
			rootDimension.setPropKey("e"+Util.get6bitCode(category.getName()));
			rootDimension.setType("ignore");
			rootDimension.setScript("no-script");
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
		List<ItemEvaluation> sourcelist = itemEvaluationService.findTree(category);
		ItemEvaluation.sortList(list, sourcelist, "1",true);
		model.addAttribute("list", list);
		model.addAttribute("treeId", treeId);
		return "modules/mod/itemEvaluationList";
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
			evalNode.setFeatured(true);
			evalNode.setType(types[k]);
			evalNode.setScript(nodeScript[k]);
			itemEvaluationService.save(evalNode);
			i += 10;
			k++;
		}
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