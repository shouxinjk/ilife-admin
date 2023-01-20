/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.web;

import java.util.List;
import java.util.Map;

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
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Hierarchy;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.service.HierarchyService;
import com.pcitech.iLife.modules.mod.service.PersonaService;
import com.pcitech.iLife.modules.mod.service.PhaseService;
import com.pcitech.iLife.modules.ope.entity.Person;
import com.pcitech.iLife.modules.ope.service.PersonService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.Util;

/**
 * 用户Controller
 * @author chenci
 * @version 2017-09-27
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/person")
public class PersonController extends BaseController {

	@Autowired
	private PersonService personService;
	@Autowired
	private PersonaService personaService;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private HierarchyService hierarchyService;
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public Person get(@RequestParam(required=false) String id) {
		Person entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = personService.get(id);
		}
		if (entity == null){
			entity = new Person();
		}
		return entity;
	}
	
	@RequiresPermissions("ope:person:view")
	@RequestMapping(value = {"list", ""})
	public String list(Person person, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Person> page = personService.findPage(new Page<Person>(request, response), person); 
		model.addAttribute("page", page);
		return "modules/ope/personList";
	}

	@RequiresPermissions("ope:person:view")
	@RequestMapping(value = "form")
	public String form(Person person, Model model) {
		model.addAttribute("person", person);
		return "modules/ope/personForm";
	}

	@RequiresPermissions("ope:person:edit")
	@RequestMapping(value = "save")
	public String save(Person person, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, person)){
			return form(person, model);
		}
		personService.save(person);
		addMessage(redirectAttributes, "保存用户成功");
		return "redirect:"+Global.getAdminPath()+"/ope/person/?repage";
	}
	
	@RequiresPermissions("ope:person:edit")
	@RequestMapping(value = "delete")
	public String delete(Person person, RedirectAttributes redirectAttributes) {
		personService.delete(person);
		addMessage(redirectAttributes, "删除用户成功");
		return "redirect:"+Global.getAdminPath()+"/ope/person/?repage";
	}

	//新增或修改person信息：
	@ResponseBody
	@RequestMapping(value = "rest/person", method = RequestMethod.POST)
	public JSONObject upsert( @RequestBody Person person) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		//默认ID为openid，如果未传递则新建
		String personId = person.getId();
		if(personId==null||personId.trim().length()==0||personService.get(personId)==null) {//认为是新增
			personId = Util.get32UUID();
			person.setId(personId);
			person.setIsNewRecord(true);
		}
		Person oldPerson = personService.get(personId);//对于修改的情况，需要保留原有设置：阶段、阶层、画像
		//检查设置默认画像
		if(person.getPersona()==null || personaService.get(person.getPersona())==null) {
			//如果之前已经设置，则使用之前的设置
			if(oldPerson!=null&&oldPerson.getPersona()!=null) {//使用原有值
				person.setPersona(oldPerson.getPersona());
			}else {//使用默认值
				Dict dict = new Dict();
				dict.setType("sx_default");
				dict.setValue("persona_id");
				List<Dict> dicts = dictService.findList(dict);
				if(dicts.size()>0) {
					person.setPersona(personaService.get(dicts.get(0).getLabel()));
				}
			}
		}
		//检查设置默认阶段
		if(person.getPhase()==null || phaseService.get(person.getPhase())==null) {
			//如果之前已经设置，则使用之前的设置
			if(oldPerson!=null&&oldPerson.getPhase()!=null) {//使用原有值
				person.setPhase(oldPerson.getPhase());
			}else {//使用默认值
				Dict dict = new Dict();
				dict.setType("sx_default");
				dict.setValue("phase_id");
				List<Dict> dicts = dictService.findList(dict);
				if(dicts.size()>0) {
					person.setPhase(phaseService.get(dicts.get(0).getLabel()));
				}
			}
		}
		//检查设置默认阶层
		if(person.getHierarchy()==null || hierarchyService.get(person.getHierarchy())==null) {
			//如果之前已经设置，则使用之前的设置
			if(oldPerson!=null&&oldPerson.getHierarchy()!=null) {//使用原有值
				person.setHierarchy(oldPerson.getHierarchy());
			}else {//使用默认值
				Dict dict = new Dict();
				dict.setType("sx_default");
				dict.setValue("persona_id");
				List<Dict> dicts = dictService.findList(dict);
				if(dicts.size()>0) {
					person.setHierarchy(hierarchyService.get(dicts.get(0).getLabel()));
				}
			}
		}
		try {
			personService.save(person);
			result.put("data", person);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Person person, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Person> list =personService.findList(person);
		for (int i=0; i<list.size(); i++){
			Person e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getNickname());
//			if (type != null && "3".equals(type)){
//				map.put("isParent", true);
//			}
			mapList.add(map);
			
		}
		return mapList;
	}
}