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
import com.pcitech.iLife.modules.wx.entity.WxSubscribes;
import com.pcitech.iLife.modules.wx.service.WxSubscribesService;

/**
 * 关注记录Controller
 * @author ilife
 * @version 2022-03-31
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxSubscribes")
public class WxSubscribesController extends BaseController {

	@Autowired
	private WxSubscribesService wxSubscribesService;
	
	@ModelAttribute
	public WxSubscribes get(@RequestParam(required=false) String id) {
		WxSubscribes entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxSubscribesService.get(id);
		}
		if (entity == null){
			entity = new WxSubscribes();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxSubscribes:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxSubscribes wxSubscribes, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxSubscribes> page = wxSubscribesService.findPage(new Page<WxSubscribes>(request, response), wxSubscribes); 
		model.addAttribute("page", page);
		return "modules/wx/wxSubscribesList";
	}

	@RequiresPermissions("wx:wxSubscribes:view")
	@RequestMapping(value = "form")
	public String form(WxSubscribes wxSubscribes, Model model) {
		model.addAttribute("wxSubscribes", wxSubscribes);
		return "modules/wx/wxSubscribesForm";
	}

	@RequiresPermissions("wx:wxSubscribes:edit")
	@RequestMapping(value = "save")
	public String save(WxSubscribes wxSubscribes, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxSubscribes)){
			return form(wxSubscribes, model);
		}
		wxSubscribesService.save(wxSubscribes);
		addMessage(redirectAttributes, "保存关注记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxSubscribes/?repage";
	}
	
	@RequiresPermissions("wx:wxSubscribes:edit")
	@RequestMapping(value = "delete")
	public String delete(WxSubscribes wxSubscribes, RedirectAttributes redirectAttributes) {
		wxSubscribesService.delete(wxSubscribes);
		addMessage(redirectAttributes, "删除关注记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxSubscribes/?repage";
	}

}