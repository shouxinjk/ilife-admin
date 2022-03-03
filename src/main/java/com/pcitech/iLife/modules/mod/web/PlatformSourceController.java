/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.PlatformSource;
import com.pcitech.iLife.modules.mod.service.PlatformSourceService;

/**
 * 商品数据来源Controller
 * @author ilife
 * @version 2022-03-03
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/platformSource")
public class PlatformSourceController extends BaseController {

	@Autowired
	private PlatformSourceService platformSourceService;
	
	@ModelAttribute
	public PlatformSource get(@RequestParam(required=false) String id) {
		PlatformSource entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = platformSourceService.get(id);
		}
		if (entity == null){
			entity = new PlatformSource();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:platformSource:view")
	@RequestMapping(value = {"list", ""})
	public String list(PlatformSource platformSource, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PlatformSource> page = platformSourceService.findPage(new Page<PlatformSource>(request, response), platformSource); 
		model.addAttribute("page", page);
		return "modules/mod/platformSourceList";
	}

	@RequiresPermissions("mod:platformSource:view")
	@RequestMapping(value = "form")
	public String form(PlatformSource platformSource, Model model) {
		model.addAttribute("platformSource", platformSource);
		return "modules/mod/platformSourceForm";
	}

	@RequiresPermissions("mod:platformSource:edit")
	@RequestMapping(value = "save")
	public String save(PlatformSource platformSource, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, platformSource)){
			return form(platformSource, model);
		}
		platformSourceService.save(platformSource);
		addMessage(redirectAttributes, "保存商品数据来源成功");
		return "redirect:"+Global.getAdminPath()+"/mod/platformSource/?repage";
	}
	
	@RequiresPermissions("mod:platformSource:edit")
	@RequestMapping(value = "delete")
	public String delete(PlatformSource platformSource, RedirectAttributes redirectAttributes) {
		platformSourceService.delete(platformSource);
		addMessage(redirectAttributes, "删除商品数据来源成功");
		return "redirect:"+Global.getAdminPath()+"/mod/platformSource/?repage";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "rest/active")
	//查询所有可用source
	public List<PlatformSource> findActiveSources(HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		PlatformSource platformSource = new PlatformSource();
		platformSource.setStatus("active");
		return platformSourceService.findActiveSources(platformSource);
	}

}