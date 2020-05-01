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
import com.pcitech.iLife.modules.mod.entity.UserTagCategory;
import com.pcitech.iLife.modules.mod.entity.UserTag;
import com.pcitech.iLife.modules.mod.service.UserTagCategoryService;
import com.pcitech.iLife.modules.mod.service.UserTagService;

/**
 * 标签Controller
 * @author chenci
 * @version 2017-09-27
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userTag")
public class UserTagController extends BaseController {

	@Autowired
	private UserTagService userTagService;
	@Autowired
	private UserTagCategoryService userTagCategoryService;
	
	@ModelAttribute
	public UserTag get(@RequestParam(required=false) String id) {
		UserTag entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userTagService.get(id);
		}
		if (entity == null){
			entity = new UserTag();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserTag userTag,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		userTag.setUserTagCategory(new UserTagCategory(treeId));
		Page<UserTag> page = userTagService.findPage(new Page<UserTag>(request, response), userTag); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/userTagList";
	}

	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = "form")
	public String form(UserTag userTag,Model model) {
		if(userTag.getUserTagCategory()!=null&&StringUtils.isNoneBlank(userTag.getUserTagCategory().getId())){
			userTag.setUserTagCategory(userTagCategoryService.get(userTag.getUserTagCategory().getId()));
		}
		model.addAttribute("userTag", userTag);
		return "modules/mod/userTagForm";
	}

	@RequiresPermissions("mod:userTag:edit")
	@RequestMapping(value = "save")
	public String save(UserTag userTag, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userTag)){
			return form(userTag, model);
		}
		userTagService.save(userTag);
		addMessage(redirectAttributes, "保存标签成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userTag/?treeId="+userTag.getUserTagCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:userTag:edit")
	@RequestMapping(value = "delete")
	public String delete(UserTag userTag, String treeId,RedirectAttributes redirectAttributes) {
		userTagService.delete(userTag);
		addMessage(redirectAttributes, "删除标签成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userTag/?treeId="+treeId+"&repage";
	}

	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/userTag");
		model.addAttribute("title","标签");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/userTag");
		model.addAttribute("title","分类");
		model.addAttribute("list", userTagCategoryService.findTree());
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个分类。");
		return "treeData/none";
	}
}