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
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.service.WxArticleService;

/**
 * 微信文章管理Controller
 * @author ilife
 * @version 2022-03-28
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxArticle")
public class WxArticleController extends BaseController {

	@Autowired
	private WxArticleService wxArticleService;
	
	@ModelAttribute
	public WxArticle get(@RequestParam(required=false) String id) {
		WxArticle entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxArticleService.get(id);
		}
		if (entity == null){
			entity = new WxArticle();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxArticle:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxArticle wxArticle, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxArticle> page = wxArticleService.findPage(new Page<WxArticle>(request, response), wxArticle); 
		model.addAttribute("page", page);
		return "modules/wx/wxArticleList";
	}

	@RequiresPermissions("wx:wxArticle:view")
	@RequestMapping(value = "form")
	public String form(WxArticle wxArticle, Model model) {
		model.addAttribute("wxArticle", wxArticle);
		return "modules/wx/wxArticleForm";
	}

	@RequiresPermissions("wx:wxArticle:edit")
	@RequestMapping(value = "save")
	public String save(WxArticle wxArticle, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxArticle)){
			return form(wxArticle, model);
		}
		wxArticleService.save(wxArticle);
		addMessage(redirectAttributes, "保存微信文章成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxArticle/?repage";
	}
	
	@RequiresPermissions("wx:wxArticle:edit")
	@RequestMapping(value = "delete")
	public String delete(WxArticle wxArticle, RedirectAttributes redirectAttributes) {
		wxArticleService.delete(wxArticle);
		addMessage(redirectAttributes, "删除微信文章成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxArticle/?repage";
	}

}