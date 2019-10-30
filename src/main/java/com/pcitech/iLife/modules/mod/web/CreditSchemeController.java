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
import com.pcitech.iLife.modules.mod.entity.CreditScheme;
import com.pcitech.iLife.modules.mod.service.CreditSchemeService;

/**
 * 积分规则Controller
 * @author qchzhu
 * @version 2019-10-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/creditScheme")
public class CreditSchemeController extends BaseController {

	@Autowired
	private CreditSchemeService creditSchemeService;
	
	@ModelAttribute
	public CreditScheme get(@RequestParam(required=false) String id) {
		CreditScheme entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditSchemeService.get(id);
		}
		if (entity == null){
			entity = new CreditScheme();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:creditScheme:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditScheme creditScheme, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditScheme> page = creditSchemeService.findPage(new Page<CreditScheme>(request, response), creditScheme); 
		model.addAttribute("page", page);
		return "modules/mod/creditSchemeList";
	}

	@RequiresPermissions("mod:creditScheme:view")
	@RequestMapping(value = "form")
	public String form(CreditScheme creditScheme, Model model) {
		model.addAttribute("creditScheme", creditScheme);
		return "modules/mod/creditSchemeForm";
	}

	@RequiresPermissions("mod:creditScheme:edit")
	@RequestMapping(value = "save")
	public String save(CreditScheme creditScheme, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditScheme)){
			return form(creditScheme, model);
		}
		creditSchemeService.save(creditScheme);
		addMessage(redirectAttributes, "保存积分规则管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/creditScheme/?repage";
	}
	
	@RequiresPermissions("mod:creditScheme:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditScheme creditScheme, RedirectAttributes redirectAttributes) {
		creditSchemeService.delete(creditScheme);
		addMessage(redirectAttributes, "删除积分规则管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/creditScheme/?repage";
	}

}