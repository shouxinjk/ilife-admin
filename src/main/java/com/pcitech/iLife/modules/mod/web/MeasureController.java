/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import com.pcitech.iLife.modules.mod.entity.DictValue;
import com.pcitech.iLife.modules.mod.entity.Hierarchy;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.TagCategory;
import com.pcitech.iLife.modules.mod.entity.Tags;
import com.pcitech.iLife.modules.mod.entity.UserCategory;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.entity.UserTagCategory;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.mod.service.TagCategoryService;
import com.pcitech.iLife.modules.mod.service.TagsService;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.util.Util;

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
	@Autowired
	private TagsService tagsService;
	@Autowired
	private TagCategoryService tagCategoryService;
	@Autowired
	private UserMeasureService userMeasureService;

	/**
	 * 根据categoryId加载所有measure列表，以进行VALS标注
	 * @param categoryId 分类ID
	 * @return List<Measure> 属性列表
	 */
	@ResponseBody
	@RequestMapping(value = "rest/category/{categoryId}", method = RequestMethod.GET)
	//根据分类ID，查询该分下所有关键属性
	public List<Measure> listValuesByMeasureId( @PathVariable String categoryId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		return measureService.findByCategory(categoryId);
	}
	
	/**
	 * 更新属性VALS标注
	 * @param id
	 * @param alpha
	 * @param beta
	 * @param gamma
	 * @param delte
	 * @param epsilon
	 * @param zeta
	 * @param eta
	 * @param theta
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/propvals", method = RequestMethod.POST)
	//根据属性ID更新VALS
	public Measure updateValuesByMeasureId( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double percentage, 
			@RequestParam(required=true) double alpha, 
			@RequestParam(required=true) double beta, 
			@RequestParam(required=true) double gamma, 
			@RequestParam(required=true) double delte, 
			@RequestParam(required=true) double epsilon, 
			@RequestParam(required=true) double zeta, 
			@RequestParam(required=true) double eta, 
			@RequestParam(required=true) double theta, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Measure m = measureService.get(id);
		m.setPercentage(percentage);
		m.setAlpha(alpha);
		m.setBeta(beta);
		m.setGamma(gamma);
		m.setDelte(delte);
		m.setEpsilon(epsilon);
		m.setZeta(zeta);
		m.setEta(eta);
		m.setTheta(theta);
		m.setUpdateDate(new Date());
		measureService.save(m);
		return measureService.get(id);
	}
	
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
		//检查默认key值，如果没有则随机设置
		if(measure.getProperty()==null || measure.getProperty().trim().length()==0)
			measure.setProperty("p"+Util.get6bitCode(measure.getName()));//以p打头的7位字符串，大小写区分。保存时如果重复将报错
		model.addAttribute("measure", measure);
		model.addAttribute("treeId", measure.getCategory().getId());
		return "modules/mod/measureForm";
	}

	@RequiresPermissions("mod:measure:edit")
	@RequestMapping(value = "save")
	public String save(Measure measure, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, measure)){
			return form(measure, model);
		}
		measureService.save(measure);
		//将tag分别建立为主题
		if(measure.getTags()!=null&&measure.getTags().trim().length()>0) {
			saveTags(measure);
		}
		addMessage(redirectAttributes, "保存商品属性成功");
		return "redirect:"+Global.getAdminPath()+"/mod/measure/?treeId="+measure.getCategory().getId()+"&repage";
	}
	
	private void saveTags(Measure measure) {
		List<Measure> list =measureService.findList(measure);//根据当前measure查询
		if(list==null || list.size()==0)
			return;
		Measure target = list.get(0);
		String tags = target.getTags();
		if(tags == null || tags.trim().length()==0)
			return;
		
		//处理标签主题的默认分类节点
		TagCategory parent = new TagCategory();
		parent.setParent(new TagCategory("0"));//查找一级节点
		parent.setName("auto");//一级节点名称为auto
		List<TagCategory> parents = tagCategoryService.findList(parent);
		if(parents!=null && parents.size()>0)
			parent = parents.get(0);//取一级节点的第一个作为目录
		else {//否则建立一个名为auto的节点
			tagCategoryService.save(parent);
			parents = tagCategoryService.findList(parent);
			parent = parents.get(0);
		}
		
		//逐个建立标签主题
		String[] tagArray = tags.split("\\s+");
		for(String tag:tagArray) {
			Tags item = new Tags();
			item.setMeasure(measure);
			item.setName(tag);
			List<Tags> exists = tagsService.findList(item);
			if(exists == null || exists.size()==0) {//仅在没有相同tag的时候才创建
				item.setType("auto");
				item.setTagCategory(parent);//放到第一个一级节点下，或者直接挂到根目录下
				tagsService.save(item);
			}
		}
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
//	public List<Map<String, Object>> listData(Measure measure, HttpServletRequest request, HttpServletResponse response, Model model) {
	public List<Map<String, Object>> listData(@RequestParam(required=false) String extId,  HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		ItemCategory category = itemCategoryService.get(extId);//根据category过滤
		//遍历获取所有节点的属性
		while(category!=null) {
			List<Measure> measures =measureService.findByCategory(category.getId());
			for (Measure item:measures){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", item.getId());
				map.put("pId", "0");
				map.put("pIds", "0");
				map.put("name", extId.equalsIgnoreCase(item.getId())?item.getName():"["+category.getName()+"]"+item.getName());
				mapList.add(map);
			}
			category = itemCategoryService.get(category.getParent());//注意：必须通过service逐层获取上级，通过entity只能获取一层
		}
		return mapList;
	}
	
	/**
	 * 获取类目下的属性。支持获取所有继承属性
	 * 
	 * @param category
	 * @param cascade true/false
	 * @param noPrefix true/false 默认会带有是否继承前缀，传入该参数则忽略
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "measures")
	public List<Map<String, Object>> listMeasureByCategory(@RequestParam(required=true) String category,@RequestParam(required=false) String cascade,@RequestParam(required=false) String noPrefix,HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		boolean isCascade = true;//默认为true，获取当前目录的属性
		
		if("user".equalsIgnoreCase(category)) { //兼容用户属性，通过关键字获取
			return loadUserProps();
		}

		List<Map<String, Object>> mapList = Lists.newArrayList();
		
		ItemCategory itemCategory = itemCategoryService.get(category);
		while(isCascade && itemCategory!=null) {
			List<Measure> list =measureService.findByCategory(itemCategory.getId());
			for (int i=0; i<list.size(); i++){
				Measure e = list.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("category", e.getCategory().getId());
				if("true".equalsIgnoreCase(noPrefix))
					map.put("name", e.getName());
				else
					map.put("name", (category.equalsIgnoreCase(e.getCategory().getId())?"๏":"○")+e.getName());
				map.put("type", category.equalsIgnoreCase(e.getCategory().getId())?"self":"inherit");
				map.put("property", e.getProperty());
				map.put("labelType",e.getAutoLabelType());//标注类型：auto,manual,dict,refer
				map.put("referDict",e.getAutoLabelDict());//标注字典，仅对dict标注有效
				map.put("referCategory",e.getAutoLabelCategory()==null?"":e.getAutoLabelCategory().getId());//引用标注，仅对refer标注有效
				map.put("defaultValue",e.getDefaultValue()==null?"":e.getDefaultValue());//返回默认值
				map.put("isModifiable", e.getIsModifiable());//是否可手动编辑，用于标注时进行控制
				mapList.add(map);
			}
			
			itemCategory = itemCategoryService.get(itemCategory.getParent());
			if(!"true".equalsIgnoreCase(cascade))
				isCascade = false;
		}
		
		return mapList;
	}
	
	/**
	 * 移动端能够直接提交新建属性。注意对新建属性默认补齐设置
	 * @param measure
	 * @return
	 */
	@RequestMapping(value = "rest/measure", method = RequestMethod.POST)
	public JSONObject upsert(Measure measure) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(measure.getId()==null||measure.getId().trim().length()==0) {//自动补齐默认设置
			measure.setId(Util.get32UUID());
			measure.setIsNewRecord(true);
			//注意：需要在前端自动补齐
			/**
			measure.setAlpha(0.2);
			measure.setBeta(0.2);
			measure.setGamma(0.2);
			measure.setDelte(0.2);
			measure.setEpsilon(0.2);
			
			measure.setZeta(0.5);
			measure.setEta(0.3);
			measure.setTheta(0.2);
			//**/
		}
		try {
			measureService.save(measure);
			result.put("success", true);
			result.put("data", measure);
			result.put("msg",  "保存关键属性成功");
		}catch(Exception ex) {
			result.put("msg", ex.getMessage());
		}
		//将tag分别建立为主题
		if(measure.getTags()!=null&&measure.getTags().trim().length()>0) {
			saveTags(measure);
		}
		return result;
	}
	
	@RequestMapping(value = "rest/measure/{id}", method = RequestMethod.DELETE)
	public JSONObject delete(String id) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		Measure measure = measureService.get(id);
		if(measure ==null) {
			result.put("msg", "指定ID的关键属性不存在");
			return result;
		}
		result.put("data", measure);
		measureService.delete(measure);
		result.put("success", true);
		result.put("msg", "删除商品属性成功");
		return result;
	}
	
	public List<Map<String, Object>>  loadUserProps(){
		List<Map<String, Object>> mapList = Lists.newArrayList();
		UserMeasure query = new UserMeasure();
		List<UserMeasure> userMeasures = userMeasureService.findList(query);
		for(UserMeasure e:userMeasures) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("category", e.getCategory()==null?"0":e.getCategory().getId());
			map.put("name",e.getCategory()==null? e.getName() :(e.getCategory().getName() +" "+ e.getName()));
			map.put("type", "self");
			map.put("property", e.getProperty());
			map.put("labelType",e.getAutoLabelType());//标注类型：auto,manual,dict,refer
			map.put("referDict",e.getAutoLabelDict());//标注字典，仅对dict标注有效
			map.put("referCategory",e.getAutoLabelCategory()==null?"":e.getAutoLabelCategory().getId());//引用标注，仅对refer标注有效
			map.put("defaultValue","");//返回默认值
			map.put("isModifiable", "");//是否可手动编辑，用于标注时进行控制
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
	public String none(HttpServletRequest request, HttpServletResponse response, Model model) {
//		model.addAttribute("message","请在左侧选择一个类型。");
		//默认直接查询所有记录
		Measure measure = new Measure();
		Page<Measure> page = measureService.findPage(new Page<Measure>(request, response), measure);
		model.addAttribute("page", page);
//		return "treeData/none";
		return "modules/mod/measureList";
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
				//查询该类别下的属性
				Measure query = new Measure();
				query.setCategory(e);
				List<Measure> props = measureService.findList(query);
				for(Measure prop:props) {
					Map<String, Object> leafNode = Maps.newHashMap();
					leafNode.put("id", prop.getId());
					leafNode.put("pId", e.getId());
					leafNode.put("name", "๏"+prop.getName());
					mapList.add(leafNode);
				}
			}
		}
		return mapList;
	}
}