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
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.UserTagCategory;
import com.pcitech.iLife.modules.mod.service.UserTagCategoryService;

/**
 * 用户标签分类Controller
 * @author qchzhu
 * @version 2020-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userTagCategory")
public class UserTagCategoryController extends BaseController {

	@Autowired
	private UserTagCategoryService userTagCategoryService;
	
	@ModelAttribute
	public UserTagCategory get(@RequestParam(required=false) String id) {
		UserTagCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userTagCategoryService.get(id);
		}
		if (entity == null){
			entity = new UserTagCategory();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userTagCategory:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserTagCategory userTagCategory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<UserTagCategory> list = Lists.newArrayList();
		List<UserTagCategory> sourcelist = userTagCategoryService.findTree(); 
		UserTagCategory.sortList(list, sourcelist, "0",true);
		model.addAttribute("list", list);
		return "modules/mod/userTagCategoryList";
	}

	@RequiresPermissions("mod:userTagCategory:view")
	@RequestMapping(value = "form")
	public String form(UserTagCategory userTagCategory, Model model) {
		if (userTagCategory.getParent()!=null && StringUtils.isNotBlank(userTagCategory.getParent().getId())){
			UserTagCategory parent=userTagCategoryService.get(userTagCategory.getParent().getId());
			userTagCategory.setParent(parent);
			if(parent !=null && StringUtils.isNotBlank(parent.getType())){
				userTagCategory.setType(parent.getType());
			}

			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(userTagCategory.getId())){
				UserTagCategory userTagCategoryChild = new UserTagCategory();
				userTagCategoryChild.setParent(new UserTagCategory(userTagCategory.getParent().getId()));
				List<UserTagCategory> list = userTagCategoryService.findList(userTagCategory); 
				if (list.size() > 0){
					userTagCategory.setSort(list.get(list.size()-1).getSort());
					if (userTagCategory.getSort() != null){
						userTagCategory.setSort(userTagCategory.getSort() + 30);
					}
				}
			}
		}
		if (userTagCategory.getSort() == null){
			userTagCategory.setSort(30);
		}
		model.addAttribute("userTagCategory", userTagCategory);
		return "modules/mod/userTagCategoryForm";
	}

	@RequiresPermissions("mod:userTagCategory:edit")
	@RequestMapping(value = "save")
	public String save(UserTagCategory userTagCategory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userTagCategory)){
			return form(userTagCategory, model);
		}
		userTagCategoryService.save(userTagCategory);
		if(StringUtils.isBlank(userTagCategory.getParent().getId())||userTagCategory.getParent().getId().equals("0")){
			userTagCategoryService.updateChildrenType(userTagCategory);
		}
		addMessage(redirectAttributes, "保存标签分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userTagCategory/?repage";
	}
	
	@RequiresPermissions("mod:userTagCategory:edit")
	@RequestMapping(value = "delete")
	public String delete(UserTagCategory userTagCategory, RedirectAttributes redirectAttributes) {
		userTagCategoryService.delete(userTagCategory);
		addMessage(redirectAttributes, "删除标签分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userTagCategory/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserTagCategory> list = userTagCategoryService.findList(new UserTagCategory());
		for (int i=0; i<list.size(); i++){
			UserTagCategory e = list.get(i);
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
	
	/**
	 * 批量修改排序
	 */
	@RequiresPermissions("mod:phase:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
    	int len = ids.length;
    	UserTagCategory[] entitys = new UserTagCategory[len];
    	for (int i = 0; i < len; i++) {
    		entitys[i] = userTagCategoryService.get(ids[i]);
    		entitys[i].setSort(sorts[i]);
    		userTagCategoryService.save(entitys[i]);
    	}
    	addMessage(redirectAttributes, "保存排序成功!");
		return "redirect:" + adminPath + "/mod/userTagCategory/";
	}

}