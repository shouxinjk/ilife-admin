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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.UserCategory;
import com.pcitech.iLife.modules.mod.service.UserCategoryService;

/**
 * 用户属性分类Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userCategory")
public class UserCategoryController extends BaseController {

	@Autowired
	private UserCategoryService userCategoryService;
	
	@ModelAttribute
	public UserCategory get(@RequestParam(required=false) String id) {
		UserCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userCategoryService.get(id);
		}
		if (entity == null){
			entity = new UserCategory();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userCategory:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserCategory userCategory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<UserCategory> list = userCategoryService.findList(userCategory); 
		model.addAttribute("list", list);
		return "modules/mod/userCategoryList";
	}

	@RequiresPermissions("mod:userCategory:view")
	@RequestMapping(value = "form")
	public String form(UserCategory userCategory, Model model) {
		if (userCategory.getParent()!=null && StringUtils.isNotBlank(userCategory.getParent().getId())){
			userCategory.setParent(userCategoryService.get(userCategory.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(userCategory.getId())){
				UserCategory userCategoryChild = new UserCategory();
				userCategoryChild.setParent(new UserCategory(userCategory.getParent().getId()));
				List<UserCategory> list = userCategoryService.findList(userCategory); 
				if (list.size() > 0){
					userCategory.setSort(list.get(list.size()-1).getSort());
					if (userCategory.getSort() != null){
						userCategory.setSort(userCategory.getSort() + 30);
					}
				}
			}
		}
		if (userCategory.getSort() == null){
			userCategory.setSort(30);
		}
		model.addAttribute("userCategory", userCategory);
		return "modules/mod/userCategoryForm";
	}

	@RequiresPermissions("mod:userCategory:edit")
	@RequestMapping(value = "save")
	public String save(UserCategory userCategory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userCategory)){
			return form(userCategory, model);
		}
		userCategoryService.save(userCategory);
		addMessage(redirectAttributes, "保存用户属性分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userCategory/?repage";
	}
	
	@RequiresPermissions("mod:userCategory:edit")
	@RequestMapping(value = "delete")
	public String delete(UserCategory userCategory, RedirectAttributes redirectAttributes) {
		userCategoryService.delete(userCategory);
		addMessage(redirectAttributes, "删除用户属性分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userCategory/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserCategory> list = userCategoryService.findList(new UserCategory());
		for (int i=0; i<list.size(); i++){
			UserCategory e = list.get(i);
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
	
}