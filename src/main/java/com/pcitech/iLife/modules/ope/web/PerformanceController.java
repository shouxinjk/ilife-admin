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
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.DictMeta;
import com.pcitech.iLife.modules.mod.entity.DictValue;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.service.DictValueService;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.ope.entity.HumanMarkedValue;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.ope.service.HumanMarkedValueService;
import com.pcitech.iLife.modules.ope.service.PerformanceService;

/**
 * 标注Controller
 * @author chenci
 * @version 2017-09-28
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/performance")
public class PerformanceController extends BaseController {
	@Autowired
	private MeasureService measureService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private PerformanceService performanceService;
	@Autowired
	private HumanMarkedValueService humanMarkedValueService;
	@Autowired
	private DictValueService dictValueService;

	/**
	 * 根据categoryId及openid获取所有待标注属性值及字典值列表，其中categoryId为可选:
	 * 1，根据categoryId及openid获取待标注属性值
	 * 2，根据categoryId及openid获取待标注字典值
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/pending",method = RequestMethod.GET)
	public JSONObject listPendingValuesAndDicts( @RequestParam(required=true) String openid,  
			@RequestParam String categoryId,  
			@RequestParam int from,  
			@RequestParam int to) {
		JSONObject result = new JSONObject();
		result.put("success",false);
		
		if(openid==null||openid.trim().length()==0) {
			result.put("msg", "openid is required.");
			return result;
		}
		
		//组织查询参数
		Map<String,Object> params = Maps.newHashMap();
		params.put("openid", openid);
		params.put("from", from);
		params.put("to", to);
		
		//查询Category
		ItemCategory category = itemCategoryService.get(categoryId);
		if(category==null) {
			result.put("msg", "no itemCategory found by id."+categoryId);
		}else {
			params.put("categoryId", categoryId);
		}
		
		//根据category及openid查询待标注属性值
		result.put("performance", performanceService.findPendingList(params));
		
		//根据category及openid查询待标注字典值
		result.put("dict", dictValueService.findPendingList(params));
		
		result.put("success",true);
		return result;
	}
	
	/**
	 * 根据measureId及openid获取所有字典值列表，包含已经参与评分的记录:
	 * 1，根据measureId获取所有数值记录
	 * 2，根据measureId及openid获取所有已评分记录
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/values-with-score",method = RequestMethod.GET)
	public JSONObject listValuesWithScoreByMeasureAndOpenid( @RequestParam(required=true) String measureId,  
			@RequestParam String openid) {
		JSONObject result = new JSONObject();
		result.put("success",false);
		
		//查询Measure
		Measure measure = measureService.get(measureId);
		if(measure==null) {
			result.put("msg", "cannot find measure by id."+measureId);
			return result;
		}
		
		//根据measure查询所有performance
		Performance performance = new Performance();
		performance.setMeasure(measure);
		performance.setIsMarked(null);//重要：需要忽略是否已标注设置
		result.put("values", performanceService.findList(performance));
		
		//根据measure及openid查询所有humanMarkedDict
		HumanMarkedValue humanMarkedValue = new HumanMarkedValue();
		humanMarkedValue.setOpenid(openid);
		humanMarkedValue.setMeasure(measure);
		result.put("scores", humanMarkedValueService.findList(humanMarkedValue));
		
		result.put("success",true);
		return result;
	}
	
	//根据measureId及categoryId查询performance值
	@ResponseBody
	@RequestMapping(value = "rest/values/{measureId}/{categoryId}", method = RequestMethod.GET)
	//根据属性ID（系统标准属性ID）查询所有属性值，并进行标注。根据level等级、controlvalue倒序排列
	public List<Performance> listValuesByMeasureIdAndCategoryIdForJqGrid( @PathVariable String measureId,@PathVariable String categoryId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,String> params = Maps.newHashMap();
		params.put("categoryId", categoryId);
		params.put("measureId", measureId);
		return performanceService.findListByMeasureAndCategory(params);
	}
	
	//不工作。jsGrid单个记录更新时传递的是FormData
	/**
	@ResponseBody
	@RequestMapping(value = "rest/propvalue", method = RequestMethod.POST)
	//根据属性值ID更新指定数据值条目，传递参数包括id,level,controlValue
	public Map<String,String> updateValuesByMeasureId( @RequestBody Map<String,Object> params, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		params.put("updateDate", new Date());
		performanceService.updateControlValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "control value updated.");
		return result;
	}
	//**/
	@ResponseBody
	@RequestMapping(value = "rest/propvalue", method = RequestMethod.POST)
	//根据属性值ID更新指定数据值条目，传递参数包括id,level,controlValue
	public Performance updateValuesByMeasureId( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double controlValue, 
			@RequestParam(required=true) int level, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("level", level);
		params.put("controlValue", controlValue);
		params.put("updateDate", new Date());
		performanceService.updateControlValue(params);
		return performanceService.get(id);
//		Map<String,String> result = Maps.newHashMap();
//		result.put("result", "control value updated.");
//		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/updateMarkedValue")
	//更新属性值标注，包括level、markedvalue。自动添加更新日期
	public Map<String,String> updateMarkedValue( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double markedValue, 
			@RequestParam(required=true) int level, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("level", level);
		params.put("isReady", 0);//注意：实际将同时设置isReady=0，分析系统将自动读取
		params.put("markedValue", markedValue);
		params.put("updateDate", new Date());
		performanceService.updateMarkedValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "marked value updated.");
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "rest/updateControlValue")
	//更新属性值标注，包括level、controlvalue。自动添加更新日期
	public Map<String,String> updateControlValue( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double controlValue, 
			@RequestParam(required=true) int level, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("level", level);
		params.put("controlValue", controlValue);
		params.put("updateDate", new Date());
		performanceService.updateControlValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "control value updated.");
		return result;
	}
	
	@ModelAttribute
	public Performance get(@RequestParam(required=false) String id) {
		Performance entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = performanceService.get(id);
		}
		if (entity == null){
			entity = new Performance();
		}
		return entity;
	}
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = {"list"})
	public String list(Performance performance,String treeId,String pId,String treeModule,String topType,  HttpServletRequest request, HttpServletResponse response, Model model) {
		//如果指定属性则根据属性过滤，否则直接查询所有待标注记录
		if(treeModule.equals("measure")){
			performance.setMeasure(new Measure(treeId));
			performance.setCategory(new ItemCategory(pId));//增加类目过滤。注意：仅在选中节点为Measure时，其pId是CategoryId
		}
		performance.setIsMarked(1);//显示已标注记录
		Page<Performance> page = performanceService.findPage(new Page<Performance>(request, response), performance);
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);//记录当前选中的measureId
		model.addAttribute("pId", pId);//记录当前选中的categoryId
		model.addAttribute("pType", treeModule);
		return "modules/ope/performanceList";
	}
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = {"listPending", ""})
	public String listPending(Performance performance,String treeId,String pId,String treeModule,String topType,  HttpServletRequest request, HttpServletResponse response, Model model) {
		//如果指定属性则根据属性过滤，否则直接查询所有待标注记录
		if(treeModule.equals("measure")){
			performance.setMeasure(new Measure(treeId));
			performance.setCategory(new ItemCategory(pId));//增加类目过滤。注意：仅在选中节点为Measure时，其pId是CategoryId
		}
		performance.setIsMarked(0);//显示待标注记录
		Page<Performance> page = performanceService.findPage(new Page<Performance>(request, response), performance);
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);//记录当前选中的measureId
		model.addAttribute("pId", pId);//记录当前选中的categoryId
		model.addAttribute("pType", treeModule);
		return "modules/ope/performanceListPending";
	}

	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = "form")
	public String form(Performance performance,String treeId,String pId,String pType,  Model model) {
		Measure measure=new Measure();
		if(pType.equals("measure")){
			measure = measureService.get(treeId);//treeId记录当前选中的measureId
			performance.setMeasure(measure);
			performance.setCategory(itemCategoryService.get(pId));//pId记录当前选中的categoryId
			//model.addAttribute("categoryId", measure.getCategory().getId());//设置当前类目ID
		}else {//否则提示选择属性
			model.addAttribute("message","选择属性查看标注。");
			return "treeData/none";
		}
		model.addAttribute("treeId", treeId);//记录当前选中的measureId
		model.addAttribute("pId", pId);//记录当前选中的categoryId
		model.addAttribute("pType", pType);
		model.addAttribute("performance", performance);
		return "modules/ope/performanceForm";
	}

	@RequiresPermissions("ope:performance:edit")
	@RequestMapping(value = "save")
	public String save(Performance performance,String treeId,String pId,String pType,  Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, performance)){
			return form(performance,treeId,pId,pType,model);
		}
		if(performance.getMeasure() == null){//不知道为啥，前端传进来的measure信息丢失了，手动补一次
			performance.setMeasure(measureService.get(treeId));
		}
		if(performance.getCategory() == null){//不知道为啥，前端传进来的measure信息丢失了，手动补一次
			performance.setCategory(itemCategoryService.get(pId));
		}
		performance.setIsReady(0);//强制同步
		performanceService.save(performance);
		addMessage(redirectAttributes, "保存标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/performance/?treeId="+treeId+"&pId="+pId+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("ope:performance:edit")
	@RequestMapping(value = "delete")
	public String delete(Performance performance,String treeId,String pId,String pType,  RedirectAttributes redirectAttributes) {
		performanceService.delete(performance);
		addMessage(redirectAttributes, "删除标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/performance/?treeId="+treeId+"&pId="+pId+"&treeModule="+pType+"&repage";
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
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","ope/performance");
		model.addAttribute("title","关键属性");
		return "treeData/index";
	}
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","ope/performance");
		model.addAttribute("title","属性及分类");
		List<ItemCategory> itemCategoryTree = itemCategoryService.findTree();
		model.addAttribute("list", performanceService.getTree(itemCategoryTree));
		return "treeData/tree";
	}
	
	@RequiresPermissions("ope:performance:view")
	@RequestMapping(value = "none")
	public String none(HttpServletRequest request, HttpServletResponse response, Model model) {
		//默认直接查询所有待标注记录
		Performance performance = new Performance();
		performance.setIsMarked(0);//显示待标注记录
		Page<Performance> page = performanceService.findPage(new Page<Performance>(request, response), performance);
		model.addAttribute("page", page);
		return "modules/ope/performanceListPending";
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
				//查询该类别下的直接属性：属性定义及属性值均在当前类目下
				Measure query = new Measure();
				query.setCategory(e);
				List<Measure> props = measureService.findList(query);
				for(Measure prop:props) {
					Map<String, Object> leafNode = Maps.newHashMap();
					leafNode.put("id", prop.getId());
					leafNode.put("pId", e.getId());
					leafNode.put("name", "๏"+prop.getName());
					leafNode.put("icon","/${ctx}/static/images/icon/prop.png");//手动设置icon，当前未生效
					mapList.add(leafNode);
				}
			}
		}
		return mapList;
	}
	
	/**
	 * 根据 measureId查询原始值列表，用于辅助标注
	 * @param measureId
	 * @param json{
	 * 	categoryId:xxx, 类目ID，必选
	 * 	q:xxx 查询字符串。可选。空白查询全部
	 *  size: xxx 数量。可选。如果传递则进行限制，否则返回全部
	 * }
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/search/{measureId}", method = RequestMethod.POST)
	public JSONObject searchValues(@PathVariable String measureId, @RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		List<String> suggestions = Lists.newArrayList();
		result.put("data", suggestions);
		
		Performance query = new Performance();
		
		//检查设置关键属性
		Measure measure = measureService.get(measureId);
		if(measure == null) {
			result.put("msg", "measureId is required.");
			return result;
		}
		
		//检查设置类目
		ItemCategory itemCategory = itemCategoryService.get(json.getString("categoryId"));
		if(itemCategory == null) {
			result.put("msg", "the Dict is category specified.  categoryId is required.");
			return result;
		}
		query.setCategory(itemCategory);
		
		//设置搜索字符串
		if(json.getString("q")!=null && json.getString("q").trim().length()>0) {
			query.setOriginalValue(json.getString("q").trim());
		}
		
		//返回条数：注意数据库查询中直接得到全部，仅控制返回数量
		int size = -1;
		try {
			size = Integer.parseInt(json.getString("size"));
		}catch(Exception ex) {
			//do nothing
		}
		
		//查询
		List<Performance> performances = performanceService.findList(query);
		int count = 1;
		for(Performance performance:performances) {
			if(size>0 && count>size)
				break;
			suggestions.add(performance.getOriginalValue());
			count++;
		}
		
		result.put("success", true);
		result.put("data", suggestions);
		return result;
	}
}