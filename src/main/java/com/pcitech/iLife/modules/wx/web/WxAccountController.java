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
import com.pcitech.iLife.modules.wx.entity.WxAccount;
import com.pcitech.iLife.modules.wx.service.WxAccountService;

/**
 * 微信公众号管理Controller
 * @author ilife
 * @version 2022-03-28
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxAccount")
public class WxAccountController extends BaseController {

	@Autowired
	private WxAccountService wxAccountService;
	
	@ModelAttribute
	public WxAccount get(@RequestParam(required=false) String id) {
		WxAccount entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxAccountService.get(id);
		}
		if (entity == null){
			entity = new WxAccount();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxAccount:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxAccount wxAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxAccount> page = wxAccountService.findPage(new Page<WxAccount>(request, response), wxAccount); 
		model.addAttribute("page", page);
		return "modules/wx/wxAccountList";
	}

	@RequiresPermissions("wx:wxAccount:view")
	@RequestMapping(value = "form")
	public String form(WxAccount wxAccount, Model model) {
		model.addAttribute("wxAccount", wxAccount);
		return "modules/wx/wxAccountForm";
	}

	@RequiresPermissions("wx:wxAccount:edit")
	@RequestMapping(value = "save")
	public String save(WxAccount wxAccount, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxAccount)){
			return form(wxAccount, model);
		}
		wxAccountService.save(wxAccount);
		addMessage(redirectAttributes, "保存微信公众号成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxAccount/?repage";
	}
	
	@RequiresPermissions("wx:wxAccount:edit")
	@RequestMapping(value = "delete")
	public String delete(WxAccount wxAccount, RedirectAttributes redirectAttributes) {
		wxAccountService.delete(wxAccount);
		addMessage(redirectAttributes, "删除微信公众号成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxAccount/?repage";
	}

}