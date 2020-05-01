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
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.MotivationCategory;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.service.MotivationCategoryService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.OccasionCategoryService;
import com.pcitech.iLife.modules.mod.service.OccasionNeedService;
import com.pcitech.iLife.modules.mod.service.OccasionService;
import com.pcitech.iLife.modules.mod.service.PhaseService;

/**
 * 诱因对需要的影响Controller
 * @author qchzhu
 * @version 2020-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/needOccasion")
public class NeedOccasionController extends BaseController {
	@Autowired
	private MotivationService motivationService;
	@Autowired
	private OccasionNeedService needOccasionService;
	@Autowired
	private MotivationCategoryService motivationCategoryService;
	@Autowired
	private PhaseService phaseService;
	
	@ModelAttribute
	public OccasionNeed get(@RequestParam(required=false) String id) {
		OccasionNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = needOccasionService.get(id);
		}
		if (entity == null){
			entity = new OccasionNeed();
		}
		return entity;
	}

	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = {"list", ""})
	public String list(OccasionNeed needOccasion,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if("motivation".equalsIgnoreCase(treeModule)){
			needOccasion.setNeed(new Motivation(treeId));
		}else{//否则提示选择动机
			model.addAttribute("message","选择动机查看可能影响的诱因。");
			return "treeData/none";
		}
			
		Page<OccasionNeed> page = needOccasionService.findPage(new Page<OccasionNeed>(request, response), needOccasion); 
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/needOccasionList";
	}

	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = "form")
	public String form(OccasionNeed needOccasion,String pid,String pType, Model model) {
		Motivation parent=new Motivation();
		if("motivation".equalsIgnoreCase(pType)){
			parent = motivationService.get(pid);
			needOccasion.setNeed(parent);
		}else {//否则提示选择诱因
			model.addAttribute("message","选择动机查看影响的诱因。");
			return "treeData/none";
		}
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("needOccasion", needOccasion);
		return "modules/mod/needOccasionForm";
	}

	@RequiresPermissions("mod:needOccasion:edit")
	@RequestMapping(value = "save")
	public String save(OccasionNeed needOccasion,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, needOccasion)){
			return form(needOccasion,pid,pType,model);
		}
		if(needOccasion.getNeed() == null){//不知道为啥，前端传进来的motivation信息丢失了，手动补一次
			needOccasion.setNeed(motivationService.get(pid));
		}
		needOccasionService.save(needOccasion);
		addMessage(redirectAttributes, "保存成功");
		return "redirect:"+Global.getAdminPath()+"/mod/needOccasion/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:needOccasion:edit")
	@RequestMapping(value = "delete")
	public String delete(OccasionNeed needOccasion,String pid,String pType, RedirectAttributes redirectAttributes) {
		needOccasionService.delete(needOccasion);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:"+Global.getAdminPath()+"/mod/needOccasion/?treeId="+pid+"&treeModule="+pType+"&repage";
	}

	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/needOccasion");
		model.addAttribute("title","影响需要的诱因");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/needOccasion");
		model.addAttribute("title","需要清单");
		List<MotivationCategory> categories = motivationCategoryService.findTree();
		List<Phase> phases = phaseService.findTree();
		model.addAttribute("list", motivationService.getTreeWithLeaf(phases,categories));
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个动机。");
		return "treeData/none";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<MotivationCategory> list = motivationCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			MotivationCategory e = list.get(i);
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