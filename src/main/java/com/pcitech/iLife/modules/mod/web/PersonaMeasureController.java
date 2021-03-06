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
import com.pcitech.iLife.modules.mod.entity.PersonaMeasure;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.PersonaMeasureService;
import com.pcitech.iLife.modules.mod.service.PersonaService;
import com.pcitech.iLife.modules.mod.service.PhaseService;
import com.pcitech.iLife.modules.mod.service.UserCategoryService;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;

/**
 * 画像需要构成Controller
 * @author qchzhu
 * @version 2020-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/personaMeasure")
public class PersonaMeasureController extends BaseController {
	@Autowired
	private PersonaService personaService;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private UserMeasureService userMeasureService;
	@Autowired
	private UserCategoryService userCategoryService;
	@Autowired
	private PersonaMeasureService personaMeasureService;
	
	@ModelAttribute
	public PersonaMeasure get(@RequestParam(required=false) String id) {
		PersonaMeasure entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = personaMeasureService.get(id);
		}
		if (entity == null){
			entity = new PersonaMeasure();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:personaMeasure:view")
	@RequestMapping(value = {"list", ""})
	public String list(PersonaMeasure personaMeasure,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeModule.equals("persona")){
			personaMeasure.setPersona(new Persona(treeId));
		}else{//否则提示选择用户画像
			model.addAttribute("message","选择画像查看其VALS标注。");
			return "treeData/none";
		}
			
		Page<PersonaMeasure> page = personaMeasureService.findPage(new Page<PersonaMeasure>(request, response), personaMeasure); 
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/personaMeasureList";
	}

	@RequiresPermissions("mod:personaMeasure:view")
	@RequestMapping(value = "form")
	public String form(PersonaMeasure personaMeasure,String pid,String pType, Model model) {
		Persona parent=new Persona();
		if(pType.equals("persona")){
			parent = personaService.get(pid);
			personaMeasure.setPersona(parent);
		}else {//否则提示选择用户画像
			model.addAttribute("message","选择画像查看其VALS标注。");
			return "treeData/none";
		}
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("personaMeasure", personaMeasure);
		return "modules/mod/personaMeasureForm";
	}

	@RequiresPermissions("mod:personaMeasure:edit")
	@RequestMapping(value = "save")
	public String save(PersonaMeasure personaMeasure,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, personaMeasure)){
			return form(personaMeasure,pid,pType,model);
		}
		if(personaMeasure.getPersona() == null){//不知道为啥，前端传进来的persona信息丢失了，手动补一次
			personaMeasure.setPersona(personaService.get(pid));
		}
		personaMeasureService.save(personaMeasure);
		addMessage(redirectAttributes, "保存用户分群的VALS标注成功");
		return "redirect:"+Global.getAdminPath()+"/mod/personaMeasure/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:personaMeasure:edit")
	@RequestMapping(value = "delete")
	public String delete(PersonaMeasure personaMeasure,String pid,String pType, RedirectAttributes redirectAttributes) {
		personaMeasureService.delete(personaMeasure);
		addMessage(redirectAttributes, "删除用户分群的VALS标注成功");
		return "redirect:"+Global.getAdminPath()+"/mod/personaMeasure/?treeId="+pid+"&treeModule="+pType+"&repage";
	}

	@RequiresPermissions("mod:personaMeasure:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/personaMeasure");
		model.addAttribute("title","用户分群VALS标注");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:personaMeasure:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/personaMeasure");
		model.addAttribute("title","成长阶段");
		List<Phase> phaseTree = phaseService.findTree();
		model.addAttribute("list", personaService.getTree(phaseTree));
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:personaMeasure:view")
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