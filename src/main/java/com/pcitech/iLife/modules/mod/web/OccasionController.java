/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.pcitech.iLife.modules.mod.service.OccasionCategoryService;
import com.pcitech.iLife.modules.mod.service.OccasionService;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 外部诱因Controller
 * @author chenci
 * @version 2017-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/occasion")
@ApiIgnore
public class OccasionController extends BaseController {

	@Autowired
	private OccasionService occasionService;
	@Autowired
	private OccasionCategoryService occasionCategoryService;
	
	@ModelAttribute
	public Occasion get(@RequestParam(required=false) String id) {
		Occasion entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = occasionService.get(id);
		}
		if (entity == null){
			entity = new Occasion();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:occasion:view")
	@RequestMapping(value = {"list", ""})
	public String list(Occasion occasion,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		occasion.setOccasionCategory(new OccasionCategory(treeId));
		Page<Occasion> page = occasionService.findPage(new Page<Occasion>(request, response), occasion); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/occasionList";
	}

	@RequiresPermissions("mod:occasion:view")
	@RequestMapping(value = "form")
	public String form(Occasion occasion, Model model) {
		if(occasion.getOccasionCategory()!=null&&StringUtils.isNoneBlank(occasion.getOccasionCategory().getId())){
			occasion.setOccasionCategory(occasionCategoryService.get(occasion.getOccasionCategory().getId()));
		}
		model.addAttribute("occasion", occasion);
		return "modules/mod/occasionForm";
	}

	@RequiresPermissions("mod:occasion:edit")
	@RequestMapping(value = "save")
	public String save(Occasion occasion, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, occasion)){
			return form(occasion, model);
		}
		occasionService.save(occasion);
		addMessage(redirectAttributes, "保存外部诱因成功");
		return "redirect:"+Global.getAdminPath()+"/mod/occasion/?treeId="+occasion.getOccasionCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:occasion:edit")
	@RequestMapping(value = "delete")
	public String delete(Occasion occasion,String treeId,  RedirectAttributes redirectAttributes) {
		occasionService.delete(occasion);
		addMessage(redirectAttributes, "删除外部诱因成功");
		return "redirect:"+Global.getAdminPath()+"/mod/occasion/?treeId="+treeId+"&repage";
	}

	@RequiresPermissions("mod:occasion:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/occasion");
		model.addAttribute("title","外部诱因");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:occasion:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/occasion");
		model.addAttribute("title","分类");
		model.addAttribute("list", occasionCategoryService.findTree());
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:occasion:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个分类。");
		return "treeData/none";
	}
	
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Occasion occasion, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Occasion> list =occasionService.findList(occasion);
		for (int i=0; i<list.size(); i++){
			Occasion e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
//			if (type != null && "3".equals(type)){
//				map.put("isParent", true);
//			}
			mapList.add(map);
			
		}
		return mapList;
	}
}