/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
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
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.service.OccasionCategoryService;
import com.pcitech.iLife.modules.mod.service.OccasionNeedService;
import com.pcitech.iLife.modules.mod.service.OccasionService;

/**
 * 诱因对需要的影响Controller
 * @author qchzhu
 * @version 2020-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/occasionNeed")
public class OccasionNeedController extends BaseController {
	@Autowired
	private OccasionService occasionService;
	@Autowired
	private OccasionNeedService occasionNeedService;
	@Autowired
	private OccasionCategoryService occasionCategoryService;
	
	@ModelAttribute
	public OccasionNeed get(@RequestParam(required=false) String id) {
		OccasionNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = occasionNeedService.get(id);
		}
		if (entity == null){
			entity = new OccasionNeed();
		}
		return entity;
	}

	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(OccasionNeed occasionNeed,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if("occasion".equalsIgnoreCase(treeModule)){
			occasionNeed.setOccasion(new Occasion(treeId));
		}else{//否则提示选择诱因
			model.addAttribute("message","选择诱因查看其需要构成。");
			return "treeData/none";
		}
			
		Page<OccasionNeed> page = occasionNeedService.findPage(new Page<OccasionNeed>(request, response), occasionNeed); 
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/occasionNeedList";
	}

	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = "form")
	public String form(OccasionNeed occasionNeed,String pid,String pType, Model model) {
		Occasion parent=new Occasion();
		if("occasion".equalsIgnoreCase(pType)){
			parent = occasionService.get(pid);
			occasionNeed.setOccasion(parent);
		}else {//否则提示选择诱因
			model.addAttribute("message","选择诱因查看其需要构成。");
			return "treeData/none";
		}
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("occasionNeed", occasionNeed);
		return "modules/mod/occasionNeedForm";
	}

	@RequiresPermissions("mod:occasionNeed:edit")
	@RequestMapping(value = "save")
	public String save(OccasionNeed occasionNeed,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, occasionNeed)){
			return form(occasionNeed,pid,pType,model);
		}
		if(occasionNeed.getOccasion() == null){//不知道为啥，前端传进来的occasion信息丢失了，手动补一次
			occasionNeed.setOccasion(occasionService.get(pid));
		}
		occasionNeedService.save(occasionNeed);
		addMessage(redirectAttributes, "保存诱因影响的需要成功");
		return "redirect:"+Global.getAdminPath()+"/mod/occasionNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:occasionNeed:edit")
	@RequestMapping(value = "delete")
	public String delete(OccasionNeed occasionNeed,String pid,String pType, RedirectAttributes redirectAttributes) {
		occasionNeedService.delete(occasionNeed);
		addMessage(redirectAttributes, "删除诱因影响的需要成功");
		return "redirect:"+Global.getAdminPath()+"/mod/occasionNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
	}

	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/occasionNeed");
		model.addAttribute("title","诱因的需要构成");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/occasionNeed");
		model.addAttribute("title","诱因");
		List<OccasionCategory> list = occasionCategoryService.findTree();
		model.addAttribute("list", occasionService.getTree(list));
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个诱因。");
		return "treeData/none";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<OccasionCategory> list = occasionCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			OccasionCategory e = list.get(i);
			if (extId == null || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():0);
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
}