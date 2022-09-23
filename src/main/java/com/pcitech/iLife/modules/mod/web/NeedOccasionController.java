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
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.CategoryNeed;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.MotivationCategory;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.service.MotivationCategoryService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.OccasionCategoryService;
import com.pcitech.iLife.modules.mod.service.OccasionNeedService;
import com.pcitech.iLife.modules.mod.service.OccasionService;
import com.pcitech.iLife.modules.mod.service.PhaseService;

/**
 * 诱因对需要的影响Controller
 * @author qchzhu
 * @version 2020-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/needOccasion")
public class NeedOccasionController extends BaseController {
	@Autowired
	private MotivationService motivationService;
	@Autowired
	private OccasionNeedService needOccasionService;
	@Autowired
	private OccasionService occasionService;
	@Autowired
	private MotivationCategoryService motivationCategoryService;
	@Autowired
	private PhaseService phaseService;
	
	@ModelAttribute
	public OccasionNeed get(@RequestParam(required=false) String id) {
		OccasionNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = needOccasionService.get(id);
		}
		if (entity == null){
			entity = new OccasionNeed();
		}
		return entity;
	}

	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = {"list", ""})
	public String list(OccasionNeed needOccasion,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if("motivation".equalsIgnoreCase(treeModule)){
			needOccasion.setNeed(new Motivation(treeId));
		}else{//否则提示选择动机
			model.addAttribute("message","选择动机查看可能影响的诱因。");
			return "treeData/none";
		}
			
		Page<OccasionNeed> page = needOccasionService.findPage(new Page<OccasionNeed>(request, response), needOccasion); 
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/needOccasionList";
	}


	/**
	 * 显示所有待添加Occasion
	 * 
	 * 查询所有Need，排除已添加Need后返回
	 */
	@RequiresPermissions("mod:needOccasion:edit")
	@RequestMapping(value = {"list2"})
	public String listPendingNeeds(OccasionNeed needOccasion,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		//根据需要过滤
		Motivation motivation = motivationService.get(treeId);
		List<Occasion> occasions =Lists.newArrayList();
		if(motivation==null) {
			logger.warn("cannot get itemCategory by id."+treeId);
		}else {
			Map<String,String> params = Maps.newHashMap();
			params.put("needId", treeId);
			params.put("name", "");//TODO 添加需要名称，能够根据名称过滤
			
			occasions = occasionService.findPendingListForNeed(params);
		}
		model.addAttribute("occasions", occasions);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/needOccasionList2";
	}
	
	/**
	 * 批量保存需要
	 * @param personaNeeds {categoryId:xxx, needs:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("mod:needOccasion:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddNeeds(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String needId = json.getString("needId");
		JSONArray occasionIds = json.getJSONArray("occasionIds");
		logger.debug("got params.[needId]"+needId+" [needIds]"+occasionIds);
		for(int i=0;i<occasionIds.size();i++) {
			String occasionId = occasionIds.getString(i);
			OccasionNeed occasionNeed = new OccasionNeed();
			occasionNeed.setNeed(motivationService.get(needId));
			occasionNeed.setOccasion(occasionService.get(occasionId));
			occasionNeed.setWeight(7.5);//默认为0.75，采用1-10打分
			occasionNeed.setSort(10);
			occasionNeed.setCreateDate(new Date());
			occasionNeed.setUpdateDate(new Date());
			occasionNeed.setDescription("batch added");
			try {
				needOccasionService.save(occasionNeed);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("add need failed.[needId]"+needId+" [occasionId]"+occasionId, ex);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("needId", needId);
		return result;
	}
	
	
	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = "form")
	public String form(OccasionNeed needOccasion,String pid,String pType, Model model) {
		Motivation parent=new Motivation();
		if("motivation".equalsIgnoreCase(pType)){
			parent = motivationService.get(pid);
			needOccasion.setNeed(parent);
		}else {//否则提示选择诱因
			model.addAttribute("message","选择动机查看影响的诱因。");
			return "treeData/none";
		}
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("needOccasion", needOccasion);
		return "modules/mod/needOccasionForm";
	}

	@RequiresPermissions("mod:needOccasion:edit")
	@RequestMapping(value = "save")
	public String save(OccasionNeed needOccasion,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, needOccasion)){
			return form(needOccasion,pid,pType,model);
		}
		if(needOccasion.getNeed() == null){//不知道为啥，前端传进来的motivation信息丢失了，手动补一次
			needOccasion.setNeed(motivationService.get(pid));
		}
		needOccasionService.save(needOccasion);
		addMessage(redirectAttributes, "保存成功");
		return "redirect:"+Global.getAdminPath()+"/mod/needOccasion/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:needOccasion:edit")
	@RequestMapping(value = "delete")
	public String delete(OccasionNeed needOccasion,String pid,String pType, RedirectAttributes redirectAttributes) {
		needOccasionService.delete(needOccasion);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:"+Global.getAdminPath()+"/mod/needOccasion/?treeId="+pid+"&treeModule="+pType+"&repage";
	}

	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/needOccasion");
		model.addAttribute("title","影响需要的诱因");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/needOccasion");
		model.addAttribute("title","需要清单");
		List<MotivationCategory> categories = motivationCategoryService.findTree();
		List<Phase> phases = phaseService.findTree();
		model.addAttribute("list", motivationService.getTreeWithLeaf(phases,categories));
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:needOccasion:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个动机。");
		return "treeData/none";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<MotivationCategory> list = motivationCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			MotivationCategory e = list.get(i);
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