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
import org.springframework.web.bind.annotation.PathVariable;
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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.entity.UserDimensionMeasure;
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.entity.UserDimensionMeasure;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.service.UserDimensionMeasureService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.mod.service.UserDimensionMeasureService;
import com.pcitech.iLife.modules.mod.service.UserDimensionService;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;
import com.pcitech.iLife.util.Util;

/**
 * 用户客观评价Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userDimension")
public class UserDimensionController extends BaseController {

	@Autowired
	private UserDimensionService userDimensionService;
	@Autowired
	private UserMeasureService measureService;
	@Autowired
	private UserDimensionMeasureService userDimensionMeasureService;
	
	@ModelAttribute
	public UserDimension get(@RequestParam(required=false) String id) {
		UserDimension entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userDimensionService.get(id);
		}
		if (entity == null){
			entity = new UserDimension();
		}
		return entity;
	}

	@RequiresPermissions("mod:userDimension:view")
	@RequestMapping(value = "form")
	public String form(UserDimension userDimension, Model model) {
		if (userDimension.getParent()!=null && StringUtils.isNotBlank(userDimension.getParent().getId())){
			userDimension.setParent(userDimensionService.get(userDimension.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(userDimension.getId())){
				UserDimension userDimensionChild = new UserDimension();
				userDimensionChild.setParent(new UserDimension(userDimension.getParent().getId()));
				List<UserDimension> list = userDimensionService.findList(userDimension); 
				if (list.size() > 0){
					userDimension.setSort(list.get(list.size()-1).getSort());
					if (userDimension.getSort() != null){
						userDimension.setSort(userDimension.getSort() + 30);
					}
				}
			}
		}
		if (userDimension.getSort() == null){
			userDimension.setSort(30);
		}
		if(userDimension.getPropKey()==null || userDimension.getPropKey().trim().length()==0)
			userDimension.setPropKey("m"+Util.get6bitCode("um"+userDimension.getName()));//以e打头的7位字符串，大小写区分。保存时如果重复将报错
		
		model.addAttribute("userDimension", userDimension);
		return "modules/mod/userDimensionForm";
	}

	@RequiresPermissions("mod:userDimension:edit")
	@RequestMapping(value = "save")
	public String save(UserDimension userDimension, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userDimension)){
			return form(userDimension, model);
		}
		userDimensionService.save(userDimension);
		addMessage(redirectAttributes, "保存用户客观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userDimension/?repage";
	}
	
	@RequiresPermissions("mod:userDimension:edit")
	@RequestMapping(value = "delete")
	public String delete(UserDimension userDimension, RedirectAttributes redirectAttributes) {
		userDimensionService.delete(userDimension);
		addMessage(redirectAttributes, "删除用户客观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userDimension/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserDimension> list = userDimensionService.findList(new UserDimension());
		for (int i=0; i<list.size(); i++){
			UserDimension e = list.get(i);
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
	

	//获取客观评价树。
	//自动根据category及parentId=1查询得到根节点
	@ResponseBody
	@RequestMapping(value = "rest/dim-tree-by-category", method = RequestMethod.GET)
	public List<Map<String, Object>> listDiemensionTreeForSunburstChart(String categoryId) {
		UserDimension root = new UserDimension(); 
		root.setId("1");//指定该目录下ID为1的记录为根节点
		UserDimension q = new UserDimension(); 
		q.setParent(root);
		
		List<UserDimension> nodes = userDimensionService.findList(q);
		if(nodes !=null && nodes.size()>0)
			return listDiemensionTreeForSunburstChart(categoryId,nodes.get(0).getId());
		else {//否则尝试查询root.id=categoryId的记录
			return listDiemensionTreeForSunburstChart(categoryId,categoryId);
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
		UserDimension parentDimension = userDimensionService.get(parentId);//以当前维度为父节点查询
		UserDimension q = new UserDimension(); 
		q.setParent(parentDimension);
		for(UserDimension node:userDimensionService.findList(q)) {//组装dimension节点列表
			Map<String, Object> map = Maps.newHashMap();
			map.put("name", node.getName());
			map.put("weight", node.getWeight());
			map.put("children",listDiemensionTreeForSunburstChart(categoryId,node.getId()));//迭代获取所有下级维度
			mapList.add(map);
		}
		//获取关联的属性节点
		UserDimensionMeasure dimensionMeasure = new UserDimensionMeasure();
		dimensionMeasure.setDimension(parentDimension);
		List<UserDimensionMeasure> dimensionMeasures = userDimensionMeasureService.findList(dimensionMeasure);
		for(UserDimensionMeasure item:dimensionMeasures) {
			//需要判定measure是否是继承得到
			UserMeasure measure = measureService.get(item.getMeasure());
			//添加属性
			Map<String, Object> node = Maps.newHashMap();
			if(measure==null) {
				node.put("name", "-"+item.getName());//表示measure在建立后被删除
			}else if(measure.getCategory().getId().equalsIgnoreCase(categoryId)) {//是继承属性
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
	public List<UserDimension> listFeaturedDimensionByCategoryId(String categoryId) {
		UserDimension q = new UserDimension(); 
		q.setFeatured(true);//仅返回featured节点
		return userDimensionService.findList(q);
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
			UserDimension parentDimension = userDimensionService.get(id.replace("dim-", ""));//以当前维度为父节点查询
			UserDimension q = new UserDimension(); 
			q.setParent(parentDimension);
			for(UserDimension node:userDimensionService.findList(q)) {//组装dimension节点列表
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
			//仅有一个分类，不支持
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
			UserDimension dimension = userDimensionService.get(id.replace("dim-",""));
			UserDimension q = new UserDimension();
			q.setParent(dimension);
			for(UserDimension node:userDimensionService.findList(q)) {//查询子维度列表
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", "dim-"+node.getId());//对于维度节点使用 dim- 前缀进行区分
				map.put("type", node.getType());//"维度");
				map.put("name", node.getName());
				map.put("weight", node.getWeight());
				mapList.add(map);
			}
			//查询 维度-属性 并添加
			UserDimensionMeasure userDimensionMeasure = new UserDimensionMeasure();
			userDimensionMeasure.setDimension(dimension);
			for(UserDimensionMeasure node:userDimensionMeasureService.findList(userDimensionMeasure)) {//查询维度下的属性列表
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", "prop-"+node.getId());//对于维度节点使用 prop- 前缀进行区分
				map.put("type", dimension.getType());//"属性");
				map.put("name", node.getName()+"-"+node.getMeasure().getName());
				map.put("weight", node.getWeight());
				mapList.add(map);
			}
		}else {//是category，则直接加载level1 维度列表
			//添加维度节点定义：根据分类ID查找对应分类下的维度定义节点
			//do nothing
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
		UserDimension parentDimension = null;
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", id);//保持原始id
		if(id.startsWith("dim-")) {//对维度占比进行更新
			UserDimension userDimension = userDimensionService.get(id.replace("dim-", ""));
			parentDimension = userDimensionService.get(userDimension.getParent().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
			userDimension.setWeight(weight);
			userDimensionService.save(userDimension);
			userDimension = userDimensionService.get(id.replace("dim-", ""));
			map.put("type", userDimension.getType());//"维度");
			map.put("name", userDimension.getName());
			map.put("weight", userDimension.getWeight());
		}else if(id.startsWith("prop-")) {//对维度-属性占比进行更新
			UserDimensionMeasure userDimensionMeasure = userDimensionMeasureService.get(id.replace("prop-", ""));
			parentDimension = userDimensionService.get(userDimensionMeasure.getDimension().getId());//重要：需要重新获取，通过getParent获取的仅包含ID及name
			userDimensionMeasure.setWeight(weight);
			userDimensionMeasureService.save(userDimensionMeasure);
			userDimensionMeasure = userDimensionMeasureService.get(id.replace("prop-", ""));
			map.put("type", parentDimension.getType());//"属性");
			map.put("name", userDimensionMeasure.getName());
			map.put("weight", userDimensionMeasure.getWeight());
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
	

	//动态计算脚本并保存
	private void saveWithScript(UserDimension userDimension) {
		logger.error("try to save with script.[userDimension.id]"+userDimension.getId(),userDimension);
		//预生成脚本：对于weighted-sum脚本，自动查询下级节点，并生成
		if(!userDimension.getIsNewRecord() && userDimension.getId()!=null && userDimension.getId().trim().length()>0 //对于已经存在的节点进行。新节点无需处理
				&& userDimension.getScript()!=null && userDimension.getScript().trim().length()>0 && userDimension.getScript().indexOf("weighted-sum")>=0) {
			//先获取属性列表
			UserDimensionMeasure userDimensionMeasure = new UserDimensionMeasure();
			userDimensionMeasure.setDimension(userDimension);
			userDimensionMeasure.setDelFlag("0");
			List<UserDimensionMeasure> measures = userDimensionMeasureService.findList(userDimensionMeasure);
			String script = "";
			String scriptMemo = "//weighted-sum ";
			int index = 0;
			for(UserDimensionMeasure measure:measures) {
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
			UserDimension query = new UserDimension();
			query.setParent(userDimension);
			query.setDelFlag("0");
			List<UserDimension> subDimensions = userDimensionService.findList(query);
			index = 0;
			for(UserDimension dimension:subDimensions) {
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
			userDimension.setScript(script+"\n"+scriptMemo);
		}
		userDimensionService.save(userDimension);
	}
	
	@RequiresPermissions("mod:userDimension:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserDimension userDimension,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
//		List<UserDimension> list = Lists.newArrayList();
		//直接获取顶级节点的子节点
		List<UserDimension> rootNodes = userDimensionService.findList(userDimension); 
		
		List<JSONObject> list = findDimensionAndMeasure(rootNodes);
		model.addAttribute("list", list);
		model.addAttribute("treeId", treeId);
		return "modules/mod/userDimensionList";
	}
	
	/**
	 * 装载客观评价维度及关联的属性
	 * @param category
	 * @return
	 */
	private List<JSONObject> findDimensionAndMeasure(List<UserDimension> rootNodes){
		List<JSONObject> nodes = Lists.newArrayList();
		//添加所有一级节点
		for(UserDimension rootDimension:rootNodes) {
			//添加本级节点
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(rootDimension);
			node.put("type", rootDimension.getType());
			node.put("id", rootDimension.getId());
			node.put("parent", rootDimension.getParent());
			node.put("name", rootDimension.getName());
			node.put("weight", rootDimension.getWeight());
			node.put("description", rootDimension.getDescription());
			node.put("propKey", rootDimension.getPropKey());
			node.put("featured", rootDimension.isFeatured());
			node.put("script", rootDimension.getScript());
			nodes.add(node);
			//递归遍历子节点
			loadDimensionAndMeasureCascade(rootDimension,nodes);
		}
		return nodes;
	}
	
	//递归获取所有下级节点与关联属性
	private void loadDimensionAndMeasureCascade(UserDimension dimension, List<JSONObject> nodes) {
		//查询所有下级节点
		UserDimension q = new UserDimension();
		q.setParent(dimension);
		List<UserDimension> dimensions = userDimensionService.findList(q);
		for(UserDimension item:dimensions) {
			//添加本级节点
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(item);
			node.put("type", item.getType());
			node.put("id", item.getId());
			node.put("parent", item.getParent());
			node.put("name", item.getName());
			node.put("weight", item.getWeight());
			node.put("description", item.getDescription());
			node.put("propKey", item.getPropKey());
			node.put("featured", item.isFeatured());
			node.put("script", item.getScript());
			nodes.add(node);
			loadDimensionAndMeasureCascade(item,nodes);//递归遍历
		}
		//查询所有关联属性
		UserDimensionMeasure dimensionMeasure = new UserDimensionMeasure();
		dimensionMeasure.setDimension(dimension);
		List<UserDimensionMeasure> dimensionMeasures = userDimensionMeasureService.findList(dimensionMeasure);
		for(UserDimensionMeasure item:dimensionMeasures) {
			//需要判定measure是否是继承得到
			UserMeasure measure = measureService.get(item.getMeasure());
			//添加属性
			JSONObject node = new JSONObject();//(JSONObject)JSONObject.toJSON(item);
			node.put("type", dimension.getType());//"measure");
			node.put("id", item.getId());
			node.put("parent", dimension);//将dimension作为parent设置
			logger.debug("[measure.category.id]"+measure.getCategory().getId());
			if(measure==null) {
				node.put("name", "-"+item.getName());//表示measure在建立后被删除
			}else {
				node.put("name", "○"+measure.getName());
			}
			node.put("weight", item.getWeight());
			node.put("description", item.getDescription());
			node.put("propKey", measure.getProperty());
			nodes.add(node);
		}
	}
}