/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.diy.entity.TenantPoints;
import com.pcitech.iLife.modules.diy.service.TenantPointsService;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.util.Util;

/**
 * 租户余额信息Controller
 * @author ilife
 * @version 2023-06-15
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/tenantPoints")
public class TenantPointsController extends BaseController {

	@Autowired
	private TenantPointsService tenantPointsService;
	
	@ModelAttribute
	public TenantPoints get(@RequestParam(required=false) String id) {
		TenantPoints entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tenantPointsService.get(id);
		}
		if (entity == null){
			entity = new TenantPoints();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:tenantPoints:view")
	@RequestMapping(value = {"list", ""})
	public String list(TenantPoints tenantPoints, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TenantPoints> page = tenantPointsService.findPage(new Page<TenantPoints>(request, response), tenantPoints); 
		model.addAttribute("page", page);
		return "modules/diy/tenantPointsList";
	}

	@RequiresPermissions("diy:tenantPoints:view")
	@RequestMapping(value = "form")
	public String form(TenantPoints tenantPoints, Model model) {
		model.addAttribute("tenantPoints", tenantPoints);
		return "modules/diy/tenantPointsForm";
	}

	@RequiresPermissions("diy:tenantPoints:edit")
	@RequestMapping(value = "save")
	public String save(TenantPoints tenantPoints, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, tenantPoints)){
			return form(tenantPoints, model);
		}
		tenantPointsService.save(tenantPoints);
		addMessage(redirectAttributes, "保存租户余额信息成功");
		return "redirect:"+Global.getAdminPath()+"/diy/tenantPoints/?repage";
	}
	
	@RequiresPermissions("diy:tenantPoints:edit")
	@RequestMapping(value = "delete")
	public String delete(TenantPoints tenantPoints, RedirectAttributes redirectAttributes) {
		tenantPointsService.delete(tenantPoints);
		addMessage(redirectAttributes, "删除租户余额信息成功");
		return "redirect:"+Global.getAdminPath()+"/diy/tenantPoints/?repage";
	}

}