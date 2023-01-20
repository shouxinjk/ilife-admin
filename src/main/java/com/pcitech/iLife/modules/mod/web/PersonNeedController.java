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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Channel;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.PersonNeed;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.PhaseNeed;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.PersonNeedService;
import com.pcitech.iLife.modules.mod.service.PersonaNeedService;
import com.pcitech.iLife.modules.mod.service.PersonaService;
import com.pcitech.iLife.modules.ope.entity.Person;
import com.pcitech.iLife.modules.ope.service.PersonService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.Util;

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
	@Autowired
	private PersonaNeedService personaNeedService;
	@Autowired
	private PersonaService personaService;
	
	@Autowired
	private DictService dictService;
	
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
	
	//新增或修改权重
	@ResponseBody
	@RequestMapping(value = "rest/person-need", method = RequestMethod.POST)
	public JSONObject upsert( @RequestBody PersonNeed personNeed) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(personNeed.getId()==null||personNeed.getId().trim().length()==0) {//认为是新增
			//personNeed.setId(Util.get32UUID());
			personNeed.setId(Util.md5(personNeed.getPerson().getId()+personNeed.getNeed().getId()));//personId+needId唯一
			personNeed.setIsNewRecord(true);
		}
		try {
			personNeedService.save(personNeed);
			result.put("data", personNeed);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("success", false);
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//删除需要
	@ResponseBody
	@RequestMapping(value = "rest/person-need", method = RequestMethod.PUT)
	public JSONObject delete( @RequestBody PersonNeed personNeed) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		try {
			personNeedService.delete(personNeed);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//查询已添加need列表
	//对于空白列表，需要从所属画像clone需要构成
	@ResponseBody
	@RequestMapping(value = "rest/needs/{personId}", method = RequestMethod.GET)
	public List<PersonNeed> listNeeds(@PathVariable String personId) {
		Person person = personService.get(personId);
		if(person==null)
			return Lists.newArrayList();
		PersonNeed personNeedQuery = new PersonNeed();
		personNeedQuery.setPerson(person);
		List<PersonNeed> personNeeds = personNeedService.findList(personNeedQuery);
		if(personNeeds.size()==0) { //从画像克隆
			Persona persona = person.getPersona();
			if(persona==null) { //如果用户上没有画像信息，则直接从默认初始画像获取
				Dict dict = new Dict();
				dict.setType("sx_default");
				dict.setValue("persona_id");
				List<Dict> dicts = dictService.findList(dict);
				if(dicts.size()>0) {
					persona = personaService.get(dicts.get(0).getLabel());
				}
			}
			//从画像克隆需要
			if(persona != null) {
				PersonaNeed q = new PersonaNeed();
				q.setPersona(persona);
				List<PersonaNeed> personaNeeds = personaNeedService.findList(q);
				for(PersonaNeed personaNeed:personaNeeds) {
					PersonNeed personNeed = new PersonNeed();
					personNeed.setPerson(person);
					personNeed.setNeed(personaNeed.getNeed());
					personNeed.setWeight(personaNeed.getWeight());
					personNeed.setIsNewRecord(true);
					personNeed.setId(Util.md5(person.getId()+personaNeed.getNeed().getId()));
					try {
						personNeedService.save(personNeed);
					}catch(Exception ex) {
						//do nothing
					}
				}
			}
		}
		return personNeedService.findList(personNeedQuery);
	}
	
	//查询待添加need列表
	@ResponseBody
	@RequestMapping(value = "rest/pending-needs/{personId}", method = RequestMethod.GET)
	public List<Motivation> listPendingNeeds(@PathVariable String personId) {
		Map<String,String> params = Maps.newHashMap();
		params.put("personId", personId);
		params.put("name", "");//TODO 添加需要名称，能够根据名称过滤
		return motivationService.findPendingListForPerson(params);
	}

	
}