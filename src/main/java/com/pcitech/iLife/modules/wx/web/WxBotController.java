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
import com.pcitech.iLife.modules.wx.entity.WxBot;
import com.pcitech.iLife.modules.wx.service.WxBotService;

/**
 * 微信机器人Controller
 * @author ilife
 * @version 2022-06-07
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxBot")
public class WxBotController extends BaseController {

	@Autowired
	private WxBotService wxBotService;
	
	@ModelAttribute
	public WxBot get(@RequestParam(required=false) String id) {
		WxBot entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxBotService.get(id);
		}
		if (entity == null){
			entity = new WxBot();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxBot:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxBot wxBot, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxBot> page = wxBotService.findPage(new Page<WxBot>(request, response), wxBot); 
		model.addAttribute("page", page);
		return "modules/wx/wxBotList";
	}

	@RequiresPermissions("wx:wxBot:view")
	@RequestMapping(value = "form")
	public String form(WxBot wxBot, Model model) {
		model.addAttribute("wxBot", wxBot);
		return "modules/wx/wxBotForm";
	}

	@RequiresPermissions("wx:wxBot:edit")
	@RequestMapping(value = "save")
	public String save(WxBot wxBot, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxBot)){
			return form(wxBot, model);
		}
		wxBotService.save(wxBot);
		addMessage(redirectAttributes, "保存微信机器人成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxBot/?repage";
	}
	
	@RequiresPermissions("wx:wxBot:edit")
	@RequestMapping(value = "delete")
	public String delete(WxBot wxBot, RedirectAttributes redirectAttributes) {
		wxBotService.delete(wxBot);
		addMessage(redirectAttributes, "删除微信机器人成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxBot/?repage";
	}

}