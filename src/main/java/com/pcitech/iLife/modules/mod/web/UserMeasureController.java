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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.UserCategory;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.service.UserCategoryService;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;

/**
 * 用户属性定义Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userMeasure")
public class UserMeasureController extends BaseController {

	@Autowired
	private UserMeasureService userMeasureService;
	@Autowired
	private UserCategoryService userCategoryService;
	
	@ModelAttribute
	public UserMeasure get(@RequestParam(required=false) String id) {
		UserMeasure entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userMeasureService.get(id);
		}
		if (entity == null){
			entity = new UserMeasure();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserMeasure userMeasure,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		userMeasure.setCategory(new UserCategory(treeId));
		Page<UserMeasure> page = userMeasureService.findPage(new Page<UserMeasure>(request, response), userMeasure); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/userMeasureList";
	}

	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = "form")
	public String form(UserMeasure userMeasure, Model model) {
		if(userMeasure.getCategory()!=null&&StringUtils.isNoneBlank(userMeasure.getCategory().getId())){
			userMeasure.setCategory(userCategoryService.get(userMeasure.getCategory().getId()));
		}
		model.addAttribute("userMeasure", userMeasure);
		return "modules/mod/userMeasureForm";
	}

	@RequiresPermissions("mod:userMeasure:edit")
	@RequestMapping(value = "save")
	public String save(UserMeasure userMeasure, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userMeasure)){
			return form(userMeasure, model);
		}
		userMeasureService.save(userMeasure);
		addMessage(redirectAttributes, "保存用户属性定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userMeasure/?treeId="+userMeasure.getCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:userMeasure:edit")
	@RequestMapping(value = "delete")
	public String delete(UserMeasure userMeasure, RedirectAttributes redirectAttributes) {
		userMeasureService.delete(userMeasure);
		addMessage(redirectAttributes, "删除用户属性定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userMeasure/?treeId="+userMeasure.getCategory().getId()+"&repage";
	}

	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(UserMeasure measure, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserMeasure> list =userMeasureService.findList(measure);
		for (int i=0; i<list.size(); i++){
			UserMeasure e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
//			if (type != null && "3".equals(type)){
//				map.put("isParent", true);
//			}
			mapList.add(map);
			
		}
		return mapList;
	}

	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/userMeasure");
		model.addAttribute("title","用户属性");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/userMeasure");
		model.addAttribute("title","属性类型");
		List<UserCategory> userCategoryTree = userCategoryService.findTree();
		model.addAttribute("list", userCategoryTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个类型。");
		return "treeData/none";
	}
	
}