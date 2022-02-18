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
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.MotivationCategory;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.PhaseNeed;
import com.pcitech.iLife.modules.mod.service.MotivationCategoryService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.PhaseNeedService;
import com.pcitech.iLife.modules.mod.service.PhaseService;

/**
 * 阶段需要构成Controller
 * @author ilife
 * @version 2022-02-09
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/phaseNeed")
public class PhaseNeedController extends BaseController {

	@Autowired
	private PhaseService phaseService;
	
	@Autowired
	private PhaseNeedService phaseNeedService;
	
	@Autowired
	private MotivationCategoryService motivationCategoryService;
	@Autowired
	private MotivationService motivationService;
	
	@ModelAttribute
	public PhaseNeed get(@RequestParam(required=false) String id) {
		PhaseNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = phaseNeedService.get(id);
		}
		if (entity == null){
			entity = new PhaseNeed();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(PhaseNeed phaseNeed,String treeId ,String treeModule,String topType, HttpServletRequest request, HttpServletResponse response, Model model) {
		//根据阶段过滤
		Phase phase = phaseService.get(treeId);
		phaseNeed.setPhase(phase);
		Page<PhaseNeed> page = phaseNeedService.findPage(new Page<PhaseNeed>(request, response), phaseNeed); 
		
		//增加需要所在的类目。注意：需要逐个填补。效率低下
		List<PhaseNeed> needsWithCategory = Lists.newArrayList();
		
		List<PhaseNeed> needs = page.getList();
		for(PhaseNeed need:needs) {
			Motivation m = motivationService.get(need.getNeed());
			MotivationCategory cat = motivationCategoryService.get(m.getMotivationCategory());
			List<String> catNames = Lists.newArrayList();
			catNames.add(cat.getName());//当前类目名称
			
			while(cat!=null && cat.getParent()!=null) {
				cat = motivationCategoryService.get(cat.getParent());
				if(cat!=null)
					catNames.add(cat.getName());
			}
			
			String fullCategory = "";
			for(int i=catNames.size()-2;i>=0;i--) {
				if(i!=catNames.size()-2)
					fullCategory += " > ";
				fullCategory += catNames.get(i);
			}
			
			need.setNeedCategory(fullCategory);
			needsWithCategory.add(need);
		}
		page.setList(needsWithCategory);//替换为带有需要全路径的列表
		
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/phaseNeedList";
	}

	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = "form")
	public String form(PhaseNeed phaseNeed,String pid,String pType,  Model model) {
		//设置当前选中的阶段
		Phase phase = phaseService.get(phaseNeed.getPhase());
		phaseNeed.setPhase(phase);
		
		model.addAttribute("phaseNeed", phaseNeed);
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		return "modules/mod/phaseNeedForm";
	}

	@RequiresPermissions("mod:phaseNeed:edit")
	@RequestMapping(value = "save")
	public String save(PhaseNeed phaseNeed,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, phaseNeed)){
			return form(phaseNeed,pid,pType, model);
		}
		phaseNeedService.save(phaseNeed);
		addMessage(redirectAttributes, "保存阶段需要构成成功");
		return "redirect:"+Global.getAdminPath()+"/mod/phaseNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:phaseNeed:edit")
	@RequestMapping(value = "delete")
	public String delete(PhaseNeed phaseNeed,String pid,String pType, RedirectAttributes redirectAttributes) {
		phaseNeedService.delete(phaseNeed);
		addMessage(redirectAttributes, "删除阶段需要构成成功");
		return "redirect:"+Global.getAdminPath()+"/mod/phaseNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
	}


	@ResponseBody
	@RequestMapping(value = "rest/weight")
	//更新需要满足度
	public Map<String,String> updateWeight( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double weight, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("weight", weight);
		params.put("updateDate", new Date());
		phaseNeedService.updateWeight(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "weight updated.");
		return result;
	}

	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/phaseNeed");
		model.addAttribute("title","阶段需要构成");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/phaseNeed");
		model.addAttribute("title","成长阶段");
		List<Phase> phaseTree = phaseService.findTree();
		model.addAttribute("list", phaseTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个阶段。");
		return "treeData/none";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Phase> list = phaseService.findTree();
		for (int i=0; i<list.size(); i++){
			Phase e = list.get(i);
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