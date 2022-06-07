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
import com.pcitech.iLife.modules.wx.entity.WxGroup;
import com.pcitech.iLife.modules.wx.service.WxGroupService;

/**
 * 微信群Controller
 * @author ilife
 * @version 2022-06-07
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxGroup")
public class WxGroupController extends BaseController {

	@Autowired
	private WxGroupService wxGroupService;
	
	@ModelAttribute
	public WxGroup get(@RequestParam(required=false) String id) {
		WxGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxGroupService.get(id);
		}
		if (entity == null){
			entity = new WxGroup();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxGroup:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxGroup wxGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxGroup> page = wxGroupService.findPage(new Page<WxGroup>(request, response), wxGroup); 
		model.addAttribute("page", page);
		return "modules/wx/wxGroupList";
	}

	@RequiresPermissions("wx:wxGroup:view")
	@RequestMapping(value = "form")
	public String form(WxGroup wxGroup, Model model) {
		model.addAttribute("wxGroup", wxGroup);
		return "modules/wx/wxGroupForm";
	}

	@RequiresPermissions("wx:wxGroup:edit")
	@RequestMapping(value = "save")
	public String save(WxGroup wxGroup, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxGroup)){
			return form(wxGroup, model);
		}
		wxGroupService.save(wxGroup);
		addMessage(redirectAttributes, "保存微信群成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGroup/?repage";
	}
	
	@RequiresPermissions("wx:wxGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(WxGroup wxGroup, RedirectAttributes redirectAttributes) {
		wxGroupService.delete(wxGroup);
		addMessage(redirectAttributes, "删除微信群成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGroup/?repage";
	}

}