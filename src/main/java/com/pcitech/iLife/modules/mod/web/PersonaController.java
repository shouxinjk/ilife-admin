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
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.PersonaService;
import com.pcitech.iLife.modules.mod.service.PhaseService;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 用户分群Controller
 * @author chenci
 * @version 2017-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/persona")
@ApiIgnore
public class PersonaController extends BaseController {

	@Autowired
	private PersonaService personaService;
	@Autowired
	private PhaseService phaseService;
	
	@ModelAttribute
	public Persona get(@RequestParam(required=false) String id) {
		Persona entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = personaService.get(id);
		}
		if (entity == null){
			entity = new Persona();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:persona:view")
	@RequestMapping(value = {"list", ""})
	public String list(Persona persona,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeModule.equals("persona")){
			persona.setParent(new Persona(treeId));
		}else if(treeModule.equals("phase")){
			persona.setPhase(new Phase(treeId));
			persona.setParent(new Persona("0"));
		}
			
		Page<Persona> page = personaService.findPage(new Page<Persona>(request, response), persona); 
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/personaList";
	}

	@RequiresPermissions("mod:persona:view")
	@RequestMapping(value = "form")
	public String form(Persona persona,String pid,String pType, Model model) {
		Persona parent=new Persona();
		if(pType.equals("persona")){
			parent = personaService.get(pid);
			persona.setParent(parent);
			persona.setPhase(parent.getPhase());
		}else if(pType.equals("phase")){
			persona.setPhase(phaseService.get(pid));
			parent.setId("0");
			parent.setName("顶级");
			persona.setParent(parent);
		}
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("persona", persona);
		return "modules/mod/personaForm";
	}

	@RequiresPermissions("mod:persona:edit")
	@RequestMapping(value = "save")
	public String save(Persona persona,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, persona)){
			return form(persona,pid,pType,model);
		}
		personaService.save(persona);
		addMessage(redirectAttributes, "保存用户分群成功");
		return "redirect:"+Global.getAdminPath()+"/mod/persona/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:persona:edit")
	@RequestMapping(value = "delete")
	public String delete(Persona persona,String pid,String pType, RedirectAttributes redirectAttributes) {
		personaService.delete(persona);
		addMessage(redirectAttributes, "删除用户分群成功");
		return "redirect:"+Global.getAdminPath()+"/mod/persona/?treeId="+pid+"&treeModule="+pType+"repage";
	}

	@RequiresPermissions("mod:persona:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/persona");
		model.addAttribute("title","用户分群");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:persona:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/persona");
		model.addAttribute("title","成长阶段");
		List<Phase> phaseTree = phaseService.findTree();
		model.addAttribute("list", personaService.getTree(phaseTree));
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:persona:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个项目。");
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