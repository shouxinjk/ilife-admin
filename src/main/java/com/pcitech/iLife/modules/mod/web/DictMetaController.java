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
import com.pcitech.iLife.modules.mod.entity.DictMeta;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.service.DictMetaService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;

/**
 * 业务字典定义Controller
 * @author chenci
 * @version 2022-10-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/dictMeta")
public class DictMetaController extends BaseController {

	@Autowired
	private DictMetaService dictMetaService;
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public DictMeta get(@RequestParam(required=false) String id) {
		DictMeta entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dictMetaService.get(id);
		}
		if (entity == null){
			entity = new DictMeta();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:dictMeta:view")
	@RequestMapping(value = {"list", ""})
	public String list(DictMeta dictMeta, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<DictMeta> page = dictMetaService.findPage(new Page<DictMeta>(request, response), dictMeta); 
		model.addAttribute("page", page);
		return "modules/mod/dictMetaList";
	}

	@RequiresPermissions("mod:dictMeta:view")
	@RequestMapping(value = "form")
	public String form(DictMeta dictMeta, Model model) {
		model.addAttribute("dictMeta", dictMeta);
		return "modules/mod/dictMetaForm";
	}

	@RequiresPermissions("mod:dictMeta:edit")
	@RequestMapping(value = "save")
	public String save(DictMeta dictMeta, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, dictMeta)){
			return form(dictMeta, model);
		}
		dictMetaService.save(dictMeta);
		addMessage(redirectAttributes, "保存业务字典定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/dictMeta/?repage";
	}
	
	@RequiresPermissions("mod:dictMeta:edit")
	@RequestMapping(value = "delete")
	public String delete(DictMeta dictMeta, RedirectAttributes redirectAttributes) {
		dictMetaService.delete(dictMeta);
		addMessage(redirectAttributes, "删除业务字典定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/dictMeta/?repage";
	}
	
}