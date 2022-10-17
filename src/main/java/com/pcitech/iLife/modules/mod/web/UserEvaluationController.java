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
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.mod.entity.UserEvaluationDimension;
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.mod.entity.UserEvaluationDimension;
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.mod.service.UserDimensionService;
import com.pcitech.iLife.modules.mod.service.UserEvaluationDimensionService;
import com.pcitech.iLife.modules.mod.service.UserEvaluationService;
import com.pcitech.iLife.util.Util;

/**
 * 用户主观评价Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userEvaluation")
public class UserEvaluationController extends BaseController {

	@Autowired
	private UserEvaluationService userEvaluationService;
	
	@Autowired
	private UserDimensionService userDimensionService;
	
	@Autowired
	private UserEvaluationDimensionService userEvaluationDimensionService;
	
	@ModelAttribute
	public UserEvaluation get(@RequestParam(required=false) String id) {
		UserEvaluation entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userEvaluationService.get(id);
		}
		if (entity == null){
			entity = new UserEvaluation();
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
	public List<UserEvaluation> listFeaturedDimension() {
		UserEvaluation q = new UserEvaluation(); 
		UserEvaluation root = new UserEvaluation(); 
		root.setId("0");
		//q.setParent(root);
		q.setFeatured(true);//仅返回featured节点
		List<UserEvaluation> result = userEvaluationService.findList(q);
		List<UserEvaluation> list = Lists.newArrayList();
		for(UserEvaluation item:result) {
			if(",a,b,c,d,e,x,y,z,alpha,beta,gamma,delte,epsilon,eta,zeta,theta".indexOf(","+item.getType())>-1) {
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
	public List<UserEvaluation> listFeaturedEvaluation() {
		UserEvaluation q = new UserEvaluation(); 
		UserEvaluation root = new UserEvaluation(); 
		root.setId("0");
		q.setParent(root);
		q.setFeatured(true);//仅返回featured节点
		return userEvaluationService.findList(q);
	}

	@ResponseBody
	@RequestMapping(value = "rest/weight", method = RequestMethod.POST)
	//更新维度占比 或者 维度下属性 占比。注意：需要根据id类型进行区分
	public Map<String, Object> updateWeightsByMeasureId( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double weight,  
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		UserEvaluation parentEvaluation = null;
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", id);//保持原始id
		if(id.startsWith("eval-")) {//对主观评价维度占比进行更新
			UserEvaluation userEvaluation = userEvaluationService.get(id.replace("eval-", ""));
			parentEvaluation = userEvaluationService.get(userEvaluation.getParent().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
			userEvaluation.setWeight(weight);
			userEvaluationService.save(userEvaluation);
			userEvaluation = userEvaluationService.get(id.replace("eval-", ""));
			map.put("type", "主观评价");
			map.put("name", userEvaluation.getName());
			map.put("weight", userEvaluation.getWeight());
		}else if(id.startsWith("dim-")) {//对客观评价占比进行更新
			UserEvaluationDimension userEvaluationDimension = userEvaluationDimensionService.get(id.replace("dim-", ""));
			parentEvaluation = userEvaluationService.get(userEvaluationDimension.getEvaluation().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
			userEvaluationDimension.setWeight(weight);
			userEvaluationDimensionService.save(userEvaluationDimension);
			userEvaluationDimension = userEvaluationDimensionService.get(id.replace("dim-", ""));
			map.put("type", "客观评价");
			map.put("name", userEvaluationDimension.getName());
			map.put("weight", userEvaluationDimension.getWeight());
		}else if(id.startsWith("prop-")) {//对维度-属性占比进行更新
			//TODO：需要对主观评价关联的客观评价及属性权重进行处理
//			UserEvaluationMeasure userEvaluationMeasure = userEvaluationMeasureService.get(id.replace("prop-", ""));
//			parentEvaluation = userEvaluationService.get(userEvaluationMeasure.getEvaluation().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
//			userEvaluationMeasure.setWeight(weight);
//			userEvaluationMeasureService.save(userEvaluationMeasure);
//			userEvaluationMeasure = userEvaluationMeasureService.get(id.replace("prop-", ""));
//			map.put("type", "属性");
//			map.put("name", userEvaluationMeasure.getName());
//			map.put("weight", userEvaluationMeasure.getWeight());
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
	private void saveWithScript(UserEvaluation userEvaluation) {
		logger.error("try to save with script.[userEvaluation.id]"+userEvaluation.getId(),userEvaluation);
		//预生成脚本：对于weighted-sum脚本，自动查询下级节点，并生成
//		if(!userEvaluation.getIsNewRecord() && userEvaluation.getId()!=null && userEvaluation.getId().trim().length()>0 ) {
			//先获取关联的客观维度列表
			UserEvaluationDimension userEvaluationDimension = new UserEvaluationDimension();
			userEvaluationDimension.setEvaluation(userEvaluation);
			userEvaluationDimension.setDelFlag("0");
			List<UserEvaluationDimension> measures = userEvaluationDimensionService.findList(userEvaluationDimension);
			String script = "";
			String scriptMemo = "";
			int index = 0;
			for(UserEvaluationDimension measure:measures) {
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
			UserEvaluation query = new UserEvaluation();
			query.setParent(userEvaluation);
			query.setDelFlag("0");
			List<UserEvaluation> subDimensions = userEvaluationService.findList(query);
			index = 0;
			for(UserEvaluation dimension:subDimensions) {
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
			userEvaluation.setScript(script);
			userEvaluation.setScriptMemo(scriptMemo);
//		}
		userEvaluationService.save(userEvaluation);
	}
	
	/**
	@RequiresPermissions("mod:userEvaluation:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserEvaluation userEvaluation, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<UserEvaluation> list = userEvaluationService.findList(userEvaluation); 
		model.addAttribute("list", list);
		return "modules/mod/userEvaluationList";
	}
	//**/
	@RequiresPermissions("mod:userEvaluation:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserEvaluation userEvaluation,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
//		List<UserEvaluation> list = Lists.newArrayList();
		//直接查询顶级节点下的所有子节点
		UserEvaluation root = new UserEvaluation();
		root.setId("0");
		UserEvaluation q = new UserEvaluation(); 
		q.setParent(root);
		List<UserEvaluation> rootNodes = userEvaluationService.findList(q);//查询所有子节点
		
		List<JSONObject> list = findEvaluationAndDimension(rootNodes);
		
		model.addAttribute("list", list);
		model.addAttribute("treeId", treeId);
		return "modules/mod/userEvaluationList";
	}
	

	/**
	 * 装载主观评价维度及关联的客观评价维度
	 * @param category
	 * @return
	 */
	private List<JSONObject> findEvaluationAndDimension(List<UserEvaluation> rootNodes){
		List<JSONObject> nodes = Lists.newArrayList();
		for(UserEvaluation rootEvaluation:rootNodes) {
			//添加本级节点
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(rootDimension);
			node.put("type", "evaluation");
			node.put("id", rootEvaluation.getId());
			node.put("parent", rootEvaluation.getParent());
			node.put("category", rootEvaluation.getCategory());
			node.put("name", rootEvaluation.getName());
			node.put("weight", rootEvaluation.getWeight());
			node.put("description", rootEvaluation.getDescription());
			node.put("propKey", rootEvaluation.getPropKey());
			node.put("featured", rootEvaluation.isFeatured());
			node.put("script", rootEvaluation.getScript());
			node.put("scriptMemo", rootEvaluation.getScriptMemo());
			nodes.add(node);
			//递归遍历子节点
			loadEvaluationAndDimensionCascade(rootEvaluation,nodes);
		}
		return nodes;
	}
	
	//递归获取所有下级节点与关联属性
	private void loadEvaluationAndDimensionCascade(UserEvaluation evaluation, List<JSONObject> nodes) {
		//查询所有下级节点
		UserEvaluation q = new UserEvaluation();
		q.setParent(evaluation);
		List<UserEvaluation> evaluations = userEvaluationService.findList(q);
		for(UserEvaluation item:evaluations) {
			//添加本级节点
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(item);
			node.put("type", "evaluation");
			node.put("id", item.getId());
			node.put("parent", item.getParent());
			node.put("category", item.getCategory());
			node.put("name", item.getName());
			node.put("weight", item.getWeight());
			node.put("description", item.getDescription());
			node.put("propKey", item.getPropKey());
			node.put("featured", item.isFeatured());
			node.put("script", item.getScript());
			node.put("scriptMemo", item.getScriptMemo());
			nodes.add(node);
			loadEvaluationAndDimensionCascade(item,nodes);//递归遍历
		}
		
		//查询所有关联的客观评价
		UserEvaluationDimension evaluationDimension = new UserEvaluationDimension();
		evaluationDimension.setEvaluation(evaluation);
		List<UserEvaluationDimension> evaluationDimensions = userEvaluationDimensionService.findList(evaluationDimension);
		for(UserEvaluationDimension item:evaluationDimensions) {
			UserDimension dimension = userDimensionService.get(item.getDimension().getId());
			//添加客观评价
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(item);
			node.put("type", "dimension");
			node.put("id", item.getId());
			node.put("parent", evaluation);//将当前evaluation节点作为parent设置
			node.put("category", item.getCategory());
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
		UserDimensionMeasure dimensionMeasure = new UserDimensionMeasure();
		dimensionMeasure.setDimension(dimension);
		List<UserDimensionMeasure> dimensionMeasures = userDimensionMeasureService.findList(dimensionMeasure);
		for(UserDimensionMeasure item:dimensionMeasures) {
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
	
	//根据category获取相应的主观评价树。
	//自动根据category及parentId=1查询得到根节点
	@ResponseBody
	@RequestMapping(value = "rest/dim-tree-by-category", method = RequestMethod.GET)
	public List<Map<String, Object>> listDimensionTreeForSunburstChartByCategory() {
		UserEvaluation root = new UserEvaluation(); 
		root.setId("0");//指定从根节点开始查
		UserEvaluation q = new UserEvaluation(); 
		q.setParent(root);
		
		List<UserEvaluation> nodes = userEvaluationService.findList(q);
		if(nodes !=null && nodes.size()>0)
			return listDimensionTreeForSunburstChart(nodes.get(0).getId());
		else {//否则尝试查询root.id=categoryId的记录
			return listDimensionTreeForSunburstChart("0");
		}
	}

	/**
	 * 获取指定节点下的维度树。用于主观评价图形化显示。sunburst。
	 * 输入为父维度ID（顶级维度与所属类目ID相同），输出为所有子维度name及weight。嵌套输出。
	 */
	@ResponseBody
	@RequestMapping(value = "rest/dim-tree", method = RequestMethod.GET)
	public List<Map<String, Object>> listDimensionTreeForSunburstChart(String parentId) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		UserEvaluation parent = new UserEvaluation();
		parent.setId(parentId);
		UserEvaluation q = new UserEvaluation(); 
		q.setParent(parent);
		for(UserEvaluation node:userEvaluationService.findList(q)) {//组装dimension节点列表
			Map<String, Object> map = Maps.newHashMap();
			map.put("name", node.getName());
			map.put("weight", node.getWeight());
			map.put("children",listDimensionTreeForSunburstChart(node.getId()));//迭代获取所有下级维度
			mapList.add(map);
		}
		//获取关联的客观评价节点
		UserEvaluationDimension evaluationDimension = new UserEvaluationDimension();
		evaluationDimension.setEvaluation(parent);
		List<UserEvaluationDimension> evaluationDimensions = userEvaluationDimensionService.findList(evaluationDimension);
		for(UserEvaluationDimension item:evaluationDimensions) {
			//需要判定dimension是否存在
			UserDimension dimension = userDimensionService.get(item.getDimension());
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
		UserDimensionMeasure dimensionMeasure = new UserDimensionMeasure();
		dimensionMeasure.setDimension(parentDimension);
		List<UserDimensionMeasure> dimensionMeasures = userDimensionMeasureService.findList(dimensionMeasure);
		for(UserDimensionMeasure item:dimensionMeasures) {
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
	@RequiresPermissions("mod:userEvaluation:view")
	@RequestMapping(value = "form")
	public String form(UserEvaluation userEvaluation, Model model) {
		if (userEvaluation.getParent()!=null && StringUtils.isNotBlank(userEvaluation.getParent().getId())){
			userEvaluation.setParent(userEvaluationService.get(userEvaluation.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(userEvaluation.getId())){
				UserEvaluation userEvaluationChild = new UserEvaluation();
				userEvaluationChild.setParent(new UserEvaluation(userEvaluation.getParent().getId()));
				List<UserEvaluation> list = userEvaluationService.findList(userEvaluation); 
				if (list.size() > 0){
					userEvaluation.setSort(list.get(list.size()-1).getSort());
					if (userEvaluation.getSort() != null){
						userEvaluation.setSort(userEvaluation.getSort() + 30);
					}
				}
			}
		}
		if (userEvaluation.getSort() == null){
			userEvaluation.setSort(30);
		}
		if(userEvaluation.getPropKey()==null || userEvaluation.getPropKey().trim().length()==0)
			userEvaluation.setPropKey("e"+Util.get6bitCode("ue"+userEvaluation.getName()));//以e打头的7位字符串，大小写区分。保存时如果重复将报错
		
		model.addAttribute("userEvaluation", userEvaluation);
		return "modules/mod/userEvaluationForm";
	}

	@RequiresPermissions("mod:userEvaluation:edit")
	@RequestMapping(value = "save")
	public String save(UserEvaluation userEvaluation, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userEvaluation)){
			return form(userEvaluation, model);
		}
		userEvaluationService.save(userEvaluation);
		//递归更新父节点
		if(userEvaluation.getParent()!=null && userEvaluation.getParent().getId()!=null)
			saveWithScript(userEvaluation.getParent());
		addMessage(redirectAttributes, "保存用户主观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluation/?repage";
	}
	
	@RequiresPermissions("mod:userEvaluation:edit")
	@RequestMapping(value = "delete")
	public String delete(UserEvaluation userEvaluation, RedirectAttributes redirectAttributes) {
		userEvaluationService.delete(userEvaluation);
		addMessage(redirectAttributes, "删除用户主观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluation/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserEvaluation> list = userEvaluationService.findList(new UserEvaluation());
		for (int i=0; i<list.size(); i++){
			UserEvaluation e = list.get(i);
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
	
}