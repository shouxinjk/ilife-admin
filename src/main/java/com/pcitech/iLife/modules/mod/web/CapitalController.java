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
import com.pcitech.iLife.modules.mod.entity.Capital;
import com.pcitech.iLife.modules.mod.service.CapitalService;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 资本类型Controller
 * @author chenci
 * @version 2017-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/capital")
@ApiIgnore
public class CapitalController extends BaseController {

	@Autowired
	private CapitalService capitalService;
	
	@ModelAttribute
	public Capital get(@RequestParam(required=false) String id) {
		Capital entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = capitalService.get(id);
		}
		if (entity == null){
			entity = new Capital();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:capital:view")
	@RequestMapping(value = {"list", ""})
	public String list(Capital capital, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Capital> page = capitalService.findPage(new Page<Capital>(request, response), capital); 
		model.addAttribute("page", page);
		return "modules/mod/capitalList";
	}

	@RequiresPermissions("mod:capital:view")
	@RequestMapping(value = "form")
	public String form(Capital capital, Model model) {
		model.addAttribute("capital", capital);
		return "modules/mod/capitalForm";
	}

	@RequiresPermissions("mod:capital:edit")
	@RequestMapping(value = "save")
	public String save(Capital capital, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, capital)){
			return form(capital, model);
		}
		capitalService.save(capital);
		addMessage(redirectAttributes, "保存资本类型成功");
		return "redirect:"+Global.getAdminPath()+"/mod/capital/?repage";
	}
	
	@RequiresPermissions("mod:capital:edit")
	@RequestMapping(value = "delete")
	public String delete(Capital capital, RedirectAttributes redirectAttributes) {
		capitalService.delete(capital);
		addMessage(redirectAttributes, "删除资本类型成功");
		return "redirect:"+Global.getAdminPath()+"/mod/capital/?repage";
	}


	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Capital capital, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Capital> list =capitalService.findList(capital);
		for (int i=0; i<list.size(); i++){
			Capital e = list.get(i);
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
}