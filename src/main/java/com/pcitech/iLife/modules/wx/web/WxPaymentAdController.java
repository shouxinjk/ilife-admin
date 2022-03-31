/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.wx.entity.WxPaymentAd;
import com.pcitech.iLife.modules.wx.entity.WxPoints;
import com.pcitech.iLife.modules.wx.service.WxPaymentAdService;

/**
 * 置顶广告付款Controller
 * @author ilife
 * @version 2022-03-31
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxPaymentAd")
public class WxPaymentAdController extends BaseController {

	@Autowired
	private WxPaymentAdService wxPaymentAdService;
	
	@ModelAttribute
	public WxPaymentAd get(@RequestParam(required=false) String id) {
		WxPaymentAd entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxPaymentAdService.get(id);
		}
		if (entity == null){
			entity = new WxPaymentAd();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxPaymentAd:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxPaymentAd wxPaymentAd, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxPaymentAd> page = wxPaymentAdService.findPage(new Page<WxPaymentAd>(request, response), wxPaymentAd); 
		model.addAttribute("page", page);
		return "modules/wx/wxPaymentAdList";
	}

	@RequiresPermissions("wx:wxPaymentAd:view")
	@RequestMapping(value = "form")
	public String form(WxPaymentAd wxPaymentAd, Model model) {
		model.addAttribute("wxPaymentAd", wxPaymentAd);
		return "modules/wx/wxPaymentAdForm";
	}

	@RequiresPermissions("wx:wxPaymentAd:edit")
	@RequestMapping(value = "save")
	public String save(WxPaymentAd wxPaymentAd, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxPaymentAd)){
			return form(wxPaymentAd, model);
		}
		wxPaymentAdService.save(wxPaymentAd);
		addMessage(redirectAttributes, "保存指定广告付款成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPaymentAd/?repage";
	}
	
	@RequiresPermissions("wx:wxPaymentAd:edit")
	@RequestMapping(value = "delete")
	public String delete(WxPaymentAd wxPaymentAd, RedirectAttributes redirectAttributes) {
		wxPaymentAdService.delete(wxPaymentAd);
		addMessage(redirectAttributes, "删除指定广告付款成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPaymentAd/?repage";
	}

	/**
	 * 查询指定时间段内已经售出的广告列表
	 */
	@ResponseBody
	@RequestMapping(value = "rest/sold-in-advance/{days}", method = RequestMethod.GET)
	public List<WxPaymentAd> findSoldAds(@PathVariable int days) {
		return wxPaymentAdService.findSoldAds(days);
	}
	
}