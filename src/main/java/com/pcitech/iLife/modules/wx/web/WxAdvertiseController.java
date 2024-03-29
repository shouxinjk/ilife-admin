/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.wx.entity.WxAdvertise;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.service.WxAdvertiseService;
import com.pcitech.iLife.util.Util;

/**
 * 微信广告位管理Controller
 * @author ilife
 * @version 2022-03-28
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxAdvertise")
public class WxAdvertiseController extends BaseController {

	@Autowired
	private WxAdvertiseService wxAdvertiseService;
	
	@ModelAttribute
	public WxAdvertise get(@RequestParam(required=false) String id) {
		WxAdvertise entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxAdvertiseService.get(id);
		}
		if (entity == null){
			entity = new WxAdvertise();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxAdvertise:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxAdvertise wxAdvertise, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxAdvertise> page = wxAdvertiseService.findPage(new Page<WxAdvertise>(request, response), wxAdvertise); 
		model.addAttribute("page", page);
		return "modules/wx/wxAdvertiseList";
	}

	@RequiresPermissions("wx:wxAdvertise:view")
	@RequestMapping(value = "form")
	public String form(WxAdvertise wxAdvertise, Model model) {
		model.addAttribute("wxAdvertise", wxAdvertise);
		return "modules/wx/wxAdvertiseForm";
	}

	@RequiresPermissions("wx:wxAdvertise:edit")
	@RequestMapping(value = "save")
	public String save(WxAdvertise wxAdvertise, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxAdvertise)){
			return form(wxAdvertise, model);
		}
		wxAdvertiseService.save(wxAdvertise);
		addMessage(redirectAttributes, "保存微信广告位成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxAdvertise/?repage";
	}
	
	@RequiresPermissions("wx:wxAdvertise:edit")
	@RequestMapping(value = "delete")
	public String delete(WxAdvertise wxAdvertise, RedirectAttributes redirectAttributes) {
		wxAdvertiseService.delete(wxAdvertise);
		addMessage(redirectAttributes, "删除微信广告位成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxAdvertise/?repage";
	}
	
	@RequiresPermissions("wx:wxAdvertise:edit")
	@RequestMapping(value = "clone")
	public String clone(WxAdvertise wxAdvertise, RedirectAttributes redirectAttributes) {
		WxAdvertise copy = wxAdvertiseService.get(wxAdvertise);
		copy.setName(copy.getName()+" copy");
		copy.setWeight(copy.getWeight()-1);
		copy.setId(Util.get32UUID());
		copy.setIsNewRecord(true);
		copy.setCreateDate(new Date());
		copy.setUpdateDate(new Date());
		wxAdvertiseService.save(copy);
		addMessage(redirectAttributes, "克隆微信广告位成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxAdvertise/?repage";
	}
	
	/**
	 * 按类型获取所有可用广告位
	 */
	@ResponseBody
	@RequestMapping(value = "rest/ads/{type}", method = RequestMethod.GET)
	public List<WxAdvertise> listAdsByType( @PathVariable String type) {
		return wxAdvertiseService.listAdsByType(type);
	}

}