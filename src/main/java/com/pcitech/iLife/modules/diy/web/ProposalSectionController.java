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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.diy.entity.ProposalScheme;
import com.pcitech.iLife.modules.diy.entity.ProposalSection;
import com.pcitech.iLife.modules.diy.entity.ProposalSubtype;
import com.pcitech.iLife.modules.diy.service.ProposalSchemeService;
import com.pcitech.iLife.modules.diy.service.ProposalSectionService;

/**
 * 个性化定制章节Controller
 * @author chenci
 * @version 2022-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/proposalSection")
public class ProposalSectionController extends BaseController {

	@Autowired
	private ProposalSectionService proposalSectionService;
	@Autowired
	private ProposalSchemeService proposalSchemeService;
	
	@ModelAttribute
	public ProposalSection get(@RequestParam(required=false) String id) {
		ProposalSection entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = proposalSectionService.get(id);
		}
		if (entity == null){
			entity = new ProposalSection();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:proposalSection:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProposalSection proposalSection, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProposalSection> page = proposalSectionService.findPage(new Page<ProposalSection>(request, response), proposalSection); 
		model.addAttribute("page", page);
		return "modules/diy/proposalSectionList";
	}

	@RequiresPermissions("diy:proposalSection:view")
	@RequestMapping(value = "form")
	public String form(ProposalSection proposalSection, Model model) {
		model.addAttribute("proposalSection", proposalSection);
		return "modules/diy/proposalSectionForm";
	}

	@RequiresPermissions("diy:proposalSection:edit")
	@RequestMapping(value = "save")
	public String save(ProposalSection proposalSection, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, proposalSection)){
			return form(proposalSection, model);
		}
		proposalSectionService.save(proposalSection);
		addMessage(redirectAttributes, "保存个性化定制章节成功");
		return "redirect:"+Global.getAdminPath()+"/diy/proposalSection/?repage";
	}
	
	@RequiresPermissions("diy:proposalSection:edit")
	@RequestMapping(value = "delete")
	public String delete(ProposalSection proposalSection, RedirectAttributes redirectAttributes) {
		proposalSectionService.delete(proposalSection);
		addMessage(redirectAttributes, "删除个性化定制章节成功");
		return "redirect:"+Global.getAdminPath()+"/diy/proposalSection/?repage";
	}

	/**
	 * 查询所有可用定制主题章节
	 * @param proposalSection
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(ProposalSection proposalSection, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		while(proposalSection!=null) { //需要循环获取上级scheme下的section
			List<ProposalSection> list = proposalSectionService.findList(proposalSection);
			for (int i=0; i<list.size(); i++){
				ProposalSection e = list.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", "0");
				map.put("pIds", "0");
				map.put("name", e.getName());
				
				if (mapList.indexOf(map) == mapList.lastIndexOf(map)) { //不存在则加入
					mapList.add(map);
				}
				
			}
			ProposalScheme scheme = proposalSchemeService.get(proposalSection.getScheme());
			ProposalScheme parentScheme = null;
			if(scheme!=null) {
				parentScheme = proposalSchemeService.get(scheme.getParent());
				if(parentScheme!=null)
					proposalSection.setScheme(parentScheme);//查询上级关联的section
				else
					proposalSection = null;
			}else {
				proposalSection = null;
			}
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/sections/{schemeId}", method = RequestMethod.GET)
	public List<ProposalSection> listSectionsByProposalSchemeId(@PathVariable String schemeId) {
		ProposalScheme proposalScheme = proposalSchemeService.get(schemeId);
		if( proposalScheme==null )
			return Lists.newArrayList();
		ProposalSection proposalSection = new ProposalSection();
		proposalSection.setScheme(proposalScheme);
		return proposalSectionService.findList(proposalSection);
	}
	
}