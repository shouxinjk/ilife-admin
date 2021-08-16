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
import org.springframework.web.bind.annotation.PathVariable;
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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.HierarchyService;
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
	
	//获取阶段及画像。传入的ID有两种：阶段或画像，使用前缀区分。phase-或persona-
	@ResponseBody
	@RequestMapping(value = "rest/tree/{id}")
	public List<Map<String, Object>> listStandardProperetiesByParentId( @PathVariable String id, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,String> icon = Maps.newHashMap();
		icon.put("folder","fas fa-book");
		icon.put("openFolder","fas fa-book-open");
		icon.put("file","fas fa-file");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		//默认id接受前端传递的id值，但对于根节点，前端传递的是div的id，固定为 tree-source ，需要进行映射
		if(id==null || id.trim().length()==0 || "tree-source".equalsIgnoreCase(id)) {
			listByPhase("1",mapList);//默认首先列出所有顶级阶段，
		}else if(id.startsWith("phase-")) {
			listByPhase(id.replace("phase-", ""),mapList);
		}else if(id.startsWith("persona-")) {
			listByPersona(id.replace("persona-", ""),mapList);
		}else {//这个就离奇了么，不知道是个啥，直接忽略
			logger.error("Unknown id type.[id]"+id);
			Map<String,Object> result = Maps.newHashMap();
			result.put("success", false);
			result.put("msg", "Unknown id type.[id]"+id);
			mapList.add(result);
		}
		return mapList;
	}
	
	//根据阶段查询下级阶段及画像
	private void listByPhase(String id,List<Map<String, Object>> mapList) {
		Map<String,String> icon = Maps.newHashMap();
		icon.put("folder","fas fa-book");
		icon.put("openFolder","fas fa-book-open");
		icon.put("file","fas fa-file");

		//加载阶段列表
		List<Phase> list = phaseService.findByParentId(id);
		for (int i=0; i<list.size(); i++){
			Phase e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
//			if(!"-1".equalsIgnoreCase(id))//根节点不能加
			map.put("parent", "1".equalsIgnoreCase(id)?"tree-source":"phase-"+id);//如果是顶级节点，直接挂到根节点下
			map.put("value", e.getName());
			map.put("id", "phase-"+e.getId());
			map.put("opened", false);
			map.put("items", true);//默认都认为有下级目录
//			map.put("icon", icon);//设置图标
			mapList.add(map);
		}
		
		//添加画像列表
		List<Persona> list2 =personaService.findByPhaseId(id);
		for (int i=0; i<list2.size(); i++){
			Persona e = list2.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("parent",  "phase-"+id);
			map.put("value", e.getName());
			map.put("id", "persona-"+e.getId());//属性条目通过 prop- 前缀进行区分
			map.put("opened", true);
			map.put("items", true);//可能有下级画像
			map.put("icon", icon);//设置图标
			mapList.add(map);
		}			
	}
	

	//根据阶段查询下级阶段及画像
	private void listByPersona(String id,List<Map<String, Object>> mapList) {
		Map<String,String> icon = Maps.newHashMap();
		icon.put("folder","fas fa-book");
		icon.put("openFolder","fas fa-book-open");
		icon.put("file","fas fa-file");
		
		//添加画像列表
		List<Persona> list2 =personaService.findByParentId(id);
		for (int i=0; i<list2.size(); i++){
			Persona e = list2.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("parent",  "persona-"+id);
			map.put("value", e.getName());
			map.put("id", "persona-"+e.getId());//属性条目通过 prop- 前缀进行区分
			map.put("opened", true);
			map.put("items", true);//可能有下级画像
			map.put("icon", icon);//设置图标
			mapList.add(map);
		}			
	}
	
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
		return "redirect:"+Global.getAdminPath()+"/mod/persona/?treeId="+pid+"&treeModule="+pType+"&repage";
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