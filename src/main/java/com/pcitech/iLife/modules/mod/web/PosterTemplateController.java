/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.PosterTemplate;
import com.pcitech.iLife.modules.mod.service.PosterTemplateService;

/**
 * 海报模板管理Controller
 * @author ilife
 * @version 2021-12-28
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/posterTemplate")
public class PosterTemplateController extends BaseController {

	@Autowired
	private PosterTemplateService posterTemplateService;
	
	@ModelAttribute
	public PosterTemplate get(@RequestParam(required=false) String id) {
		PosterTemplate entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = posterTemplateService.get(id);
		}
		if (entity == null){
			entity = new PosterTemplate();
		}
		return entity;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/item-templates", method = RequestMethod.GET)
	public List<PosterTemplate> getItemTemplates(String categoryId){
		return posterTemplateService.findItemList(categoryId);
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/board-templates", method = RequestMethod.GET)
	public List<PosterTemplate> getBoardTemplates(){
		return posterTemplateService.findBoardList();
	}
	
	@RequiresPermissions("mod:posterTemplate:view")
	@RequestMapping(value = {"list", ""})
	public String list(PosterTemplate posterTemplate, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PosterTemplate> page = posterTemplateService.findPage(new Page<PosterTemplate>(request, response), posterTemplate); 
		model.addAttribute("page", page);
		return "modules/mod/posterTemplateList";
	}

	@RequiresPermissions("mod:posterTemplate:view")
	@RequestMapping(value = "form")
	public String form(PosterTemplate posterTemplate, Model model) {
		model.addAttribute("posterTemplate", posterTemplate);
		return "modules/mod/posterTemplateForm";
	}

	@RequiresPermissions("mod:posterTemplate:edit")
	@RequestMapping(value = "save")
	public String save(PosterTemplate posterTemplate, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, posterTemplate)){
			return form(posterTemplate, model);
		}
		posterTemplateService.save(posterTemplate);
		addMessage(redirectAttributes, "保存海报模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/posterTemplate/?repage";
	}
	
	@RequiresPermissions("mod:posterTemplate:edit")
	@RequestMapping(value = "delete")
	public String delete(PosterTemplate posterTemplate, RedirectAttributes redirectAttributes) {
		posterTemplateService.delete(posterTemplate);
		addMessage(redirectAttributes, "删除海报模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/posterTemplate/?repage";
	}

}