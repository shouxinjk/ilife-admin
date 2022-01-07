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
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;

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
	
	@RequiresPermissions("mod:platformCategory:view")
	@RequestMapping(value = {"list", ""})
	public String list(PlatformCategory platformCategory,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		platformCategory.setPlatform(treeId);
		List<PlatformCategory> list = platformCategoryService.findList(platformCategory); 
		model.addAttribute("list", list);
		model.addAttribute("treeId", treeId);
		return "modules/mod/platformCategoryList";
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
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<PlatformCategory> list = platformCategoryService.findList(new PlatformCategory());
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
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择电商平台。");
		return "treeData/none";
	}
	
	
}