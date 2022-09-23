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
import com.pcitech.iLife.modules.mod.entity.CategoryNeed;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.CategoryNeed;
import com.pcitech.iLife.modules.mod.service.CategoryNeedService;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.PhaseService;

/**
 * 品类需要满足Controller
 * @author qchzhu
 * @version 2020-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/categoryNeed")
public class CategoryNeedController extends BaseController {
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private MotivationService motivationService;
	@Autowired
	private CategoryNeedService categoryNeedService;
	
	@ModelAttribute
	public CategoryNeed get(@RequestParam(required=false) String id) {
		CategoryNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = categoryNeedService.get(id);
		}
		if (entity == null){
			entity = new CategoryNeed();
		}
		return entity;
	}


	@RequiresPermissions("mod:categoryNeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(CategoryNeed categoryNeed,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeId!=null&&treeId.trim().length()>0){
			categoryNeed.setCategory(new ItemCategory(treeId));
		}else{//否则提示选择用户画像
			model.addAttribute("message","选择类目查看其需要构成。");
			return "treeData/none";
		}
			
		Page<CategoryNeed> page = categoryNeedService.findPage(new Page<CategoryNeed>(request, response), categoryNeed); 
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/categoryNeedList";
	}
	

	/**
	 * 显示所有待添加Need
	 * 
	 * 查询所有Need，排除已添加Need后返回
	 */
	@RequiresPermissions("mod:categoryNeed:edit")
	@RequestMapping(value = {"list2"})
	public String listPendingNeeds(CategoryNeed categoryNeed,String treeId ,String treeModule,String topType,HttpServletRequest request, HttpServletResponse response, Model model) {
		//根据诱因过滤
		ItemCategory itemCategory = itemCategoryService.get(treeId);
		List<Motivation> needs =Lists.newArrayList();
		if(itemCategory==null) {
			logger.warn("cannot get itemCategory by id."+treeId);
		}else {
			Map<String,String> params = Maps.newHashMap();
			params.put("categoryId", treeId);
			params.put("name", "");//TODO 添加需要名称，能够根据名称过滤
			
			needs = motivationService.findPendingListForItemCategory(params);
		}
		model.addAttribute("needs", needs);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/mod/categoryNeedList2";
	}
	
	/**
	 * 批量保存需要
	 * @param personaNeeds {categoryId:xxx, needs:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("mod:categoryNeed:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddNeeds(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String itemCategoryId = json.getString("categoryId");
		JSONArray needIds = json.getJSONArray("needIds");
		logger.debug("got params.[itemCategoryId]"+itemCategoryId+" [needIds]"+needIds);
		for(int i=0;i<needIds.size();i++) {
			String needId = needIds.getString(i);
			CategoryNeed categoryNeed = new CategoryNeed();
			categoryNeed.setCategory(itemCategoryService.get(itemCategoryId));
			categoryNeed.setNeed(motivationService.get(needId));
			categoryNeed.setWeight(7.5);//默认为0.75，采用1-10打分
			categoryNeed.setCreateDate(new Date());
			categoryNeed.setUpdateDate(new Date());
			categoryNeed.setDescription("batch added");
			try {
				categoryNeedService.save(categoryNeed);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("add need failed.[personaId]"+itemCategoryId+" [needId]"+needId, ex);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("categoryId", itemCategoryId);
		return result;
	}
	

	@RequiresPermissions("mod:categoryNeed:view")
	@RequestMapping(value = "form")
	public String form(CategoryNeed categoryNeed,String pid,String pType, Model model) {
		ItemCategory parent=new ItemCategory();
		if(pid!=null&&pid.trim().length()>0){
			parent = itemCategoryService.get(pid);
			categoryNeed.setCategory(parent);
		}else {//否则提示选择用户画像
			model.addAttribute("message","选择类目查看其需要满足。");
			return "treeData/none";
		}
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("categoryNeed", categoryNeed);
		return "modules/mod/categoryNeedForm";
	}

	@RequiresPermissions("mod:categoryNeed:edit")
	@RequestMapping(value = "save")
	public String save(CategoryNeed categoryNeed,String pid,String pType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, categoryNeed)){
			return form(categoryNeed,pid,pType,model);
		}
		if(categoryNeed.getCategory() == null){//不知道为啥，前端传进来的itemCategory信息丢失了，手动补一次
			categoryNeed.setCategory(itemCategoryService.get(pid));
		}
		categoryNeedService.save(categoryNeed);
		addMessage(redirectAttributes, "保存商品类目下的需要成功");
		return "redirect:"+Global.getAdminPath()+"/mod/categoryNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("mod:categoryNeed:edit")
	@RequestMapping(value = "delete")
	public String delete(CategoryNeed categoryNeed,String pid,String pType, RedirectAttributes redirectAttributes) {
		categoryNeedService.delete(categoryNeed);
		addMessage(redirectAttributes, "删除商品类目下的需要成功");
		return "redirect:"+Global.getAdminPath()+"/mod/categoryNeed/?treeId="+pid+"&treeModule="+pType+"&repage";
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
		categoryNeedService.updateWeight(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "weight updated.");
		return result;
	}

	@RequiresPermissions("mod:categoryNeed:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/categoryNeed");
		model.addAttribute("title","商品类目需要构成");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:categoryNeed:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/categoryNeed");
		model.addAttribute("title","商品类目");
		List<ItemCategory> list = itemCategoryService.findTree();
		model.addAttribute("list", list);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:categoryNeed:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个类目。");
		return "treeData/none";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ItemCategory> list = itemCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			ItemCategory e = list.get(i);
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