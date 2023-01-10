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
import com.github.binarywang.wx.util.MD5Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.MotivationCategory;
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

import oracle.net.aso.MD5;

/**
 * 阶段需要构成Controller
 * @author ilife
 * @version 2022-02-09
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/phaseNeed")
public class PhaseNeedController extends BaseController {

	@Autowired
	private PhaseService phaseService;
	
	@Autowired
	private PhaseNeedService phaseNeedService;
	
	@Autowired
	private MotivationCategoryService motivationCategoryService;
	@Autowired
	private MotivationService motivationService;
	@Autowired
	private PersonaService personaService;
	@Autowired
	private PersonaNeedService personaNeedService;
	
	@ModelAttribute
	public PhaseNeed get(@RequestParam(required=false) String id) {
		PhaseNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = phaseNeedService.get(id);
		}
		if (entity == null){
			entity = new PhaseNeed();
		}
		return entity;
	}
	

	/**
	 * 继承上级节点的需要：层级追溯到根节点
	 * @param id 阶段ID
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("mod:phaseNeed:edit")
	@RequestMapping(value = "inherit")
	public String inheritFromParent(String id, RedirectAttributes redirectAttributes) {
		logger.error("got phase id. "+id);

		//获取上级节点：
		Phase phase = phaseService.get(id);
		if(phase == null) {
			addMessage(redirectAttributes, "无法获取父节点，忽略");
		}else {//尝试获取上级
			Phase parent = phase.getParent();
			if(parent == null) {
				addMessage(redirectAttributes, "没有父节点，忽略");
			}else {
				logger.error("try get parent node by id. "+id);
				parent = phaseService.get(parent);

				if(parent!=null) {
					copyNeeds(parent, phase);//递归复制所有节点下的需要
					addMessage(redirectAttributes, "复制父节点需要成功");
				}else {
					addMessage(redirectAttributes, "不能获取父节点，忽略");
				}
			}
		}
		return "redirect:"+Global.getAdminPath()+"/mod/phaseNeed/?treeId="+id+"&repage";
	}
	
	/**
	 * 层递复制目录需要
	 * @param fromNode 父节点 toNode当前节点
	 */
	private void copyNeeds(Phase fromNode, Phase toNode) {
		logger.debug("start copy from "+fromNode.getName()+":"+fromNode.getId()+" to "+toNode.getName()+":"+toNode.getId());
		//复制节点下的需要
		PhaseNeed phaseNeed = new PhaseNeed();
		phaseNeed.setPhase(fromNode);
		phaseNeed.setDelFlag("0");
		List<PhaseNeed> needs = phaseNeedService.findList(phaseNeed);
		for(PhaseNeed need:needs) {//逐条添加到当前节点下，需要判断是否存在，仅在不存在的时候添加
			PhaseNeed query = new PhaseNeed();
			query.setPhase(toNode);
			query.setNeed(need.getNeed());
			List<PhaseNeed> nodes = phaseNeedService.findList(query);
			if(nodes.size()==0) {//仅在没有的时候才添加
				query.setDescription(need.getDescription());
				query.setWeight(need.getWeight());
				query.setCreateDate(new Date());
				query.setUpdateDate(new Date());
				phaseNeedService.save(query);
			}
		}
		
		//获取父级目录
		Phase parent = fromNode.getParent();
		if(parent!=null && parent.getId()!=null && parent.getId().trim().length()>0 && !"0".equalsIgnoreCase(parent.getId()))
			copyNeeds(parent, toNode);//递归复制上级目录需要
	}
	
	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(PhaseNeed phaseNeed,String treeId ,String treeModule,String topType, HttpServletRequest request, HttpServletResponse response, Model model) {
		//根据阶段过滤
		Phase phase = phaseService.get(treeId);
		phaseNeed.setPhase(phase);
		Page<PhaseNeed> page = phaseNeedService.findPage(new Page<PhaseNeed>(request, response), phaseNeed); 
		
		//增加需要所在的类目。注意：需要逐个填补。效率低下
		List<PhaseNeed> needsWithCategory = Lists.newArrayList();
		
		List<PhaseNeed> needs = page.getList();
		for(PhaseNeed need:needs) {
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
		return "modules/mod/phaseNeedList";
	}

	/**
	 * 显示所有待添加Need
	 * 
	 * 查询所有Need，排除已添加Need后返回
	 */
	@RequiresPermissions("mod:phaseNeed:edit")
	@RequestMapping(value = {"list2"})
	public String listPendingNeeds(String pid, HttpServletRequest request, HttpServletResponse response, Model model) {
		//根据阶段过滤
		Phase phase = phaseService.get(pid);
		List<Motivation> needs =Lists.newArrayList();
		if(phase==null) {
			logger.error("cannot get phase by id."+pid);
		}else {
			Map<String,String> params = Maps.newHashMap();
			params.put("phaseId", pid);
			params.put("name", "");//TODO 添加需要名称，能够根据名称过滤
			
			needs = motivationService.findPendingListForPhase(params);
		}
		model.addAttribute("needs", needs);
		model.addAttribute("pid", pid);
		return "modules/mod/phaseNeedList2";
	}
	
	/**
	 * 批量保存需要到阶段下
	 * @param phaseNeeds {phaseId:xxx, needs:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("mod:phaseNeed:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddNeeds(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String phaseId = json.getString("phaseId");
		JSONArray needIds = json.getJSONArray("needIds");
		logger.debug("got params.[phaseId]"+phaseId+" [needIds]"+needIds);
		for(int i=0;i<needIds.size();i++) {
			String needId = needIds.getString(i);
			PhaseNeed phaseNeed = new PhaseNeed();
			phaseNeed.setPhase(phaseService.get(phaseId));
			phaseNeed.setNeed(motivationService.get(needId));
			phaseNeed.setWeight(7.5);//默认为0.75，采用1-10打分
			phaseNeed.setCreateDate(new Date());
			phaseNeed.setUpdateDate(new Date());
			phaseNeed.setDescription("");
			try {
				phaseNeedService.save(phaseNeed);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("add need failed.[phaseId]"+phaseId+" [needId]"+needId, ex);
			}
			
			//同步添加到该阶段所有画像下
			List<Persona> personas = personaService.findByPhaseId(phaseNeed.getPhase().getId());
			for(Persona persona:personas) {
				//注意效率较低：是逐条查找
				PersonaNeed query = new PersonaNeed();
				query.setPersona(persona);
				query.setNeed(phaseNeed.getNeed());
				List<PersonaNeed> personaNeeds = personaNeedService.findList(query);
				if(personaNeeds.size()==0) {//仅在没有的情况下添加
					query.setWeight(7.5);
					query.setCreateDate(new Date());
					query.setUpdateDate(new Date());
					personaNeedService.save(query);
				}
			}
			
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("phaseId", phaseId);
		return result;
	}

	
	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = "form")
	public String form(PhaseNeed phaseNeed,String pid,String pType,  Model model) {
		//设置当前选中的阶段
		Phase phase = phaseService.get(phaseNeed.getPhase());
		phaseNeed.setPhase(phase);
		
		model.addAttribute("phaseNeed", phaseNeed);
		model.addAttribute("pid", phase.getId());
		model.addAttribute("pType", pType);
		return "modules/mod/phaseNeedForm";
	}

	@RequiresPermissions("mod:phaseNeed:edit")
	@RequestMapping(value = "save")
	public String save(PhaseNeed phaseNeed,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, phaseNeed)){
			return form(phaseNeed,pid,pType, model);
		}
		phaseNeedService.save(phaseNeed);
		addMessage(redirectAttributes, "保存阶段需要构成成功");
		return "redirect:"+Global.getAdminPath()+"/mod/phaseNeed/?treeId="+phaseNeed.getPhase().getId()+"&pid="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:phaseNeed:edit")
	@RequestMapping(value = "delete")
	public String delete(PhaseNeed phaseNeed,String pid,String pType, RedirectAttributes redirectAttributes) {
		phaseNeedService.delete(phaseNeed);
		
		//同步将need从该阶段下的所有画像中移除
		List<Persona> personas = personaService.findByPhaseId(phaseNeed.getPhase().getId());
		for(Persona persona:personas) {
			//注意效率较低：是逐个删除
			PersonaNeed query = new PersonaNeed();
			query.setPersona(persona);
			query.setNeed(phaseNeed.getNeed());
			List<PersonaNeed> personaNeeds = personaNeedService.findList(query);
			for(PersonaNeed personaNeed:personaNeeds) {
				personaNeedService.delete(personaNeed);
			}
		}
		
		addMessage(redirectAttributes, "删除阶段需要构成成功");
		return "redirect:"+Global.getAdminPath()+"/mod/phaseNeed/?treeId="+phaseNeed.getPhase().getId()+"&pid="+pid+"&treeModule="+pType+"&repage";
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
		phaseNeedService.updateWeight(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "weight updated.");
		return result;
	}
	

	//新增或修改权重
	@ResponseBody
	@RequestMapping(value = "rest/need", method = RequestMethod.POST)
	public JSONObject upsert( @RequestBody PhaseNeed phaseNeed) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(phaseNeed.getId()==null||phaseNeed.getId().trim().length()==0) {//认为是新增
			phaseNeed.setId(Util.get32UUID());
			phaseNeed.setIsNewRecord(true);
		}
		try {
			phaseNeedService.save(phaseNeed);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//删除需要
	@ResponseBody
	@RequestMapping(value = "rest/need", method = RequestMethod.PUT)
	public JSONObject delete( @RequestBody PhaseNeed phaseNeed) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		try {
			phaseNeedService.delete(phaseNeed);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//查询待已添加need列表
	@ResponseBody
	@RequestMapping(value = "rest/needs/{categoryId}", method = RequestMethod.GET)
	public List<PhaseNeed> listNeeds(@PathVariable String phaseId) {
		Phase phase = phaseService.get(phaseId);
		if(phase==null)
			return Lists.newArrayList();
		PhaseNeed phaseNeedQuery = new PhaseNeed();
		phaseNeedQuery.setPhase(phase);
		return phaseNeedService.findList(phaseNeedQuery);
	}
	
	//查询待添加need列表
	@ResponseBody
	@RequestMapping(value = "rest/pending-needs/{categoryId}", method = RequestMethod.GET)
	public List<Motivation> listPendingNeeds(@PathVariable String phaseId) {
		Map<String,String> params = Maps.newHashMap();
		params.put("phaseId", phaseId);
		params.put("name", "");//TODO 添加需要名称，能够根据名称过滤
		return motivationService.findPendingListForPhase(params);
	}


	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/phaseNeed");
		model.addAttribute("title","阶段需要构成");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/phaseNeed");
		model.addAttribute("title","成长阶段");
		List<Phase> phaseTree = phaseService.findTree();
		model.addAttribute("list", phaseTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:phaseNeed:view")
	@RequestMapping(value = "none")
	public String none(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("message","请在左侧选择一个阶段。");
		//默认直接查询所有记录
		PhaseNeed query = new PhaseNeed();
		Page<PhaseNeed> page = phaseNeedService.findPage(new Page<PhaseNeed>(request, response), query);
		model.addAttribute("page", page);
//		return "treeData/none";
		return "modules/mod/phaseNeedList";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Phase> list = phaseService.findTree();
		for (int i=0; i<list.size(); i++){
			Phase e = list.get(i);
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