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
import com.pcitech.iLife.modules.diy.entity.GuideBook;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.ItemTemplate;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.ItemTemplateService;
import com.pcitech.iLife.util.Util;

/**
 * 类目推广文案Controller
 * @author qchzhu
 * @version 2021-07-21
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/template")
public class ItemTemplateController extends BaseController {

	@Autowired
	private ItemTemplateService templateService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	
	//新增或修改
	@ResponseBody
	@RequestMapping(value = "rest/template", method = RequestMethod.POST)
	public JSONObject upsert( @RequestBody ItemTemplate itemTemplate) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		String id = Util.get32UUID();
		if(itemTemplate.getId()==null||itemTemplate.getId().trim().length()==0) {//认为是新增
			itemTemplate.setId(id);
			itemTemplate.setIsNewRecord(true);
		}
		try {
			templateService.save(itemTemplate);
			result.put("data", templateService.get(itemTemplate));
			result.put("success", true);
		}catch(Exception ex) {
			result.put("success", false);
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//过滤获取指南列表：分页可自行设置page
	/**
	{
		page:{
			pageNo:xxx,
			pageSize:xxx
		}
	}
	 */
	@ResponseBody
	@RequestMapping(value = "rest/item-templates", method = RequestMethod.POST)
	public List<ItemTemplate> listPagedList(@RequestBody ItemTemplate itemTemplate) {
		if(itemTemplate.getPage()!=null)
			return templateService.findPage(itemTemplate.getPage(), itemTemplate).getList();
		else 
			return templateService.findList(itemTemplate);
	}
	
	/**
	 * 根据categoryId获取所有推荐语模板。包含继承的上级类目模板
	 */
	@ResponseBody
	@RequestMapping(value = "rest/item-templates", method = RequestMethod.GET)
	public List<ItemTemplate> getItemTemplates(String categoryId,String status){
		Map<String,Object> params = Maps.newHashMap();
		params.put("categoryId", categoryId);
		params.put("status", status);
		return templateService.findItemList(params);
	}
	
	//获取board模板
	@ResponseBody
	@RequestMapping(value = "rest/board-templates", method = RequestMethod.GET)
	public List<ItemTemplate> getBoardTemplates(){
		return templateService.findBoardList();
	}
	
	@ModelAttribute
	public ItemTemplate get(@RequestParam(required=false) String id) {
		ItemTemplate entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = templateService.get(id);
		}
		if (entity == null){
			entity = new ItemTemplate();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:template:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemTemplate itemTemplate,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		itemTemplate.setCategory(new ItemCategory(treeId));
		Page<ItemTemplate> page = templateService.findPage(new Page<ItemTemplate>(request, response), itemTemplate); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/templateList";
	}

	@RequiresPermissions("mod:template:view")
	@RequestMapping(value = "form")
	public String form(ItemTemplate itemTemplate, Model model) {
		if(itemTemplate.getCategory()!=null&&StringUtils.isNoneBlank(itemTemplate.getCategory().getId())){
			itemTemplate.setCategory(itemCategoryService.get(itemTemplate.getCategory().getId()));
		}
		model.addAttribute("itemTemplate", itemTemplate);
		model.addAttribute("treeId", itemTemplate.getCategory().getId());
		return "modules/mod/templateForm";
	}

	@RequiresPermissions("mod:template:edit")
	@RequestMapping(value = "save")
	public String save(ItemTemplate template, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, template)){
			return form(template, model);
		}
		templateService.save(template);
		addMessage(redirectAttributes, "保存文案模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/template/?treeId="+template.getCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:template:edit")
	@RequestMapping(value = "delete")
	public String delete(ItemTemplate template, RedirectAttributes redirectAttributes) {
		templateService.delete(template);
		addMessage(redirectAttributes, "删除文案模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/template/?treeId="+template.getCategory().getId()+"&repage";
	}


	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(ItemTemplate template, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ItemTemplate> list =templateService.findList(template);
		for (int i=0; i<list.size(); i++){
			ItemTemplate e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
			mapList.add(map);
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "templates")
	public List<Map<String, Object>> listTemplateByCategory(@RequestParam(required=true) String category,HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ItemTemplate> list =templateService.findByCategory(category);
		for (int i=0; i<list.size(); i++){
			ItemTemplate e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("category", e.getCategory().getId());
			map.put("name", e.getName());
			mapList.add(map);
			
		}
		return mapList;
	}
	
	@RequiresPermissions("mod:template:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/template");
		model.addAttribute("title","文案模板");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:template:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/template");
		model.addAttribute("title","商品类目");
		List<ItemCategory> itemCategoryTree = itemCategoryService.findTree();
		model.addAttribute("list", itemCategoryTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:measure:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个类型。");
		return "treeData/none";
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
				//查询该类别下的文案模板
				ItemTemplate query = new ItemTemplate();
				query.setCategory(e);
				List<ItemTemplate> items = templateService.findList(query);
				for(ItemTemplate item:items) {
					Map<String, Object> leafNode = Maps.newHashMap();
					leafNode.put("id", item.getId());
					leafNode.put("pId", e.getId());
					leafNode.put("name", item.getName());
					mapList.add(leafNode);
				}
			}
		}
		return mapList;
	}
	
}