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
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.service.UserDimensionService;

/**
 * 用户客观评价Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userDimension")
public class UserDimensionController extends BaseController {

	@Autowired
	private UserDimensionService userDimensionService;
	
	@ModelAttribute
	public UserDimension get(@RequestParam(required=false) String id) {
		UserDimension entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userDimensionService.get(id);
		}
		if (entity == null){
			entity = new UserDimension();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userDimension:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserDimension userDimension, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<UserDimension> list = userDimensionService.findList(userDimension); 
		model.addAttribute("list", list);
		return "modules/mod/userDimensionList";
	}

	@RequiresPermissions("mod:userDimension:view")
	@RequestMapping(value = "form")
	public String form(UserDimension userDimension, Model model) {
		if (userDimension.getParent()!=null && StringUtils.isNotBlank(userDimension.getParent().getId())){
			userDimension.setParent(userDimensionService.get(userDimension.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(userDimension.getId())){
				UserDimension userDimensionChild = new UserDimension();
				userDimensionChild.setParent(new UserDimension(userDimension.getParent().getId()));
				List<UserDimension> list = userDimensionService.findList(userDimension); 
				if (list.size() > 0){
					userDimension.setSort(list.get(list.size()-1).getSort());
					if (userDimension.getSort() != null){
						userDimension.setSort(userDimension.getSort() + 30);
					}
				}
			}
		}
		if (userDimension.getSort() == null){
			userDimension.setSort(30);
		}
		model.addAttribute("userDimension", userDimension);
		return "modules/mod/userDimensionForm";
	}

	@RequiresPermissions("mod:userDimension:edit")
	@RequestMapping(value = "save")
	public String save(UserDimension userDimension, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userDimension)){
			return form(userDimension, model);
		}
		userDimensionService.save(userDimension);
		addMessage(redirectAttributes, "保存用户客观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userDimension/?repage";
	}
	
	@RequiresPermissions("mod:userDimension:edit")
	@RequestMapping(value = "delete")
	public String delete(UserDimension userDimension, RedirectAttributes redirectAttributes) {
		userDimensionService.delete(userDimension);
		addMessage(redirectAttributes, "删除用户客观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userDimension/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserDimension> list = userDimensionService.findList(new UserDimension());
		for (int i=0; i<list.size(); i++){
			UserDimension e = list.get(i);
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