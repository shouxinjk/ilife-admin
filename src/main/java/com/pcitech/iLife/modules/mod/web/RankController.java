/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.List;

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
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.entity.Rank;
import com.pcitech.iLife.modules.mod.service.RankService;
import com.pcitech.iLife.util.Util;

/**
 * 排行榜Controller
 * @author ilife
 * @version 2023-01-11
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/rank")
public class RankController extends BaseController {

	@Autowired
	private RankService rankService;
	
	@ModelAttribute
	public Rank get(@RequestParam(required=false) String id) {
		Rank entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = rankService.get(id);
		}
		if (entity == null){
			entity = new Rank();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:rank:view")
	@RequestMapping(value = {"list", ""})
	public String list(Rank rank, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Rank> page = rankService.findPage(new Page<Rank>(request, response), rank); 
		model.addAttribute("page", page);
		return "modules/mod/rankList";
	}

	@RequiresPermissions("mod:rank:view")
	@RequestMapping(value = "form")
	public String form(Rank rank, Model model) {
		model.addAttribute("rank", rank);
		return "modules/mod/rankForm";
	}

	@RequiresPermissions("mod:rank:edit")
	@RequestMapping(value = "save")
	public String save(Rank rank, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, rank)){
			return form(rank, model);
		}
		rankService.save(rank);
		addMessage(redirectAttributes, "保存排行榜成功");
		return "redirect:"+Global.getAdminPath()+"/mod/rank/?repage";
	}
	
	@RequiresPermissions("mod:rank:edit")
	@RequestMapping(value = "delete")
	public String delete(Rank rank, RedirectAttributes redirectAttributes) {
		rankService.delete(rank);
		addMessage(redirectAttributes, "删除排行榜成功");
		return "redirect:"+Global.getAdminPath()+"/mod/rank/?repage";
	}
	
	//查询列表：支持根据category.id、name、openid、nickname等查询得到
	@ResponseBody
	@RequestMapping(value = "rest/ranks", method = RequestMethod.POST)
	public List<Rank> listNeeds(@RequestBody Rank rank) {
		if(rank==null)
			return Lists.newArrayList();
		return rankService.findList(rank);
	}
	
	//新增或修改
	@ResponseBody
	@RequestMapping(value = "rest/rank", method = RequestMethod.POST)
	public JSONObject upsert( @RequestBody Rank rank) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(rank.getId()==null||rank.getId().trim().length()==0) {//认为是新增
			rank.setId(Util.get32UUID());
			rank.setIsNewRecord(true);
		}
		try {
			rankService.save(rank);
			result.put("data", rank);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//根据ID查询：用于根据dimension组合验证排序
	@ResponseBody
	@RequestMapping(value = "rest/rank/{id}", method = RequestMethod.GET)
	public Rank getById( @PathVariable String id) {
		return rankService.get(id);
	}
	
	//删除需要
	@ResponseBody
	@RequestMapping(value = "rest/rank", method = RequestMethod.PUT)
	public JSONObject delete( @RequestBody Rank rank) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		try {
			rankService.delete(rank);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}

}