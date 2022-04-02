/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

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
import com.pcitech.iLife.modules.wx.entity.WxTopping;
import com.pcitech.iLife.modules.wx.service.WxToppingService;

/**
 * 置顶记录Controller
 * @author ilife
 * @version 2022-04-02
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxTopping")
public class WxToppingController extends BaseController {

	@Autowired
	private WxToppingService wxToppingService;
	
	@ModelAttribute
	public WxTopping get(@RequestParam(required=false) String id) {
		WxTopping entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxToppingService.get(id);
		}
		if (entity == null){
			entity = new WxTopping();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxTopping:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxTopping wxTopping, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxTopping> page = wxToppingService.findPage(new Page<WxTopping>(request, response), wxTopping); 
		model.addAttribute("page", page);
		return "modules/wx/wxToppingList";
	}

	@RequiresPermissions("wx:wxTopping:view")
	@RequestMapping(value = "form")
	public String form(WxTopping wxTopping, Model model) {
		model.addAttribute("wxTopping", wxTopping);
		return "modules/wx/wxToppingForm";
	}

	@RequiresPermissions("wx:wxTopping:edit")
	@RequestMapping(value = "save")
	public String save(WxTopping wxTopping, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxTopping)){
			return form(wxTopping, model);
		}
		wxToppingService.save(wxTopping);
		addMessage(redirectAttributes, "保存置顶记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxTopping/?repage";
	}
	
	@RequiresPermissions("wx:wxTopping:edit")
	@RequestMapping(value = "delete")
	public String delete(WxTopping wxTopping, RedirectAttributes redirectAttributes) {
		wxToppingService.delete(wxTopping);
		addMessage(redirectAttributes, "删除置顶记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxTopping/?repage";
	}

}