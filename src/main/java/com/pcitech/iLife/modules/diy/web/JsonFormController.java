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
import com.pcitech.iLife.modules.diy.entity.JsonForm;
import com.pcitech.iLife.modules.diy.service.JsonFormService;
import com.pcitech.iLife.modules.mod.entity.Board;

/**
 * 动态表单Controller
 * @author chenci
 * @version 2022-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/jsonForm")
public class JsonFormController extends BaseController {

	@Autowired
	private JsonFormService jsonFormService;
	
	@ModelAttribute
	public JsonForm get(@RequestParam(required=false) String id) {
		JsonForm entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jsonFormService.get(id);
		}
		if (entity == null){
			entity = new JsonForm();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:jsonForm:view")
	@RequestMapping(value = {"list", ""})
	public String list(JsonForm jsonForm, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JsonForm> page = jsonFormService.findPage(new Page<JsonForm>(request, response), jsonForm); 
		model.addAttribute("page", page);
		return "modules/diy/jsonFormList";
	}

	@RequiresPermissions("diy:jsonForm:view")
	@RequestMapping(value = "form")
	public String form(JsonForm jsonForm, Model model) {
		model.addAttribute("jsonForm", jsonForm);
		return "modules/diy/jsonFormForm";
	}

	@RequiresPermissions("diy:jsonForm:edit")
	@RequestMapping(value = "save")
	public String save(JsonForm jsonForm, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jsonForm)){
			return form(jsonForm, model);
		}
		jsonFormService.save(jsonForm);
		addMessage(redirectAttributes, "保存动态表单成功");
		return "redirect:"+Global.getAdminPath()+"/diy/jsonForm/?repage";
	}
	
	@RequiresPermissions("diy:jsonForm:edit")
	@RequestMapping(value = "delete")
	public String delete(JsonForm jsonForm, RedirectAttributes redirectAttributes) {
		jsonFormService.delete(jsonForm);
		addMessage(redirectAttributes, "删除动态表单成功");
		return "redirect:"+Global.getAdminPath()+"/diy/jsonForm/?repage";
	}

	/**
	 * 查询所有JsonForm供选用
	 * @param jsonForm
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(JsonForm jsonForm, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<JsonForm> list =jsonFormService.findList(jsonForm);
		for (int i=0; i<list.size(); i++){
			JsonForm e = list.get(i);
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