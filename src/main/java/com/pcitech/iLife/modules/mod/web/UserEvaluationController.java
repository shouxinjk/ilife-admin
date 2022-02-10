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
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.mod.service.UserEvaluationService;
import com.pcitech.iLife.util.Util;

/**
 * 用户主观评价Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userEvaluation")
public class UserEvaluationController extends BaseController {

	@Autowired
	private UserEvaluationService userEvaluationService;
	
	@ModelAttribute
	public UserEvaluation get(@RequestParam(required=false) String id) {
		UserEvaluation entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userEvaluationService.get(id);
		}
		if (entity == null){
			entity = new UserEvaluation();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userEvaluation:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserEvaluation userEvaluation, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<UserEvaluation> list = userEvaluationService.findList(userEvaluation); 
		model.addAttribute("list", list);
		return "modules/mod/userEvaluationList";
	}

	@RequiresPermissions("mod:userEvaluation:view")
	@RequestMapping(value = "form")
	public String form(UserEvaluation userEvaluation, Model model) {
		if (userEvaluation.getParent()!=null && StringUtils.isNotBlank(userEvaluation.getParent().getId())){
			userEvaluation.setParent(userEvaluationService.get(userEvaluation.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(userEvaluation.getId())){
				UserEvaluation userEvaluationChild = new UserEvaluation();
				userEvaluationChild.setParent(new UserEvaluation(userEvaluation.getParent().getId()));
				List<UserEvaluation> list = userEvaluationService.findList(userEvaluation); 
				if (list.size() > 0){
					userEvaluation.setSort(list.get(list.size()-1).getSort());
					if (userEvaluation.getSort() != null){
						userEvaluation.setSort(userEvaluation.getSort() + 30);
					}
				}
			}
		}
		if (userEvaluation.getSort() == null){
			userEvaluation.setSort(30);
		}
		if(userEvaluation.getPropKey()==null || userEvaluation.getPropKey().trim().length()==0)
			userEvaluation.setPropKey("e"+Util.get6bitCode("ue"+userEvaluation.getName()));//以e打头的7位字符串，大小写区分。保存时如果重复将报错
		
		model.addAttribute("userEvaluation", userEvaluation);
		return "modules/mod/userEvaluationForm";
	}

	@RequiresPermissions("mod:userEvaluation:edit")
	@RequestMapping(value = "save")
	public String save(UserEvaluation userEvaluation, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userEvaluation)){
			return form(userEvaluation, model);
		}
		userEvaluationService.save(userEvaluation);
		addMessage(redirectAttributes, "保存用户主观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluation/?repage";
	}
	
	@RequiresPermissions("mod:userEvaluation:edit")
	@RequestMapping(value = "delete")
	public String delete(UserEvaluation userEvaluation, RedirectAttributes redirectAttributes) {
		userEvaluationService.delete(userEvaluation);
		addMessage(redirectAttributes, "删除用户主观评价成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluation/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserEvaluation> list = userEvaluationService.findList(new UserEvaluation());
		for (int i=0; i<list.size(); i++){
			UserEvaluation e = list.get(i);
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