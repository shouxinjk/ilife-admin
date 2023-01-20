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
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.OccasionCategoryService;
import com.pcitech.iLife.modules.mod.service.OccasionNeedService;
import com.pcitech.iLife.modules.mod.service.OccasionService;
import com.pcitech.iLife.util.Util;

/**
 * 诱因对需要的影响Controller
 * @author qchzhu
 * @version 2020-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/occasionNeed")
public class OccasionNeedController extends BaseController {
	@Autowired
	private OccasionService occasionService;
	@Autowired
	private OccasionNeedService occasionNeedService;
	@Autowired
	private OccasionCategoryService occasionCategoryService;
	@Autowired
	private MotivationService motivationService;
	
	@ModelAttribute
	public OccasionNeed get(@RequestParam(required=false) String id) {
		OccasionNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = occasionNeedService.get(id);
		}
		if (entity == null){
			entity = new OccasionNeed();
		}
		return entity;
	}

	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(OccasionNeed occasionNeed,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if("occasion".equalsIgnoreCase(treeModule)){
			occasionNeed.setOccasion(new Occasion(treeId));
		}
		/**
		else{//否则提示选择诱因
			model.addAttribute("message","选择诱因查看其需要构成。");
			return "treeData/none";
		}
		//**/	
		Page<OccasionNeed> page = occasionNeedService.findPage(new Page<OccasionNeed>(request, response), occasionNeed); 
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/occasionNeedList";
	}


	/**
	 * 显示所有待添加Need
	 * 
	 * 查询所有Need，排除已添加Need后返回
	 */
	@RequiresPermissions("mod:occasionNeed:edit")
	@RequestMapping(value = {"list2"})
	public String listPendingNeeds(OccasionNeed occasionNeed,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		//根据诱因过滤
		Occasion occasion = occasionService.get(treeId);
		List<Motivation> needs =Lists.newArrayList();
		if(occasion==null) {
			logger.warn("cannot get occasion by id."+treeId);
		}else {
			Map<String,String> params = Maps.newHashMap();
			params.put("occasionId", treeId);
			params.put("name", "");//TODO 添加需要名称，能够根据名称过滤
			
			needs = motivationService.findPendingListForOccasion(params);
		}
		model.addAttribute("needs", needs);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/occasionNeedList2";
	}
	
	/**
	 * 批量保存需要
	 * @param personaNeeds {occasionId:xxx, needs:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("mod:occasionNeed:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddNeeds(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String occasionId = json.getString("occasionId");
		JSONArray needIds = json.getJSONArray("needIds");
		logger.debug("got params.[occasionId]"+occasionId+" [needIds]"+needIds);
		for(int i=0;i<needIds.size();i++) {
			String needId = needIds.getString(i);
			OccasionNeed occasionNeed = new OccasionNeed();
			occasionNeed.setOccasion(occasionService.get(occasionId));
			occasionNeed.setNeed(motivationService.get(needId));
			occasionNeed.setWeight(7.5);//默认为0.75，采用1-10打分
			occasionNeed.setCreateDate(new Date());
			occasionNeed.setUpdateDate(new Date());
			occasionNeed.setDescription("");
			try {
				occasionNeedService.save(occasionNeed);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("add need failed.[personaId]"+occasionId+" [needId]"+needId, ex);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("occasionId", occasionId);
		return result;
	}
	
	
	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = "form")
	public String form(OccasionNeed occasionNeed,String pid,String pType, Model model) {
		Occasion parent=new Occasion();
		if("occasion".equalsIgnoreCase(pType)){
			parent = occasionService.get(pid);
			occasionNeed.setOccasion(parent);
		}
		/**
		else {//否则提示选择诱因
			model.addAttribute("message","选择诱因查看其需要构成。");
			return "treeData/none";
		}
		//**/
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("occasionNeed", occasionNeed);
		return "modules/mod/occasionNeedForm";
	}

	@RequiresPermissions("mod:occasionNeed:edit")
	@RequestMapping(value = "save")
	public String save(OccasionNeed occasionNeed,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, occasionNeed)){
			return form(occasionNeed,pid,pType,model);
		}
		if(occasionNeed.getOccasion() == null){//不知道为啥，前端传进来的occasion信息丢失了，手动补一次
			occasionNeed.setOccasion(occasionService.get(pid));
		}
		occasionNeedService.save(occasionNeed);
		addMessage(redirectAttributes, "保存诱因影响的需要成功");
		return "redirect:"+Global.getAdminPath()+"/mod/occasionNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:occasionNeed:edit")
	@RequestMapping(value = "delete")
	public String delete(OccasionNeed occasionNeed,String pid,String pType, RedirectAttributes redirectAttributes) {
		occasionNeedService.delete(occasionNeed);
		addMessage(redirectAttributes, "删除诱因影响的需要成功");
		return "redirect:"+Global.getAdminPath()+"/mod/occasionNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
	}

	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/occasionNeed");
		model.addAttribute("title","诱因的需要构成");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/occasionNeed");
		model.addAttribute("title","诱因");
		List<OccasionCategory> list = occasionCategoryService.findTree();
		model.addAttribute("list", occasionService.getTree(list));
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:occasionNeed:view")
	@RequestMapping(value = "none")
	public String none(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("message","请在左侧选择一个诱因。");
		//默认直接查询所有记录
		OccasionNeed query = new OccasionNeed();
		Page<OccasionNeed> page = occasionNeedService.findPage(new Page<OccasionNeed>(request, response), query);
		model.addAttribute("page", page);
//		return "treeData/none";
		return "modules/mod/occasionNeedList";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<OccasionCategory> list = occasionCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			OccasionCategory e = list.get(i);
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
		occasionNeedService.updateWeight(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "weight updated.");
		return result;
	}

	//新增或修改权重
	@ResponseBody
	@RequestMapping(value = "rest/occasion-need", method = RequestMethod.POST)
	public JSONObject upsert( @RequestBody OccasionNeed occasionNeed) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(occasionNeed.getId()==null||occasionNeed.getId().trim().length()==0) {//认为是新增
//			occasionNeed.setId(Util.get32UUID());
			occasionNeed.setId(Util.md5(occasionNeed.getOccasion().getId()+occasionNeed.getNeed().getId()));//occasionId+needId唯一
			occasionNeed.setIsNewRecord(true);
		}
		try {
			occasionNeedService.save(occasionNeed);
			result.put("data", occasionNeed);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("success", false);
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//删除需要
	@ResponseBody
	@RequestMapping(value = "rest/occasion-need", method = RequestMethod.PUT)
	public JSONObject delete( @RequestBody OccasionNeed occasionNeed) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		try {
			occasionNeedService.delete(occasionNeed);
			result.put("success", true);
		}catch(Exception ex) {
			result.put("error", ex.getMessage());
		}
		return result;
	}
	
	//查询已添加need列表
	@ResponseBody
	@RequestMapping(value = "rest/needs/{occasionId}", method = RequestMethod.GET)
	public List<OccasionNeed> listNeeds(@PathVariable String occasionId) {
		Occasion occasion = occasionService.get(occasionId);
		if(occasion==null)
			return Lists.newArrayList();
		OccasionNeed occasionNeedQuery = new OccasionNeed();
		occasionNeedQuery.setOccasion(occasion);
		return occasionNeedService.findList(occasionNeedQuery);
	}
	
	//查询待添加need列表
	@ResponseBody
	@RequestMapping(value = "rest/pending-needs/{occasionId}", method = RequestMethod.GET)
	public List<Motivation> listPendingNeeds(@PathVariable String occasionId) {
		Map<String,String> params = Maps.newHashMap();
		params.put("occasionId", occasionId);
		params.put("name", "");//TODO 添加需要名称，能够根据名称过滤
		return motivationService.findPendingListForOccasion(params);
	}


	
}