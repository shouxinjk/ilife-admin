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
import com.pcitech.iLife.modules.wx.entity.WxReads;
import com.pcitech.iLife.modules.wx.service.WxReadsService;

/**
 * 阅读记录Controller
 * @author ilife
 * @version 2022-03-31
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxReads")
public class WxReadsController extends BaseController {

	@Autowired
	private WxReadsService wxReadsService;
	
	@ModelAttribute
	public WxReads get(@RequestParam(required=false) String id) {
		WxReads entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxReadsService.get(id);
		}
		if (entity == null){
			entity = new WxReads();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxReads:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxReads wxReads, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxReads> page = wxReadsService.findPage(new Page<WxReads>(request, response), wxReads); 
		model.addAttribute("page", page);
		return "modules/wx/wxReadsList";
	}

	@RequiresPermissions("wx:wxReads:view")
	@RequestMapping(value = "form")
	public String form(WxReads wxReads, Model model) {
		model.addAttribute("wxReads", wxReads);
		return "modules/wx/wxReadsForm";
	}

	@RequiresPermissions("wx:wxReads:edit")
	@RequestMapping(value = "save")
	public String save(WxReads wxReads, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxReads)){
			return form(wxReads, model);
		}
		wxReadsService.save(wxReads);
		addMessage(redirectAttributes, "保存阅读记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxReads/?repage";
	}
	
	@RequiresPermissions("wx:wxReads:edit")
	@RequestMapping(value = "delete")
	public String delete(WxReads wxReads, RedirectAttributes redirectAttributes) {
		wxReadsService.delete(wxReads);
		addMessage(redirectAttributes, "删除阅读记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxReads/?repage";
	}

}