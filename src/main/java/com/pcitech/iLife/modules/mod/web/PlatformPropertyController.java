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
import org.springframework.transaction.annotation.Transactional;
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
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.mod.entity.PlatformProperty;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.modules.mod.service.PlatformPropertyService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.Util;

/**
 * 电商平台属性映射Controller
 * @author ilife
 * @version 2022-01-07
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/platformProperty")
public class PlatformPropertyController extends BaseController {

	@Autowired
	private PlatformPropertyService platformPropertyService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private PlatformCategoryService platformCategoryService;
	@Autowired
	private MeasureService measureService;
	
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public PlatformProperty get(@RequestParam(required=false) String id) {
		PlatformProperty entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = platformPropertyService.get(id);
		}
		if (entity == null){
			entity = new PlatformProperty();
		}
		return entity;
	}
	
	/**
	 * 显示所有属性列表，用于查看验证
	 * @param platformProperty
	 * @param treeId
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("mod:platformProperty:view")
	@RequestMapping(value = {"list"})
	public String list(PlatformProperty platformProperty,String treeId,  HttpServletRequest request, HttpServletResponse response, Model model) {
		platformProperty.setPlatform(treeId);
		Page<PlatformProperty> page = platformPropertyService.findPage(new Page<PlatformProperty>(request, response), platformProperty); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/platformPropertyList";
	}
	
	/**
	 * 仅显示props.xxx属性列表，用于标注
	 * @param platformProperty
	 * @param treeId
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("mod:platformProperty:view")
	@RequestMapping(value = {"listPending", ""})
	public String listPending(PlatformProperty platformProperty,String treeId,  HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeId!=null && treeId.trim().length()>0) //从首页直接进入时不会带有treeId
			platformProperty.setPlatform(treeId);
		Measure measure = new Measure();
		measure.setId("null");//设置id为null过滤所有待标注记录
//		measure.setName("null");//设置name为null过滤待标注props.xxx记录
		platformProperty.setMeasure(measure);
		Page<PlatformProperty> page = platformPropertyService.findPage(new Page<PlatformProperty>(request, response), platformProperty); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/platformPropertyListPending";
	}

	@RequiresPermissions("mod:platformProperty:view")
	@RequestMapping(value = "form")
	public String form(PlatformProperty platformProperty,String treeId, Model model) {
		if(platformProperty.getPlatform() == null || platformProperty.getPlatform().trim().length()==0)
			platformProperty.setPlatform(treeId);
		model.addAttribute("platformProperty", platformProperty);
		model.addAttribute("treeId", treeId);
		return "modules/mod/platformPropertyForm";
	}

	@RequiresPermissions("mod:platformProperty:edit")
	@RequestMapping(value = "save")
	public String save(PlatformProperty platformProperty,String treeId, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, platformProperty)){
			return form(platformProperty, treeId, model);
		}
		platformPropertyService.save(platformProperty);
		addMessage(redirectAttributes, "保存电商平台属性映射成功");
		return "redirect:"+Global.getAdminPath()+"/mod/platformProperty/listPending?treeId="+platformProperty.getPlatform()+"&repage";
	}
	
	@RequiresPermissions("mod:platformProperty:edit")
	@RequestMapping(value = "delete")
	public String delete(PlatformProperty platformProperty, RedirectAttributes redirectAttributes) {
		platformPropertyService.delete(platformProperty);
		addMessage(redirectAttributes, "删除电商平台属性映射成功");
		return "redirect:"+Global.getAdminPath()+"/mod/platformProperty/listPending?treeId="+platformProperty.getPlatform()+"&repage";
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/mapping", method = RequestMethod.PATCH)
	//更新第三方属性与标准属性的映射关系
	public Map<String,Object> updateMapping( @RequestParam(required=true) String id, 
			@RequestParam(required=true) String measureId, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		PlatformProperty platformProperty = platformPropertyService.get(id);
		if(platformProperty==null) {
			result.put("msg", "platform property record not found.[id]="+id);
			return result;
		}
		
		Measure measure = measureService.get(measureId);
		if(measure==null) {
			result.put("msg", "cannot find measure by id.[id]="+measureId);
			return result;
		}
		
		platformProperty.setMeasure(measure);
		try {
			platformPropertyService.save(platformProperty);
		}catch(Exception ex) {
			result.put("msg", "failed save platform property.[id]="+id);
			result.put("error", ex.getMessage());
			return result;
		}
		
		result.put("success", true);
		result.put("msg", "mapping updated.");
		return result;
	}
	
	//组织显示左侧电商平台列表
	private List<JSONObject> getPlatformTree(){
		//列表
		List<JSONObject> list = Lists.newArrayList();
		
		//准备根节点
		JSONObject root = new JSONObject();
		root.put("id", "0");
		root.put("name", "所有电商平台");
		root.put("parent", null);
		list.add(root);
		//查询所有电商平台
		Dict dict = new Dict();
		dict.setType("platform");//查找所有电商平台
		List<Dict> platforms = dictService.findList(dict);
		for(Dict platform:platforms) {
			JSONObject node = new JSONObject();
			node.put("id", platform.getValue());
			node.put("businessId", platform.getValue());//仅用于前端参数传递
			node.put("name", platform.getLabel());
			node.put("parent", root);
			list.add(node);
		}
		return list;
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<PlatformCategory> list = platformCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			PlatformCategory e = list.get(i);
			if (extId == null || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():0);
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	/**
	 * 新建待映射标注的属性。使用场景：在分析过程中，checkProperty将无标准属性的数据提交新建记录，等待标注
	 * insert ignore：采用platform-categoryId-name唯一进行识别
	 * @deprecated 实际中，分析器将直接提交数据到kafka，写入mysql，不需要经过管理端中转
	 * @param json 
	 * {
	 * 		name: name,
	 * 		mappingCategoryId: mappingCateogryId,已经映射的标准目录ID。
	 * 		platform: platform
	 * 
	 * 
	 * @return
	 */
	@Transactional(readOnly = false)
	@ResponseBody
	@RequestMapping(value = "rest/mapping", method = RequestMethod.POST)
	public JSONObject checkMapping(@RequestBody JSONObject json,HttpServletRequest request, HttpServletResponse response, Model model) {
		JSONObject result = new JSONObject();
		String id = Util.md5(json.getString("platform")+json.getString("categoryId")+json.getString("name"));//构建唯一字符串
		PlatformProperty query = platformPropertyService.get(id);
		if(query ==null) {//新建记录
			query = new PlatformProperty();
			query.setIsNewRecord(true);
			query.setId(id);//采用手动唯一值
			query.setName(json.getString("name"));
			query.setPlatform(json.getString("platform"));
		}
		ItemCategory category = itemCategoryService.get(json.getString("categoryId"));
		query.setCategory(category);
		try {
			platformPropertyService.save(query);
			result.put("success",true);
		}catch(Exception ex) {
			result.put("success",false);
			result.put("msg", ex.getMessage());
		}
		return result;
	}
	
	/**
	 * 根据原始类目及属性名称获取属性列表。
	 * 
	 * @param json
	 * {
	 * 		platform: platform,
	 * 		category: category //原始目录名称：可能存在尚未映射的情况
	 * }
	 */
	@ResponseBody
	@RequestMapping(value = "rest/mapping", method = RequestMethod.GET)
	public JSONObject listPending(@RequestParam(required=true) String platform,@RequestParam(required=true) String category,HttpServletRequest request, HttpServletResponse response, Model model) {
		JSONObject result = new JSONObject();
		result.put("success",false);
		PlatformProperty query = new PlatformProperty();
		query.setPlatform(platform);
		PlatformCategory platformCategory = new PlatformCategory();
		platformCategory.setName(category);
		platformCategory.setId(null);//禁止根据ID查询
		query.setPlatformCategory(platformCategory);
		List<PlatformProperty> list = platformPropertyService.findList(query);
		List<JSONObject> jsonObjects = Lists.newArrayList();
		if(list.size()>0) {
			result.put("success",true);
			for(PlatformProperty prop:list) {
				if(prop.getMeasure()!=null) {//可能尚未建立映射，仅返回已经建立映射的属性列表
					JSONObject json = new JSONObject();
					json.put("id", prop.getId());
					json.put("name", prop.getName());
					if(prop.getPlatformCategory()!=null) {
						json.put("cid", prop.getPlatformCategory().getId());
						json.put("cname", prop.getPlatformCategory().getName());
					}
					if(prop.getCategory()!=null) {
						json.put("categoryId", prop.getCategory().getId());
						json.put("categoryName", prop.getCategory().getName());
					}
					json.put("mappingId", prop.getMeasure().getId());
					json.put("mappingName", prop.getMeasure().getName());
					jsonObjects.add(json);
				}
			}
		}
		result.put("data",jsonObjects);//注意：无映射属性时反馈空列表
		return result;
	}
	
	//准备左侧平台列表
	@RequiresPermissions("mod:platformProperty:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/platformProperty");
		model.addAttribute("title","电商平台");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:platformProperty:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/platformProperty/listPending");
		model.addAttribute("title","电商平台");
		model.addAttribute("list", getPlatformTree());
		return "treeData/tree";
	}
	
	/**
	 * 默认显示所有待标注列表
	 * @param model
	 * @return
	 */
	@RequiresPermissions("mod:platformProperty:view")
	@RequestMapping(value = "none")
	public String none(HttpServletRequest request, HttpServletResponse response, Model model) {
		PlatformProperty platformProperty = new PlatformProperty();
		Measure measure = new Measure();
		measure.setId("null");//设置id为null过滤所有待标注记录
		platformProperty.setMeasure(measure);
		Page<PlatformProperty> page = platformPropertyService.findPage(new Page<PlatformProperty>(request, response), platformProperty); 
		model.addAttribute("page", page);
		return "modules/mod/platformPropertyListPending";
	}

}