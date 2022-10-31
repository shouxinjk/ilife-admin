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

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.diy.entity.JsonForm;
import com.pcitech.iLife.modules.diy.entity.ProposalScheme;
import com.pcitech.iLife.modules.diy.entity.ProposalSection;
import com.pcitech.iLife.modules.diy.service.ProposalSchemeService;

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
	
	@ResponseBody
	@RequestMapping(value = "rest/byName/{name}", method = RequestMethod.GET)
	public List<ProposalScheme> listByName(@PathVariable String name) {
		ProposalScheme proposalScheme = new ProposalScheme();
		if( name!=null && name.trim().length() > 0 ) {
			proposalScheme.setName(name);
		}
		proposalScheme.setStatus("1");//仅查询已发布主题
		return proposalSchemeService.findList(proposalScheme);
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
	
}