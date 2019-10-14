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
import com.pcitech.iLife.modules.mod.entity.ProfitShareItem;
import com.pcitech.iLife.modules.mod.service.ProfitShareItemService;

/**
 * 分润规则明细Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/profitShareItem")
public class ProfitShareItemController extends BaseController {

	@Autowired
	private ProfitShareItemService profitShareItemService;
	
	@ModelAttribute
	public ProfitShareItem get(@RequestParam(required=false) String id) {
		ProfitShareItem entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = profitShareItemService.get(id);
		}
		if (entity == null){
			entity = new ProfitShareItem();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:profitShareItem:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProfitShareItem profitShareItem, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProfitShareItem> page = profitShareItemService.findPage(new Page<ProfitShareItem>(request, response), profitShareItem); 
		model.addAttribute("page", page);
		return "modules/mod/profitShareItemList";
	}

	@RequiresPermissions("mod:profitShareItem:view")
	@RequestMapping(value = "form")
	public String form(ProfitShareItem profitShareItem, Model model) {
		model.addAttribute("profitShareItem", profitShareItem);
		return "modules/mod/profitShareItemForm";
	}

	@RequiresPermissions("mod:profitShareItem:edit")
	@RequestMapping(value = "save")
	public String save(ProfitShareItem profitShareItem, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, profitShareItem)){
			return form(profitShareItem, model);
		}
		profitShareItemService.save(profitShareItem);
		addMessage(redirectAttributes, "保存分润规则明细成功");
		return "redirect:"+Global.getAdminPath()+"/mod/profitShareItem/?repage";
	}
	
	@RequiresPermissions("mod:profitShareItem:edit")
	@RequestMapping(value = "delete")
	public String delete(ProfitShareItem profitShareItem, RedirectAttributes redirectAttributes) {
		profitShareItemService.delete(profitShareItem);
		addMessage(redirectAttributes, "删除分润规则明细成功");
		return "redirect:"+Global.getAdminPath()+"/mod/profitShareItem/?repage";
	}

}