/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.web;

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
import com.pcitech.iLife.modules.diy.entity.GuideTerm;
import com.pcitech.iLife.modules.diy.entity.Solution;
import com.pcitech.iLife.modules.diy.service.GuideTermService;

/**
 * 个性化定制指南条目Controller
 * @author chenci
 * @version 2022-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/guideTerm")
public class GuideTermController extends BaseController {

	@Autowired
	private GuideTermService guideTermService;
	
	@ModelAttribute
	public GuideTerm get(@RequestParam(required=false) String id) {
		GuideTerm entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = guideTermService.get(id);
		}
		if (entity == null){
			entity = new GuideTerm();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:guideTerm:view")
	@RequestMapping(value = {"list", ""})
	public String list(GuideTerm guideTerm, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<GuideTerm> page = guideTermService.findPage(new Page<GuideTerm>(request, response), guideTerm); 
		model.addAttribute("page", page);
		return "modules/diy/guideTermList";
	}

	@RequiresPermissions("diy:guideTerm:view")
	@RequestMapping(value = "form")
	public String form(GuideTerm guideTerm, Model model) {
		model.addAttribute("guideTerm", guideTerm);
		return "modules/diy/guideTermForm";
	}

	@RequiresPermissions("diy:guideTerm:edit")
	@RequestMapping(value = "save")
	public String save(GuideTerm guideTerm, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, guideTerm)){
			return form(guideTerm, model);
		}
		guideTermService.save(guideTerm);
		addMessage(redirectAttributes, "保存个性化定制指南条目成功");
		return "redirect:"+Global.getAdminPath()+"/diy/guideTerm/?repage";
	}
	
	@RequiresPermissions("diy:guideTerm:edit")
	@RequestMapping(value = "delete")
	public String delete(GuideTerm guideTerm, RedirectAttributes redirectAttributes) {
		guideTermService.delete(guideTerm);
		addMessage(redirectAttributes, "删除个性化定制指南条目成功");
		return "redirect:"+Global.getAdminPath()+"/diy/guideTerm/?repage";
	}

	/**
	 * 查询所有指南条目
	 * @param guideTerm
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(GuideTerm guideTerm, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<GuideTerm> list = guideTermService.findList(guideTerm);
		for (int i=0; i<list.size(); i++){
			GuideTerm e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
			mapList.add(map);
			
		}
		return mapList;
	}
	
}