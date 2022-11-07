/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.web;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.diy.entity.ProposalScheme;
import com.pcitech.iLife.modules.diy.entity.ProposalSubtype;
import com.pcitech.iLife.modules.diy.entity.Solution;
import com.pcitech.iLife.modules.diy.service.ProposalSchemeService;
import com.pcitech.iLife.modules.diy.service.ProposalSubtypeService;

/**
 * 个性化定制小类Controller
 * @author chenci
 * @version 2022-10-31
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/proposalSubtype")
public class ProposalSubtypeController extends BaseController {

	@Autowired
	private ProposalSubtypeService proposalSubtypeService;
	@Autowired
	private ProposalSchemeService proposalSchemeService;
	
	@ModelAttribute
	public ProposalSubtype get(@RequestParam(required=false) String id) {
		ProposalSubtype entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = proposalSubtypeService.get(id);
		}
		if (entity == null){
			entity = new ProposalSubtype();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:proposalSubtype:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProposalSubtype proposalSubtype, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProposalSubtype> page = proposalSubtypeService.findPage(new Page<ProposalSubtype>(request, response), proposalSubtype); 
		model.addAttribute("page", page);
		return "modules/diy/proposalSubtypeList";
	}

	@RequiresPermissions("diy:proposalSubtype:view")
	@RequestMapping(value = "form")
	public String form(ProposalSubtype proposalSubtype, Model model) {
		model.addAttribute("proposalSubtype", proposalSubtype);
		return "modules/diy/proposalSubtypeForm";
	}

	@RequiresPermissions("diy:proposalSubtype:edit")
	@RequestMapping(value = "save")
	public String save(ProposalSubtype proposalSubtype, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, proposalSubtype)){
			return form(proposalSubtype, model);
		}
		proposalSubtypeService.save(proposalSubtype);
		addMessage(redirectAttributes, "保存个性化定制小类成功");
		return "redirect:"+Global.getAdminPath()+"/diy/proposalSubtype/?repage";
	}
	
	@RequiresPermissions("diy:proposalSubtype:edit")
	@RequestMapping(value = "delete")
	public String delete(ProposalSubtype proposalSubtype, RedirectAttributes redirectAttributes) {
		proposalSubtypeService.delete(proposalSubtype);
		addMessage(redirectAttributes, "删除个性化定制小类成功");
		return "redirect:"+Global.getAdminPath()+"/diy/proposalSubtype/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/subtypes/{schemeId}", method = RequestMethod.GET)
	public List<ProposalSubtype> listSubtypesByProposalSchemeId(@PathVariable String schemeId) {
		ProposalScheme proposalScheme = proposalSchemeService.get(schemeId);
		if( proposalScheme==null )
			return Lists.newArrayList();
		ProposalSubtype proposalSubtype = new ProposalSubtype();
		proposalSubtype.setScheme(proposalScheme);
		return proposalSubtypeService.findList(proposalSubtype);
	}

	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(ProposalSubtype proposalSubtype, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ProposalSubtype> list = proposalSubtypeService.findList(proposalSubtype);
		for (int i=0; i<list.size(); i++){
			ProposalSubtype e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getScheme().getName()+" "+e.getName());
			mapList.add(map);
			
		}
		return mapList;
	}
	
}