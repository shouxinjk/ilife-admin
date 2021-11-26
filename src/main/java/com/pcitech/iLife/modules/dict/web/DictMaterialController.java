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
import com.pcitech.iLife.modules.dict.entity.DictMaterial;
import com.pcitech.iLife.modules.dict.service.DictMaterialService;

/**
 * 材质字典管理Controller
 * @author iLife
 * @version 2021-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/dict/dictMaterial")
public class DictMaterialController extends BaseController {

	@Autowired
	private DictMaterialService dictMaterialService;
	
	@ModelAttribute
	public DictMaterial get(@RequestParam(required=false) String id) {
		DictMaterial entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dictMaterialService.get(id);
		}
		if (entity == null){
			entity = new DictMaterial();
		}
		return entity;
	}
	
	@RequiresPermissions("dict:dictMaterial:view")
	@RequestMapping(value = {"list", ""})
	public String list(DictMaterial dictMaterial, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<DictMaterial> page = dictMaterialService.findPage(new Page<DictMaterial>(request, response), dictMaterial); 
		model.addAttribute("page", page);
		return "modules/dict/dictMaterialList";
	}

	@RequiresPermissions("dict:dictMaterial:view")
	@RequestMapping(value = "form")
	public String form(DictMaterial dictMaterial, Model model) {
		model.addAttribute("dictMaterial", dictMaterial);
		return "modules/dict/dictMaterialForm";
	}

	@RequiresPermissions("dict:dictMaterial:edit")
	@RequestMapping(value = "save")
	public String save(DictMaterial dictMaterial, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, dictMaterial)){
			return form(dictMaterial, model);
		}
		dictMaterialService.save(dictMaterial);
		addMessage(redirectAttributes, "保存材质字典成功");
		return "redirect:"+Global.getAdminPath()+"/dict/dictMaterial/?repage";
	}
	
	@RequiresPermissions("dict:dictMaterial:edit")
	@RequestMapping(value = "delete")
	public String delete(DictMaterial dictMaterial, RedirectAttributes redirectAttributes) {
		dictMaterialService.delete(dictMaterial);
		addMessage(redirectAttributes, "删除材质字典成功");
		return "redirect:"+Global.getAdminPath()+"/dict/dictMaterial/?repage";
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
		dictMaterialService.updateMarkedValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "marked value updated.");
		return result;
	}
	
}