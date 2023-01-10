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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.CategoryNeed;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.MotivationCategory;
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.PhaseNeed;
import com.pcitech.iLife.modules.mod.service.MotivationCategoryService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.PersonaNeedService;
import com.pcitech.iLife.modules.mod.service.PersonaService;
import com.pcitech.iLife.modules.mod.service.PhaseNeedService;
import com.pcitech.iLife.modules.mod.service.PhaseService;
import com.pcitech.iLife.util.Util;

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
	private PhaseNeedService phaseNeedService;
	@Autowired
	private PersonaNeedService personaNeedService;
	@Autowired
	private MotivationCategoryService motivationCategoryService;
	@Autowired
	private MotivationService motivationService;
	
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
		}
		/**
		else{//否则提示选择用户画像
			model.addAttribute("message","选择画像查看其需要构成。");
			return "treeData/none";
		}
		//**/	
		Page<PersonaNeed> page = personaNeedService.findPage(new Page<PersonaNeed>(request, response), personaNeed); 
		
		//增加需要所在的类目。注意：需要逐个填补。效率低下
		List<PersonaNeed> needsWithCategory = Lists.newArrayList();
		
		List<PersonaNeed> needs = page.getList();
		for(PersonaNeed need:needs) {
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
		return "modules/mod/personaNeedList";
	}

	/**
	 * 显示所有待添加Need
	 * 
	 * 查询所有Need，排除已添加Need后返回
	 */
	@RequiresPermissions("mod:personaNeed:edit")
	@RequestMapping(value = {"list2"})
	public String listPendingNeeds(PersonaNeed personaNeed,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		//根据阶段过滤
		Persona persona = personaService.get(treeId);
		List<Motivation> needs =Lists.newArrayList();
		if(persona==null) {
			logger.warn("cannot get persona by id."+treeId);
		}else {
			Map<String,String> params = Maps.newHashMap();
			params.put("personaId", treeId);
			params.put("name", "");//TODO 添加需要名称，能够根据名称过滤
			
			needs = motivationService.findPendingListForPersona(params);
		}
		model.addAttribute("needs", needs);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/personaNeedList2";
	}
	

	/**
	 * 继承上级节点的需要：层级追溯到根节点
	 * @param id 阶段ID
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("mod:personaNeed:edit")
	@RequestMapping(value = "inherit")
	public String inheritFromParent(String id, RedirectAttributes redirectAttributes) {
		logger.error("got persona id. "+id);

		//获取当前节点：
		Persona persona = personaService.get(id);
		if(persona == null) {
			addMessage(redirectAttributes, "非画像节点，忽略");
		}else {
			//尝试从persona克隆需要
			Persona parent = persona.getParent();
			//尝试从phase克隆需要
			Phase phase = persona.getPhase();
			
			if(parent!=null && parent.getId()!=null && parent.getId().trim().length()>0 && !"0".equalsIgnoreCase(parent.getId()))
				copyNeeds(parent, persona);//递归复制上级画像下的需要
			if(phase!=null && phase.getId()!=null && phase.getId().trim().length()>0 && !"0".equalsIgnoreCase(phase.getId()))
				copyNeeds(phase, persona);//递归复制阶段下的需要
			
			addMessage(redirectAttributes, "从父节点克隆需要成功");
		}
		return "redirect:"+Global.getAdminPath()+"/mod/personaNeed/?treeModule=persona&treeId="+id+"&repage";
	}
	
	/**
	 * 从上级persona复制需要
	 * @param fromNode 父节点 toNode当前节点
	 */
	private void copyNeeds(Persona fromNode, Persona toNode) {
		logger.debug("start copy from "+fromNode.getName()+":"+fromNode.getId()+" to "+toNode.getName()+":"+toNode.getId());
		//复制节点下的需要
		PersonaNeed personaNeed = new PersonaNeed();
		personaNeed.setPersona(fromNode);
		personaNeed.setDelFlag("0");
		List<PersonaNeed> needs = personaNeedService.findList(personaNeed);
		for(PersonaNeed need:needs) {//逐条添加到当前节点下，需要判断是否存在，仅在不存在的时候添加
			PersonaNeed query = new PersonaNeed();
			query.setPersona(toNode);
			query.setNeed(need.getNeed());
			List<PersonaNeed> nodes = personaNeedService.findList(query);
			if(nodes.size()==0) {//仅在没有的时候才添加
				query.setDescription(need.getDescription());
				query.setWeight(need.getWeight());
				query.setCreateDate(new Date());
				query.setUpdateDate(new Date());
				personaNeedService.save(query);
			}
		}
		
		//获取父级目录
		Persona parent = fromNode.getParent();
		Phase phase = fromNode.getPhase();
		if(parent!=null && parent.getId()!=null && parent.getId().trim().length()>0 && !"0".equalsIgnoreCase(parent.getId()))
			copyNeeds(parent, toNode);//递归复制上级画像下的需要
		if(phase!=null && phase.getId()!=null && phase.getId().trim().length()>0 && !"0".equalsIgnoreCase(phase.getId()))
			copyNeeds(phase, toNode);//递归复制阶段下的需要
	}
	
	/**
	 * 从上级phase复制需要
	 * @param fromNode 父节点 toNode当前节点
	 */
	private void copyNeeds(Phase fromNode, Persona toNode) {
		logger.debug("start copy from "+fromNode.getName()+":"+fromNode.getId()+" to "+toNode.getName()+":"+toNode.getId());
		//复制节点下的需要
		PhaseNeed phaseNeed = new PhaseNeed();
		phaseNeed.setPhase(fromNode);
		phaseNeed.setDelFlag("0");
		List<PhaseNeed> needs = phaseNeedService.findList(phaseNeed);
		for(PhaseNeed need:needs) {//逐条添加到当前节点下，需要判断是否存在，仅在不存在的时候添加
			PersonaNeed query = new PersonaNeed();
			query.setPersona(toNode);
			query.setNeed(need.getNeed());
			List<PersonaNeed> nodes = personaNeedService.findList(query);
			if(nodes.size()==0) {//仅在没有的时候才添加
				query.setDescription(need.getDescription());
				query.setWeight(need.getWeight());
				query.setCreateDate(new Date());
				query.setUpdateDate(new Date());
				personaNeedService.save(query);
			}
		}
		
		//获取父级目录
		Phase parent = fromNode.getParent();
		if(parent!=null && parent.getId()!=null && parent.getId().trim().length()>0 && !"0".equalsIgnoreCase(parent.getId()))
			copyNeeds(parent, toNode);//递归复制上级目录需要
	}
	
	
	/**
	 * 批量保存需要
	 * @param personaNeeds {personaId:xxx, needs:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("mod:personaNeed:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddNeeds(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String personaId = json.getString("personaId");
		JSONArray needIds = json.getJSONArray("needIds");
		logger.debug("got params.[personaId]"+personaId+" [needIds]"+needIds);
		for(int i=0;i<needIds.size();i++) {
			String needId = needIds.getString(i);
			PersonaNeed personaNeed = new PersonaNeed();
			personaNeed.setPersona(personaService.get(personaId));
			personaNeed.setNeed(motivationService.get(needId));
			personaNeed.setWeight(7.5);//默认为0.75，采用1-10打分
			personaNeed.setCreateDate(new Date());
			personaNeed.setUpdateDate(new Date());
			personaNeed.setDescription("");
			try {
				personaNeedService.save(personaNeed);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("add need failed.[personaId]"+personaId+" [needId]"+needId, ex);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("personaId", personaId);
		return result;
	}
	
	@RequiresPermissions("mod:personaNeed:view")
	@RequestMapping(value = "form")
	public String form(PersonaNeed personaNeed,String pid,String pType, Model model) {
		Persona parent=new Persona();
		if(pType.equals("persona")){
			parent = personaService.get(pid);
			personaNeed.setPersona(parent);
			if(parent.getPhase()!=null)
				personaNeed.setPhase(parent.getPhase());//默认设置阶段与persona一致
		}
		//如果Phase为空，默认直接用persona的phase填充
		if(personaNeed.getPhase()==null) {
			Persona persona = personaService.get(personaNeed.getPersona());
			if(persona != null && persona.getPhase() != null)
				personaNeed.setPhase(persona.getPhase());
		}
		/**
		else {//否则提示选择用户画像
			model.addAttribute("message","选择画像查看其需要构成。");
			return "treeData/none";
		}
		//**/
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
	
	//新增或修改权重
	@ResponseBody
	@RequestMapping(value = "rest/need", method = RequestMethod.POST)
	public JSONObject upsert( @RequestBody PersonaNeed personaNeed) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(personaNeed.getId()==null||personaNeed.getId().trim().length()==0) {//认为是新增
			personaNeed.setId(Util.get32UUID());
			personaNeed.setIsNewRecord(true);
		}
		try {
			personaNeedService.save(personaNeed);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//删除需要
	@ResponseBody
	@RequestMapping(value = "rest/need", method = RequestMethod.PUT)
	public JSONObject delete( @RequestBody PersonaNeed personaNeed) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		try {
			personaNeedService.delete(personaNeed);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//查询待已添加need列表
	@ResponseBody
	@RequestMapping(value = "rest/needs/{personaId}", method = RequestMethod.GET)
	public List<PersonaNeed> listNeeds(@PathVariable String personaId) {
		Persona persona = personaService.get(personaId);
		if(persona==null)
			return Lists.newArrayList();
		PersonaNeed personaNeedQuery = new PersonaNeed();
		personaNeedQuery.setPersona(persona);
		return personaNeedService.findList(personaNeedQuery);
	}
	
	//查询待添加need列表
	@ResponseBody
	@RequestMapping(value = "rest/pending-needs/{personaId}", method = RequestMethod.GET)
	public List<Motivation> listPendingNeeds(@PathVariable String personaId) {
		Map<String,String> params = Maps.newHashMap();
		params.put("personaId", personaId);
		params.put("name", "");//TODO 添加需要名称，能够根据名称过滤
		return motivationService.findPendingListForPersona(params);
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
	public String none(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("message","请在左侧选择一个画像。");
		//默认直接查询所有记录
		PersonaNeed query = new PersonaNeed();
		Page<PersonaNeed> page = personaNeedService.findPage(new Page<PersonaNeed>(request, response), query);
		model.addAttribute("page", page);
//		return "treeData/none";
		return "modules/mod/personaNeedList";
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