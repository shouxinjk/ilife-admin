/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.web;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.ope.entity.HumanMarkedValue;
import com.pcitech.iLife.modules.ope.service.HumanMarkedValueService;
import com.pcitech.iLife.util.Util;

/**
 * 数据标注Controller
 * @author chenci
 * @version 2017-09-28
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/humanMarkedValue")
public class HumanMarkedValueController extends BaseController {

	@Autowired
	private HumanMarkedValueService humanMarkedValueService;
	
	@ModelAttribute
	public HumanMarkedValue get(@RequestParam(required=false) String id) {
		HumanMarkedValue entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = humanMarkedValueService.get(id);
		}
		if (entity == null){
			entity = new HumanMarkedValue();
		}
		return entity;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/value", method = RequestMethod.POST)
	//增加或修改已有标注记录，字段包括measureId、personId、originalValue、value
	public Map<String,String> updateValuesByMeasureId( @RequestBody Map<String,Object> params, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,String> result = Maps.newHashMap();
		if(params.get("measureId")==null || params.get("personId")==null || params.get("originalValue")==null || params.get("value")==null) {
			result.put("result", "error");
			result.put("msg", "Both measureId/personId/originalValue/Value are required.");
			return result;
		}
		String id = Util.md5(""+params.get("measureId")+params.get("personId")+params.get("originalValue"));//构建唯一ID
		params.put("id", id);
		try {
			humanMarkedValueService.upsertMarkedValue(params);
		}catch(Exception ex) {
			result.put("result", "error.");
			result.put("msg", ex.getMessage());
			return result;
		}
		result.put("result", "succeed.");
		result.put("msg", "marked value updated.");
		return result;
	}
	
	@RequiresPermissions("ope:humanMarkedValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(HumanMarkedValue humanMarkedValue, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<HumanMarkedValue> page = humanMarkedValueService.findPage(new Page<HumanMarkedValue>(request, response), humanMarkedValue); 
		model.addAttribute("page", page);
		return "modules/ope/humanMarkedValueList";
	}

	@RequiresPermissions("ope:humanMarkedValue:view")
	@RequestMapping(value = "form")
	public String form(HumanMarkedValue humanMarkedValue, Model model) {
		model.addAttribute("humanMarkedValue", humanMarkedValue);
		return "modules/ope/humanMarkedValueForm";
	}

	@RequiresPermissions("ope:humanMarkedValue:edit")
	@RequestMapping(value = "save")
	public String save(HumanMarkedValue humanMarkedValue, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, humanMarkedValue)){
			return form(humanMarkedValue, model);
		}
		humanMarkedValueService.save(humanMarkedValue);
		addMessage(redirectAttributes, "保存数据标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/humanMarkedValue/?repage";
	}
	
	@RequiresPermissions("ope:humanMarkedValue:edit")
	@RequestMapping(value = "delete")
	public String delete(HumanMarkedValue humanMarkedValue, RedirectAttributes redirectAttributes) {
		humanMarkedValueService.delete(humanMarkedValue);
		addMessage(redirectAttributes, "删除数据标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/humanMarkedValue/?repage";
	}

}