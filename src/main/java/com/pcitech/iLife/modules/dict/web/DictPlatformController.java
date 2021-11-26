/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.dict.web;

import java.util.Date;
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

import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.dict.entity.DictPlatform;
import com.pcitech.iLife.modules.dict.service.DictPlatformService;

/**
 * 电商平台字典管理Controller
 * @author iLife
 * @version 2021-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/dict/dictPlatform")
public class DictPlatformController extends BaseController {

	@Autowired
	private DictPlatformService dictPlatformService;
	
	@ModelAttribute
	public DictPlatform get(@RequestParam(required=false) String id) {
		DictPlatform entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dictPlatformService.get(id);
		}
		if (entity == null){
			entity = new DictPlatform();
		}
		return entity;
	}
	
	@RequiresPermissions("dict:dictPlatform:view")
	@RequestMapping(value = {"list", ""})
	public String list(DictPlatform dictPlatform, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<DictPlatform> page = dictPlatformService.findPage(new Page<DictPlatform>(request, response), dictPlatform); 
		model.addAttribute("page", page);
		return "modules/dict/dictPlatformList";
	}

	@RequiresPermissions("dict:dictPlatform:view")
	@RequestMapping(value = "form")
	public String form(DictPlatform dictPlatform, Model model) {
		model.addAttribute("dictPlatform", dictPlatform);
		return "modules/dict/dictPlatformForm";
	}

	@RequiresPermissions("dict:dictPlatform:edit")
	@RequestMapping(value = "save")
	public String save(DictPlatform dictPlatform, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, dictPlatform)){
			return form(dictPlatform, model);
		}
		dictPlatformService.save(dictPlatform);
		addMessage(redirectAttributes, "保存电商平台字典成功");
		return "redirect:"+Global.getAdminPath()+"/dict/dictPlatform/?repage";
	}
	
	@RequiresPermissions("dict:dictPlatform:edit")
	@RequestMapping(value = "delete")
	public String delete(DictPlatform dictPlatform, RedirectAttributes redirectAttributes) {
		dictPlatformService.delete(dictPlatform);
		addMessage(redirectAttributes, "删除电商平台字典成功");
		return "redirect:"+Global.getAdminPath()+"/dict/dictPlatform/?repage";
	}

	@ResponseBody
	@RequestMapping(value = "rest/updateMarkedValue")
	//更新属性值标注：markedvalue。自动添加更新日期
	public Map<String,String> updateMarkedValue( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double markedValue,
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("markedValue", markedValue);
		params.put("updateDate", new Date());
		dictPlatformService.updateMarkedValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "marked value updated.");
		return result;
	}
	
}