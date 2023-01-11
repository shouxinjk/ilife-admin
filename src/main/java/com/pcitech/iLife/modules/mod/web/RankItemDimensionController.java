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
import com.pcitech.iLife.modules.mod.entity.Rank;
import com.pcitech.iLife.modules.mod.entity.RankItemDimension;
import com.pcitech.iLife.modules.mod.service.RankItemDimensionService;
import com.pcitech.iLife.util.Util;

/**
 * 排行榜条目Controller
 * @author ilife
 * @version 2023-01-11
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/rankItemDimension")
public class RankItemDimensionController extends BaseController {

	@Autowired
	private RankItemDimensionService rankItemDimensionService;
	
	@ModelAttribute
	public RankItemDimension get(@RequestParam(required=false) String id) {
		RankItemDimension entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = rankItemDimensionService.get(id);
		}
		if (entity == null){
			entity = new RankItemDimension();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:rankItemDimension:view")
	@RequestMapping(value = {"list", ""})
	public String list(RankItemDimension rankItemDimension, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<RankItemDimension> page = rankItemDimensionService.findPage(new Page<RankItemDimension>(request, response), rankItemDimension); 
		model.addAttribute("page", page);
		return "modules/mod/rankItemDimensionList";
	}

	@RequiresPermissions("mod:rankItemDimension:view")
	@RequestMapping(value = "form")
	public String form(RankItemDimension rankItemDimension, Model model) {
		model.addAttribute("rankItemDimension", rankItemDimension);
		return "modules/mod/rankItemDimensionForm";
	}

	@RequiresPermissions("mod:rankItemDimension:edit")
	@RequestMapping(value = "save")
	public String save(RankItemDimension rankItemDimension, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, rankItemDimension)){
			return form(rankItemDimension, model);
		}
		rankItemDimensionService.save(rankItemDimension);
		addMessage(redirectAttributes, "保存排行榜条目成功");
		return "redirect:"+Global.getAdminPath()+"/mod/rankItemDimension/?repage";
	}
	
	@RequiresPermissions("mod:rankItemDimension:edit")
	@RequestMapping(value = "delete")
	public String delete(RankItemDimension rankItemDimension, RedirectAttributes redirectAttributes) {
		rankItemDimensionService.delete(rankItemDimension);
		addMessage(redirectAttributes, "删除排行榜条目成功");
		return "redirect:"+Global.getAdminPath()+"/mod/rankItemDimension/?repage";
	}
	

	//查询列表
	@ResponseBody
	@RequestMapping(value = "rest/items", method = RequestMethod.POST)
	public List<RankItemDimension> listNeeds(@RequestBody RankItemDimension rankItemDimension) {
		if(rankItemDimension==null)
			return Lists.newArrayList();
		return rankItemDimensionService.findList(rankItemDimension);
	}
	
	//新增或修改
	@ResponseBody
	@RequestMapping(value = "rest/item", method = RequestMethod.POST)
	public JSONObject upsert( @RequestBody RankItemDimension rankItemDimension) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(rankItemDimension.getId()==null||rankItemDimension.getId().trim().length()==0) {//认为是新增
			rankItemDimension.setId(Util.get32UUID());
			rankItemDimension.setIsNewRecord(true);
		}
		try {
			rankItemDimensionService.save(rankItemDimension);
			result.put("data", rankItemDimension);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}

}