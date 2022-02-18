/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Channel;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.PersonNeed;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.PersonNeedService;
import com.pcitech.iLife.modules.ope.entity.Person;
import com.pcitech.iLife.modules.ope.service.PersonService;

/**
 * 用户需要构成Controller
 * @author ilife
 * @version 2022-02-17
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/personNeed")
public class PersonNeedController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(PersonNeedController.class);
	@Autowired
	private PersonService personService;
	@Autowired
	private MotivationService motivationService;
	@Autowired
	private PersonNeedService personNeedService;
	
	@ModelAttribute
	public PersonNeed get(@RequestParam(required=false) String id) {
		PersonNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = personNeedService.get(id);
		}
		if (entity == null){
			entity = new PersonNeed();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:personNeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(PersonNeed personNeed, HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.error("try to list person needs.[personNeed]"+personNeed);
		if(personNeed.getPerson()==null) {
			personNeed.setPerson(new Person());
		}
		if(personNeed.getNeed()==null) {
			personNeed.setNeed(new Motivation());
		}
		Page<PersonNeed> page = personNeedService.findPage(new Page<PersonNeed>(request, response), personNeed); 
		model.addAttribute("page", page);
		return "modules/mod/personNeedList";
	}

	@RequiresPermissions("mod:personNeed:view")
	@RequestMapping(value = "form")
	public String form(PersonNeed personNeed, Model model) {
		logger.error("try to init person need form.[personNeed]"+JSON.toJSONString(personNeed));
		//预处理person
		if(personNeed.getPerson()==null) {
			personNeed.setPerson(new Person());
		}else if(personNeed.getPerson().getId()!=null) {//从前端仅传递了id，则重新加载
			Person p = personService.get(personNeed.getPerson().getId());
			if(p!=null) {
				personNeed.setPerson(p);
			}else {
				personNeed.setPerson(new Person());
			}
		}else {
			personNeed.setPerson(new Person());
		}
		//预处理need
		if(personNeed.getNeed()==null) {
			personNeed.setNeed(new Motivation());
		}else if(personNeed.getNeed().getId()!=null) {//从前端仅传递了id，则重新加载
			Motivation need = motivationService.get(personNeed.getNeed().getId());
			if(need!=null) {
				personNeed.setNeed(need);
			}else {
				personNeed.setNeed(new Motivation());
			}
		}else {
			personNeed.setNeed(new Motivation());
		}
		//新建时设置排序
		if(personNeed.getId()==null)
			personNeed.setSort("10");//设置默认排序
		model.addAttribute("personNeed", personNeed);
		return "modules/mod/personNeedForm";
	}

	@RequiresPermissions("mod:personNeed:edit")
	@RequestMapping(value = "save")
	public String save(PersonNeed personNeed, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, personNeed)){
			return form(personNeed, model);
		}
		personNeedService.save(personNeed);
		addMessage(redirectAttributes, "保存用户需要构成成功");
		return "redirect:"+Global.getAdminPath()+"/mod/personNeed/?person.id="+personNeed.getPerson().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:personNeed:edit")
	@RequestMapping(value = "delete")
	public String delete(PersonNeed personNeed, RedirectAttributes redirectAttributes) {
		personNeedService.delete(personNeed);
		addMessage(redirectAttributes, "删除用户需要构成成功");
		return "redirect:"+Global.getAdminPath()+"/mod/personNeed/?person.id="+personNeed.getPerson().getId()+"&repage";
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
		personNeedService.updateWeight(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "weight updated.");
		return result;
	}

}