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
import com.pcitech.iLife.modules.mod.entity.Badge;
import com.pcitech.iLife.modules.mod.entity.Credit;
import com.pcitech.iLife.modules.mod.service.CreditService;

/**
 * 贡献度Controller
 * @author ilife
 * @version 2023-01-18
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/credit")
public class CreditController extends BaseController {

	@Autowired
	private CreditService creditService;
	
	@ModelAttribute
	public Credit get(@RequestParam(required=false) String id) {
		Credit entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditService.get(id);
		}
		if (entity == null){
			entity = new Credit();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:credit:view")
	@RequestMapping(value = {"list", ""})
	public String list(Credit credit, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Credit> page = creditService.findPage(new Page<Credit>(request, response), credit); 
		model.addAttribute("page", page);
		return "modules/mod/creditList";
	}

	@RequiresPermissions("mod:credit:view")
	@RequestMapping(value = "form")
	public String form(Credit credit, Model model) {
		model.addAttribute("credit", credit);
		return "modules/mod/creditForm";
	}

	@RequiresPermissions("mod:credit:edit")
	@RequestMapping(value = "save")
	public String save(Credit credit, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, credit)){
			return form(credit, model);
		}
		creditService.save(credit);
		addMessage(redirectAttributes, "保存贡献度成功");
		return "redirect:"+Global.getAdminPath()+"/mod/credit/?repage";
	}
	
	@RequiresPermissions("mod:credit:edit")
	@RequestMapping(value = "delete")
	public String delete(Credit credit, RedirectAttributes redirectAttributes) {
		creditService.delete(credit);
		addMessage(redirectAttributes, "删除贡献度成功");
		return "redirect:"+Global.getAdminPath()+"/mod/credit/?repage";
	}

	/**
	 * 获取贡献值列表
	 */
	@ResponseBody
	@RequestMapping(value = "rest/credits", method = RequestMethod.GET)
	public List<Credit> listCreditsByPriority() {
		Credit credit = new Credit();
		return creditService.findList(credit);
	}
}