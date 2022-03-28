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
import com.pcitech.iLife.modules.wx.entity.WxPoints;
import com.pcitech.iLife.modules.wx.service.WxPointsService;

/**
 * 微信虚拟豆管理Controller
 * @author ilife
 * @version 2022-03-28
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxPoints")
public class WxPointsController extends BaseController {

	@Autowired
	private WxPointsService wxPointsService;
	
	@ModelAttribute
	public WxPoints get(@RequestParam(required=false) String id) {
		WxPoints entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxPointsService.get(id);
		}
		if (entity == null){
			entity = new WxPoints();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxPoints:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxPoints wxPoints, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxPoints> page = wxPointsService.findPage(new Page<WxPoints>(request, response), wxPoints); 
		model.addAttribute("page", page);
		return "modules/wx/wxPointsList";
	}

	@RequiresPermissions("wx:wxPoints:view")
	@RequestMapping(value = "form")
	public String form(WxPoints wxPoints, Model model) {
		model.addAttribute("wxPoints", wxPoints);
		return "modules/wx/wxPointsForm";
	}

	@RequiresPermissions("wx:wxPoints:edit")
	@RequestMapping(value = "save")
	public String save(WxPoints wxPoints, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxPoints)){
			return form(wxPoints, model);
		}
		wxPointsService.save(wxPoints);
		addMessage(redirectAttributes, "保存微信虚拟豆成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPoints/?repage";
	}
	
	@RequiresPermissions("wx:wxPoints:edit")
	@RequestMapping(value = "delete")
	public String delete(WxPoints wxPoints, RedirectAttributes redirectAttributes) {
		wxPointsService.delete(wxPoints);
		addMessage(redirectAttributes, "删除微信虚拟豆成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPoints/?repage";
	}

}