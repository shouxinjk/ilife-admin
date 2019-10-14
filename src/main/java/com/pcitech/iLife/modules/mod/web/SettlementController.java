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
import com.pcitech.iLife.modules.mod.entity.Settlement;
import com.pcitech.iLife.modules.mod.service.SettlementService;

/**
 * 结算管理Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/settlement")
public class SettlementController extends BaseController {

	@Autowired
	private SettlementService settlementService;
	
	@ModelAttribute
	public Settlement get(@RequestParam(required=false) String id) {
		Settlement entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = settlementService.get(id);
		}
		if (entity == null){
			entity = new Settlement();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:settlement:view")
	@RequestMapping(value = {"list", ""})
	public String list(Settlement settlement, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Settlement> page = settlementService.findPage(new Page<Settlement>(request, response), settlement); 
		model.addAttribute("page", page);
		return "modules/mod/settlementList";
	}

	@RequiresPermissions("mod:settlement:view")
	@RequestMapping(value = "form")
	public String form(Settlement settlement, Model model) {
		model.addAttribute("settlement", settlement);
		return "modules/mod/settlementForm";
	}

	@RequiresPermissions("mod:settlement:edit")
	@RequestMapping(value = "save")
	public String save(Settlement settlement, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, settlement)){
			return form(settlement, model);
		}
		settlementService.save(settlement);
		addMessage(redirectAttributes, "保存结算管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/settlement/?repage";
	}
	
	@RequiresPermissions("mod:settlement:edit")
	@RequestMapping(value = "delete")
	public String delete(Settlement settlement, RedirectAttributes redirectAttributes) {
		settlementService.delete(settlement);
		addMessage(redirectAttributes, "删除结算管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/settlement/?repage";
	}

}