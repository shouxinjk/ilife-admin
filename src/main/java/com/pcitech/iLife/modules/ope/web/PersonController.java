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
import com.pcitech.iLife.modules.mod.entity.Hierarchy;
import com.pcitech.iLife.modules.ope.entity.Person;
import com.pcitech.iLife.modules.ope.service.PersonService;

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