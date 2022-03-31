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
import com.pcitech.iLife.modules.wx.entity.WxPaymentPoint;
import com.pcitech.iLife.modules.wx.service.WxPaymentPointService;

/**
 * 阅豆付款Controller
 * @author ilife
 * @version 2022-03-31
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxPaymentPoint")
public class WxPaymentPointController extends BaseController {

	@Autowired
	private WxPaymentPointService wxPaymentPointService;
	
	@ModelAttribute
	public WxPaymentPoint get(@RequestParam(required=false) String id) {
		WxPaymentPoint entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxPaymentPointService.get(id);
		}
		if (entity == null){
			entity = new WxPaymentPoint();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxPaymentPoint:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxPaymentPoint wxPaymentPoint, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxPaymentPoint> page = wxPaymentPointService.findPage(new Page<WxPaymentPoint>(request, response), wxPaymentPoint); 
		model.addAttribute("page", page);
		return "modules/wx/wxPaymentPointList";
	}

	@RequiresPermissions("wx:wxPaymentPoint:view")
	@RequestMapping(value = "form")
	public String form(WxPaymentPoint wxPaymentPoint, Model model) {
		model.addAttribute("wxPaymentPoint", wxPaymentPoint);
		return "modules/wx/wxPaymentPointForm";
	}

	@RequiresPermissions("wx:wxPaymentPoint:edit")
	@RequestMapping(value = "save")
	public String save(WxPaymentPoint wxPaymentPoint, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxPaymentPoint)){
			return form(wxPaymentPoint, model);
		}
		wxPaymentPointService.save(wxPaymentPoint);
		addMessage(redirectAttributes, "保存阅豆付款成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPaymentPoint/?repage";
	}
	
	@RequiresPermissions("wx:wxPaymentPoint:edit")
	@RequestMapping(value = "delete")
	public String delete(WxPaymentPoint wxPaymentPoint, RedirectAttributes redirectAttributes) {
		wxPaymentPointService.delete(wxPaymentPoint);
		addMessage(redirectAttributes, "删除阅豆付款成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPaymentPoint/?repage";
	}

}