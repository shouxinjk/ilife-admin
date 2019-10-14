/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.CommissionScheme;
import com.pcitech.iLife.modules.mod.service.CommissionSchemeService;

/**
 * 佣金规则设置Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/commissionScheme")
public class CommissionSchemeController extends BaseController {

	@Autowired
	private CommissionSchemeService commissionSchemeService;
	
	@ModelAttribute
	public CommissionScheme get(@RequestParam(required=false) String id) {
		CommissionScheme entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = commissionSchemeService.get(id);
		}
		if (entity == null){
			entity = new CommissionScheme();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:commissionScheme:view")
	@RequestMapping(value = {"list", ""})
	public String list(CommissionScheme commissionScheme, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CommissionScheme> page = commissionSchemeService.findPage(new Page<CommissionScheme>(request, response), commissionScheme); 
		model.addAttribute("page", page);
		return "modules/mod/commissionSchemeList";
	}

	@RequiresPermissions("mod:commissionScheme:view")
	@RequestMapping(value = "form")
	public String form(CommissionScheme commissionScheme, Model model) {
		model.addAttribute("commissionScheme", commissionScheme);
		return "modules/mod/commissionSchemeForm";
	}

	@RequiresPermissions("mod:commissionScheme:edit")
	@RequestMapping(value = "save")
	public String save(CommissionScheme commissionScheme, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, commissionScheme)){
			return form(commissionScheme, model);
		}
		commissionSchemeService.save(commissionScheme);
		addMessage(redirectAttributes, "保存佣金规则设置成功");
		return "redirect:"+Global.getAdminPath()+"/mod/commissionScheme/?repage";
	}
	
	@RequiresPermissions("mod:commissionScheme:edit")
	@RequestMapping(value = "delete")
	public String delete(CommissionScheme commissionScheme, RedirectAttributes redirectAttributes) {
		commissionSchemeService.delete(commissionScheme);
		addMessage(redirectAttributes, "删除佣金规则设置成功");
		return "redirect:"+Global.getAdminPath()+"/mod/commissionScheme/?repage";
	}

}