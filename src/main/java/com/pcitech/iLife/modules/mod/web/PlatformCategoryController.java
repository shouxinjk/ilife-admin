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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.mod.entity.PlatformProperty;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.Util;

/**
 * 电商平台类目映射Controller
 * @author ilife
 * @version 2022-01-07
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/platformCategory")
public class PlatformCategoryController extends BaseController {

	@Autowired
	private PlatformCategoryService platformCategoryService;
	
	@Autowired
	private ItemCategoryService itemCategoryService;
	
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public PlatformCategory get(@RequestParam(required=false) String id) {
		PlatformCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = platformCategoryService.get(id);
		}
		if (entity == null){
			entity = new PlatformCategory();
		}
		return entity;
	}
	
	//显示指定平台下所有记录
	@RequiresPermissions("mod:platformCategory:view")
	@RequestMapping(value = {"list"})
	public String list(PlatformCategory platformCategory,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeId!=null && treeId.trim().length()>0)
			platformCategory.setPlatform(treeId);
		ItemCategory itemCategory = new ItemCategory();
		itemCategory.setId("notnull");//设置id为null，将过滤已标注记录
		platformCategory.setCategory(itemCategory);
		Page<PlatformCategory> page = platformCategoryService.findPage(new Page<PlatformCategory>(request, response), platformCategory); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/platformCategoryList";
	}
	
	//显示标准类目id为空的记录，便于标注
	@RequiresPermissions("mod:platformCategory:view")
	@RequestMapping(value = {"listPending", ""})
	public String listPending(PlatformCategory platformCategory,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeId!=null && treeId.trim().length()>0)
			platformCategory.setPlatform(treeId);
		ItemCategory itemCategory = new ItemCategory();
		itemCategory.setId("null");//设置id为null，将过滤尚未标注的记录
		platformCategory.setCategory(itemCategory);
		Page<PlatformCategory> page = platformCategoryService.findPage(new Page<PlatformCategory>(request, response), platformCategory); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/platformCategoryListPending";
	}

	@RequiresPermissions("mod:platformCategory:view")
	@RequestMapping(value = "form")
	public String form(PlatformCategory platformCategory,String treeId, Model model) {
		if(platformCategory.getPlatform() == null || platformCategory.getPlatform().trim().length()==0)
			platformCategory.setPlatform(treeId);
		if (platformCategory.getParent()!=null && StringUtils.isNotBlank(platformCategory.getParent().getId())){
			platformCategory.setParent(platformCategoryService.get(platformCategory.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(platformCategory.getId())){
				PlatformCategory platformCategoryChild = new PlatformCategory();
				platformCategoryChild.setParent(new PlatformCategory(platformCategory.getParent().getId()));
				List<PlatformCategory> list = platformCategoryService.findList(platformCategory); 
				if (list.size() > 0){
					platformCategory.setSort(list.get(list.size()-1).getSort());
					if (platformCategory.getSort() != null){
						platformCategory.setSort(platformCategory.getSort() + 30);
					}
				}
			}
		}
		if (platformCategory.getSort() == null){
			platformCategory.setSort(30);
		}
		model.addAttribute("platformCategory", platformCategory);
		model.addAttribute("treeId", treeId);
		return "modules/mod/platformCategoryForm";
	}

	@RequiresPermissions("mod:platformCategory:edit")
	@RequestMapping(value = "save")
	public String save(PlatformCategory platformCategory,String treeId, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, platformCategory)){
			return form(platformCategory, treeId, model);
		}
		platformCategoryService.save(platformCategory);
		model.addAttribute("treeId", platformCategory.getPlatform());
		addMessage(redirectAttributes, "保存电商平台类目映射成功");
		return "redirect:"+Global.getAdminPath()+"/mod/platformCategory/?treeId="+platformCategory.getPlatform()+"&repage";
	}
	
	@RequiresPermissions("mod:platformCategory:edit")
	@RequestMapping(value = "delete")
	public String delete(PlatformCategory platformCategory,String treeId,  RedirectAttributes redirectAttributes) {
		platformCategoryService.delete(platformCategory);
		addMessage(redirectAttributes, "删除电商平台类目映射成功");
		return "redirect:"+Global.getAdminPath()+"/mod/platformCategory/?treeId="+platformCategory.getPlatform()+"&repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId,@RequestParam(required=false) String platform, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		PlatformCategory q = new PlatformCategory();
		q.setPlatform(platform);	
		List<PlatformCategory> list = platformCategoryService.findList(q);
		for (int i=0; i<list.size(); i++){
			PlatformCategory e = list.get(i);
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
	
	//rest接口
	//根据platform 及 原始类目名称查询 类目映射：如果不存在则新建
	@ResponseBody
	@RequestMapping(value = "rest/mapping", method = RequestMethod.GET)
	public JSONObject checkMapping(@RequestParam(required=true) String platform,@RequestParam(required=true) String name,HttpServletRequest request, HttpServletResponse response, Model model) {
		JSONObject result = new JSONObject();
		result.put("success",false);
		PlatformCategory query = new PlatformCategory();
		query.setName(name);
		query.setPlatform(platform);
		List<PlatformCategory> list = platformCategoryService.findMapping(query);
		if(list.size()>0) {
			result.put("success",true);
			result.put("data",list);//返回整个列表
		}else {//如果没有，则新建类目，等待标注
			query.setIsNewRecord(true);
			query.setId(Util.md5(platform+name));//采用手动生成ID，避免多次查询生成多条记录
			query.setCreateDate(new Date());
			query.setUpdateDate(new Date());
			platformCategoryService.save(query);
			result.put("success",false);
			List<PlatformCategory> blankList = Lists.newArrayList();
			result.put("data",blankList);//返回空白列表
			result.put("msg","Platform category mapping does not exist. Created as new mapping record.");
		}
		return result;
	}
	
	//提交映射：platform、原始类目名称及mappingId
	 @Transactional(readOnly = false)
	@ResponseBody
	@RequestMapping(value = "rest/mapping", method = RequestMethod.POST)
	public JSONObject mapping(@RequestBody JSONObject json,HttpServletRequest request, HttpServletResponse response, Model model) {
		JSONObject result = new JSONObject();
		PlatformCategory query = new PlatformCategory();
		query.setId(Util.md5(json.getString("platform")+json.getString("name")));//采用platform+name唯一识别
		query.setName(json.getString("name"));
		query.setPlatform(json.getString("platform"));
		ItemCategory category = new ItemCategory();
		category.setId(json.getString("categoryId"));
		query.setCategory(category);
		query.setCreateDate(new Date());
		query.setUpdateDate(new Date());
		boolean success = platformCategoryService.upsertMapping(query);
		result.put("success",success);
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
	
	@RequiresPermissions("mod:platformCategory:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/platformCategory");
		model.addAttribute("title","电商平台");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:platformCategory:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/platformCategory");
		model.addAttribute("title","电商平台");
		model.addAttribute("list", getPlatformTree());
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:platformCategory:view")
	@RequestMapping(value = "none")
	public String none(HttpServletRequest request, HttpServletResponse response, Model model) {
			PlatformCategory platformCategory = new PlatformCategory();
			ItemCategory itemCategory = new ItemCategory();
			itemCategory.setId("null");//使用null作为ID，过滤所有待标注记录
			platformCategory.setCategory(itemCategory);
			Page<PlatformCategory> page = platformCategoryService.findPage(new Page<PlatformCategory>(request, response), platformCategory); 
			model.addAttribute("page", page);
			return "modules/mod/platformCategoryList";
	}
	
	
}