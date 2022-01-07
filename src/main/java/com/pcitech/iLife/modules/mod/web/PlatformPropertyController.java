/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.pcitech.iLife.modules.mod.service.PlatformPropertyService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;

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
	
	@RequiresPermissions("mod:platformProperty:view")
	@RequestMapping(value = {"list", ""})
	public String list(PlatformProperty platformProperty,String treeId,  HttpServletRequest request, HttpServletResponse response, Model model) {
		platformProperty.setPlatform(treeId);
		Page<PlatformProperty> page = platformPropertyService.findPage(new Page<PlatformProperty>(request, response), platformProperty); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/platformPropertyList";
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
		return "redirect:"+Global.getAdminPath()+"/mod/platformProperty/?treeId="+platformProperty.getPlatform()+"&repage";
	}
	
	@RequiresPermissions("mod:platformProperty:edit")
	@RequestMapping(value = "delete")
	public String delete(PlatformProperty platformProperty, RedirectAttributes redirectAttributes) {
		platformPropertyService.delete(platformProperty);
		addMessage(redirectAttributes, "删除电商平台属性映射成功");
		return "redirect:"+Global.getAdminPath()+"/mod/platformProperty/?treeId="+platformProperty.getPlatform()+"&repage";
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
		model.addAttribute("url","mod/platformProperty");
		model.addAttribute("title","电商平台");
		model.addAttribute("list", getPlatformTree());
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:platformProperty:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择电商平台。");
		return "treeData/none";
	}

}