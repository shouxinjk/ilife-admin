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
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.MotivationCategory;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.UserCategory;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.service.MotivationCategoryService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.PhaseService;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 内部动机Controller
 * @author chenci
 * @version 2017-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/motivation")
@ApiIgnore
public class MotivationController extends BaseController {

	@Autowired
	private MotivationService motivationService;
	@Autowired
	private MotivationCategoryService motivationCategoryService;
	@Autowired
	private PhaseService phaseService;
	
	@ModelAttribute
	public Motivation get(@RequestParam(required=false) String id) {
		Motivation entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = motivationService.get(id);
		}
		if (entity == null){
			entity = new Motivation();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:motivation:view")
	@RequestMapping(value = {"list", ""})
	public String list(Motivation motivation,String treeId ,String treeModule,String topType,String topId, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeModule.equals("motivationCategory")){
			motivation.setMotivationCategory(new MotivationCategory(treeId));
			motivation.setPhase(new Phase(topId));
		}else if(treeModule.equals("phase")){
			return "redirect:"+Global.getAdminPath()+"/mod/motivation/none";
		}
		Page<Motivation> page = motivationService.findPage(new Page<Motivation>(request, response), motivation); 
		
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		model.addAttribute("topId", topId);
		model.addAttribute("page", page);
		return "modules/mod/motivationList";
	}

	@RequiresPermissions("mod:motivation:view")
	@RequestMapping(value = "form")
	public String form(Motivation motivation,String pid,String pType,String topId, Model model) {
		motivation.setMotivationCategory(motivationCategoryService.get(pid));
		motivation.setPhase(phaseService.get(topId));
		model.addAttribute("motivation", motivation);
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("topId", topId);
		return "modules/mod/motivationForm";
	}

	@RequiresPermissions("mod:motivation:edit")
	@RequestMapping(value = "save")
	public String save(Motivation motivation,String pid,String pType,String topId, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, motivation)){
			return form(motivation,pid,pType ,topId,model);
		}
		motivationService.save(motivation);
		addMessage(redirectAttributes, "保存内部动机成功");
		return "redirect:"+Global.getAdminPath()+"/mod/motivation/?treeId="+pid+"&treeModule="+pType+"&topId="+topId+"&repage";
	}
	
	@RequiresPermissions("mod:motivation:edit")
	@RequestMapping(value = "delete")
	public String delete(Motivation motivation,String pid,String pType,String topId, RedirectAttributes redirectAttributes) {
		motivationService.delete(motivation);
		addMessage(redirectAttributes, "删除内部动机成功");
		return "redirect:"+Global.getAdminPath()+"/mod/motivation/?treeId="+pid+"&treeModule="+pType+"&topId="+topId+"&repage";
	}

	@RequiresPermissions("mod:persona:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/motivation");
		model.addAttribute("title","内部动机");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:persona:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/motivation");
		model.addAttribute("title","成长阶段");
		List<MotivationCategory> motivationCategoryTree = motivationCategoryService.findTree();
		List<Phase> phaseTree = phaseService.findTree();
		model.addAttribute("list", motivationService.getTree(phaseTree,motivationCategoryTree));
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:persona:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择内部动机分类。");
		return "treeData/none";
	}
	
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Motivation motivation, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Motivation> list =motivationService.findList(motivation);
		for (int i=0; i<list.size(); i++){
			Motivation e = list.get(i);
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
	

	/**
	 * 查询动机分类及动机列表，返回树结构，其中动机作为叶子节点。
	 * @param extId
	 * @param response
	 * @return
	 */
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeDataWithLeaf(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<MotivationCategory> categories = motivationCategoryService.findList(new MotivationCategory());
		for (int i=0; i<categories.size(); i++){
			MotivationCategory e = categories.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
				//查询该类别下的动机
				Motivation query = new Motivation();
				query.setMotivationCategory(e);
				List<Motivation> items = motivationService.findList(query);
				for(Motivation item:items) {
					Map<String, Object> leafNode = Maps.newHashMap();
					leafNode.put("id", item.getId());
					leafNode.put("pId", e.getId());
					leafNode.put("name", item.getName());
					mapList.add(leafNode);
				}
			}
		}
		return mapList;
	}
	
}