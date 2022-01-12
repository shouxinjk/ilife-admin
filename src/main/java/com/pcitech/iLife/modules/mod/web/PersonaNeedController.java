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
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.PersonaNeedService;
import com.pcitech.iLife.modules.mod.service.PersonaService;
import com.pcitech.iLife.modules.mod.service.PhaseService;

/**
 * 画像需要构成Controller
 * @author qchzhu
 * @version 2020-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/personaNeed")
public class PersonaNeedController extends BaseController {
	@Autowired
	private PersonaService personaService;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private PersonaNeedService personaNeedService;
	
	@ModelAttribute
	public PersonaNeed get(@RequestParam(required=false) String id) {
		PersonaNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = personaNeedService.get(id);
		}
		if (entity == null){
			entity = new PersonaNeed();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:personaNeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(PersonaNeed personaNeed,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeModule.equals("persona")){
			personaNeed.setPersona(new Persona(treeId));
		}else{//否则提示选择用户画像
			model.addAttribute("message","选择画像查看其需要构成。");
			return "treeData/none";
		}
			
		Page<PersonaNeed> page = personaNeedService.findPage(new Page<PersonaNeed>(request, response), personaNeed); 
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/personaNeedList";
	}

	@RequiresPermissions("mod:personaNeed:view")
	@RequestMapping(value = "form")
	public String form(PersonaNeed personaNeed,String pid,String pType, Model model) {
		Persona parent=new Persona();
		if(pType.equals("persona")){
			parent = personaService.get(pid);
			personaNeed.setPersona(parent);
			personaNeed.setPhase(parent.getPhase());//默认设置阶段与persona一致
		}else {//否则提示选择用户画像
			model.addAttribute("message","选择画像查看其需要构成。");
			return "treeData/none";
		}
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("personaNeed", personaNeed);
		return "modules/mod/personaNeedForm";
	}

	@RequiresPermissions("mod:personaNeed:edit")
	@RequestMapping(value = "save")
	public String save(PersonaNeed personaNeed,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, personaNeed)){
			return form(personaNeed,pid,pType,model);
		}
		if(personaNeed.getPersona() == null){//不知道为啥，前端传进来的persona信息丢失了，手动补一次
			personaNeed.setPersona(personaService.get(pid));
		}
		personaNeedService.save(personaNeed);
		addMessage(redirectAttributes, "保存用户分群下的需要成功");
		return "redirect:"+Global.getAdminPath()+"/mod/personaNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:personaNeed:edit")
	@RequestMapping(value = "delete")
	public String delete(PersonaNeed personaNeed,String pid,String pType, RedirectAttributes redirectAttributes) {
		personaNeedService.delete(personaNeed);
		addMessage(redirectAttributes, "删除用户分群下的需要成功");
		return "redirect:"+Global.getAdminPath()+"/mod/personaNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
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
		personaNeedService.updateWeight(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "weight updated.");
		return result;
	}

	@RequiresPermissions("mod:personaNeed:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/personaNeed");
		model.addAttribute("title","用户分群需要构成");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:personaNeed:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/personaNeed");
		model.addAttribute("title","成长阶段");
		List<Phase> phaseTree = phaseService.findTree();
		model.addAttribute("list", personaService.getTree(phaseTree));
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:personaNeed:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个画像。");
		return "treeData/none";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Persona> list = personaService.findTree();
		for (int i=0; i<list.size(); i++){
			Persona e = list.get(i);
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