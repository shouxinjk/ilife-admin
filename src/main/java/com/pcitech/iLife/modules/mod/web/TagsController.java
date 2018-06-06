/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.TagCategory;
import com.pcitech.iLife.modules.mod.entity.Tags;
import com.pcitech.iLife.modules.mod.service.TagCategoryService;
import com.pcitech.iLife.modules.mod.service.TagsService;

/**
 * 标签Controller
 * @author chenci
 * @version 2017-09-27
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/tags")
public class TagsController extends BaseController {

	@Autowired
	private TagsService tagsService;
	@Autowired
	private TagCategoryService tagCategoryService;
	
	@ModelAttribute
	public Tags get(@RequestParam(required=false) String id) {
		Tags entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tagsService.get(id);
		}
		if (entity == null){
			entity = new Tags();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:tags:view")
	@RequestMapping(value = {"list", ""})
	public String list(Tags tags,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		tags.setTagCategory(new TagCategory(treeId));
		Page<Tags> page = tagsService.findPage(new Page<Tags>(request, response), tags); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/tagsList";
	}

	@RequiresPermissions("mod:tags:view")
	@RequestMapping(value = "form")
	public String form(Tags tags,Model model) {
		if(tags.getTagCategory()!=null&&StringUtils.isNoneBlank(tags.getTagCategory().getId())){
			tags.setTagCategory(tagCategoryService.get(tags.getTagCategory().getId()));
		}
		model.addAttribute("tags", tags);
		return "modules/mod/tagsForm";
	}

	@RequiresPermissions("mod:tags:edit")
	@RequestMapping(value = "save")
	public String save(Tags tags, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, tags)){
			return form(tags, model);
		}
		tagsService.save(tags);
		addMessage(redirectAttributes, "保存标签成功");
		return "redirect:"+Global.getAdminPath()+"/mod/tags/?treeId="+tags.getTagCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:tags:edit")
	@RequestMapping(value = "delete")
	public String delete(Tags tags, String treeId,RedirectAttributes redirectAttributes) {
		tagsService.delete(tags);
		addMessage(redirectAttributes, "删除标签成功");
		return "redirect:"+Global.getAdminPath()+"/mod/tags/?treeId="+treeId+"&repage";
	}

	@RequiresPermissions("mod:tags:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/tags");
		model.addAttribute("title","标签");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:tags:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/tags");
		model.addAttribute("title","分类");
		model.addAttribute("list", tagCategoryService.findTree());
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:tags:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个分类。");
		return "treeData/none";
	}
}