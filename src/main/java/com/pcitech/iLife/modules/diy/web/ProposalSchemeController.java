/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.web;

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
import com.pcitech.iLife.modules.diy.entity.GuideBook;
import com.pcitech.iLife.modules.diy.entity.GuideBookProposal;
import com.pcitech.iLife.modules.diy.entity.JsonForm;
import com.pcitech.iLife.modules.diy.entity.ProposalScheme;
import com.pcitech.iLife.modules.diy.entity.ProposalSection;
import com.pcitech.iLife.modules.diy.entity.ProposalSubtype;
import com.pcitech.iLife.modules.diy.service.GuideBookProposalService;
import com.pcitech.iLife.modules.diy.service.GuideBookService;
import com.pcitech.iLife.modules.diy.service.ProposalSchemeService;
import com.pcitech.iLife.modules.diy.service.ProposalSectionService;
import com.pcitech.iLife.modules.diy.service.ProposalSubtypeService;
import com.pcitech.iLife.util.Util;

/**
 * 个性化定制模板Controller
 * @author chenci
 * @version 2022-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/proposalScheme")
public class ProposalSchemeController extends BaseController {

	@Autowired
	private ProposalSchemeService proposalSchemeService;
	@Autowired
	private GuideBookService guideBookService;
	@Autowired
	private ProposalSectionService proposalSectionService;
	@Autowired
	private ProposalSubtypeService proposalSubtypeService;
	@Autowired
	private GuideBookProposalService guideBookProposalService;
	
	@ModelAttribute
	public ProposalScheme get(@RequestParam(required=false) String id) {
		ProposalScheme entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = proposalSchemeService.get(id);
		}
		if (entity == null){
			entity = new ProposalScheme();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:proposalScheme:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProposalScheme proposalScheme, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProposalScheme> page = proposalSchemeService.findPage(new Page<ProposalScheme>(request, response), proposalScheme); 
		model.addAttribute("page", page);
		return "modules/diy/proposalSchemeList";
	}

	@RequiresPermissions("diy:proposalScheme:view")
	@RequestMapping(value = "form")
	public String form(ProposalScheme proposalScheme, Model model) {
		model.addAttribute("proposalScheme", proposalScheme);
		return "modules/diy/proposalSchemeForm";
	}

	@RequiresPermissions("diy:proposalScheme:edit")
	@RequestMapping(value = "save")
	public String save(ProposalScheme proposalScheme, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, proposalScheme)){
			return form(proposalScheme, model);
		}
		proposalSchemeService.save(proposalScheme);
		addMessage(redirectAttributes, "保存个性化定制模板成功");
		return "redirect:"+Global.getAdminPath()+"/diy/proposalScheme/?repage";
	}
	
	@RequiresPermissions("diy:proposalScheme:edit")
	@RequestMapping(value = "delete")
	public String delete(ProposalScheme proposalScheme, RedirectAttributes redirectAttributes) {
		proposalSchemeService.delete(proposalScheme);
		addMessage(redirectAttributes, "删除个性化定制模板成功");
		return "redirect:"+Global.getAdminPath()+"/diy/proposalScheme/?repage";
	}

	/**
	 * 查询所有可用定制主题
	 * @param proposalScheme
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(ProposalScheme proposalScheme, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		proposalScheme.setStatus(null);//查询全部
		proposalScheme.setType(null);//查询全部
		List<ProposalScheme> list =proposalSchemeService.findList(proposalScheme);
		for (int i=0; i<list.size(); i++){
			ProposalScheme e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
			mapList.add(map);
			
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/byStatus/{status}", method = RequestMethod.GET)
	public List<ProposalScheme> listByStatus(@PathVariable String status) {
		if( status==null || status.trim().length() == 0 )
			return Lists.newArrayList();
		ProposalScheme proposalScheme = new ProposalScheme();
		proposalScheme.setStatus(status);
		return proposalSchemeService.findList(proposalScheme);
	}
	
	//根据名称查询方案类型，如果无匹配则返回全部
	@ResponseBody
	@RequestMapping(value = "rest/byName/{name}", method = RequestMethod.GET)
	public List<ProposalScheme> listByName(@PathVariable String name) {
		ProposalScheme proposalScheme = new ProposalScheme();
		if( name!=null && name.trim().length() > 0 && "*".equalsIgnoreCase(name.trim())) {
			//直接返回全部
		}else if( name!=null && name.trim().length() > 0 ) {
			proposalScheme.setName(name);
		}
		proposalScheme.setStatus("1");//仅查询已发布主题
		List<ProposalScheme> list = proposalSchemeService.findList(proposalScheme);
		if(list.size()==0) {
			proposalScheme.setName("");//不限制，将获取全部
			list = proposalSchemeService.findList(proposalScheme);
		}
		return list;
	}
	
	//根据名称查询方案类型，如果无匹配则返回全部
	@ResponseBody
	@RequestMapping(value = "rest/pages", method = RequestMethod.GET)
	public List<ProposalScheme> listPagedList(@RequestParam int from,  
			@RequestParam int to) {
		
		//组织查询参数
		Map<String,Object> params = Maps.newHashMap();
		params.put("from", from);
		params.put("to", to);
		
		List<ProposalScheme> list = proposalSchemeService.findPagedList(params);
		return list;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/status/{schemeId}/{status}", method = RequestMethod.POST)
	public JSONObject changeStatus(@PathVariable String schemeId, @PathVariable String status) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( schemeId==null || schemeId.trim().length() == 0 || status==null || status.trim().length() == 0 ) {
			result.put("msg", "Both schemeID and status are required.");
			return result;
		}
		ProposalScheme proposalScheme = proposalSchemeService.get(schemeId);
		proposalScheme.setStatus(status);
		proposalSchemeService.save(proposalScheme);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 根据ID克隆定制主题：支持在一个根主题下建立多个子主题
	 * @param params {byOpenid:xxx, forOpenid:xxx} 均为可选
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/clone/{schemeId}", method = RequestMethod.POST)
	public JSONObject clone(@PathVariable String schemeId, @RequestBody JSONObject params) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( schemeId==null || schemeId.trim().length() == 0) {
			result.put("msg", "schemeId is required.");
			return result;
		}
		
		//查询得到ProposalScheme
		ProposalScheme scheme = proposalSchemeService.get(schemeId);
		ProposalScheme oldScheme = proposalSchemeService.get(schemeId); //保持对原始对象的引用
		if(scheme == null) {
			result.put("msg", "cannot find scheme by id. "+schemeId);
			return result;
		}
		
		//设置为新的scheme：通过重置id完成
		String id = Util.get32UUID();
		scheme.setParent(oldScheme);
		scheme.setId(id);
		scheme.setName(scheme.getName()+" copy");
		scheme.setIsNewRecord(true);
		scheme.setCreateDate(new Date());
		scheme.setUpdateDate(new Date());
		proposalSchemeService.save(scheme);
		
		//克隆关联的指南
		GuideBookProposal guideBookProposalQuery = new GuideBookProposal();
		guideBookProposalQuery.setProposal(oldScheme);//根据原scheme获取所有关联的指南
		List<GuideBookProposal> guideBookProposals = guideBookProposalService.findList(guideBookProposalQuery);
		for(GuideBookProposal guideBookProposal:guideBookProposals) {
			guideBookProposal.setId(Util.get32UUID());
			guideBookProposal.setIsNewRecord(true);
			guideBookProposal.setCreateDate(new Date());
			guideBookProposal.setUpdateDate(new Date());
			guideBookProposal.setProposal(scheme);//关联到新的scheme上
			guideBookProposalService.save(guideBookProposal);
		}
		
		//克隆section
		ProposalSection proposalSectionQuery = new ProposalSection();
		proposalSectionQuery.setScheme(oldScheme);
		List<ProposalSection> proposalSections = proposalSectionService.findList(proposalSectionQuery);
		for(ProposalSection proposalSection:proposalSections) {
			proposalSection.setId(Util.get32UUID());
			proposalSection.setIsNewRecord(true);
			proposalSection.setCreateDate(new Date());
			proposalSection.setUpdateDate(new Date());
			proposalSection.setScheme(scheme);
			proposalSectionService.save(proposalSection);
		}
		
		//克隆subtype
		ProposalSubtype proposalSubtypeQuery = new ProposalSubtype();
		proposalSectionQuery.setScheme(oldScheme);
		List<ProposalSubtype> proposalSubtypes = proposalSubtypeService.findList(proposalSubtypeQuery);
		for(ProposalSubtype proposalSubtype:proposalSubtypes) {
			proposalSubtype.setId(Util.get32UUID());
			proposalSubtype.setIsNewRecord(true);
			proposalSubtype.setCreateDate(new Date());
			proposalSubtype.setUpdateDate(new Date());
			proposalSubtype.setScheme(scheme);
			proposalSubtypeService.save(proposalSubtype);
		}
		
		result.put("success", true);
		result.put("scheme", scheme);//返回新建立的scheme
		return result;
	}
	
	/**
	 * 根据ID获取主题详情：包含基本信息、指南列表、section列表、subtype列表。用于查看及客户端编辑
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/scheme/{schemeId}", method = RequestMethod.GET)
	public JSONObject getDetail(@PathVariable String schemeId) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( schemeId==null || schemeId.trim().length() == 0) {
			result.put("msg", "schemeId is required.");
			return result;
		}
		
		//查询得到ProposalScheme
		ProposalScheme scheme = proposalSchemeService.get(schemeId);
		if(scheme == null) {
			result.put("msg", "cannot find scheme by id. "+schemeId);
			return result;
		}
		result.put("scheme", scheme);
		
		//获取关联的指南
		GuideBookProposal guideBookProposalQuery = new GuideBookProposal();
		guideBookProposalQuery.setProposal(scheme);//根据原scheme获取所有关联的指南
		List<GuideBook> guideBooks = Lists.newArrayList();
		List<GuideBookProposal> guideBookProposals = guideBookProposalService.findList(guideBookProposalQuery);
		for(GuideBookProposal guideBookProposal:guideBookProposals) {
			guideBooks.add(guideBookService.get(guideBookProposal.getGuide()));
		}
		result.put("guideBooks", guideBooks);
		
		//获取关联的section
		ProposalSection proposalSectionQuery = new ProposalSection();
		proposalSectionQuery.setScheme(scheme);
		List<ProposalSection> sections = proposalSectionService.findList(proposalSectionQuery);
		ProposalScheme parentScheme = scheme;
		while( parentScheme!=null && (sections == null || sections.size()==0) ) { //如果没有则追溯到最上级：如果当前已定义则直接使用，不支持合并
			parentScheme = proposalSchemeService.get(parentScheme.getParent());
			proposalSectionQuery.setScheme(parentScheme);
			sections = proposalSectionService.findList(proposalSectionQuery);
		}
		result.put("sections", sections);
		
		//获取关联的subtype
		ProposalSubtype proposalSubtypeQuery = new ProposalSubtype();
		proposalSubtypeQuery.setScheme(scheme);
		List<ProposalSubtype> subtypes = proposalSubtypeService.findList(proposalSubtypeQuery);
		parentScheme = scheme;
		while( parentScheme!=null && (subtypes == null || subtypes.size()==0) ) { //如果没有则追溯到最上级：如果当前已定义则直接使用，不支持合并
			parentScheme = proposalSchemeService.get(parentScheme.getParent());
			proposalSubtypeQuery.setScheme(parentScheme);
			subtypes = proposalSubtypeService.findList(proposalSubtypeQuery);
		}
		result.put("subtypes", subtypes);
		
		result.put("success", true);
		return result;
	}
	
}