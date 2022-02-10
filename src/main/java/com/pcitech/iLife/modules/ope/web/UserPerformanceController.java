/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.web;

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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.entity.UserCategory;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.service.UserCategoryService;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;
import com.pcitech.iLife.modules.ope.entity.UserPerformance;
import com.pcitech.iLife.modules.ope.service.UserPerformanceService;

/**
 * 标注Controller
 * @author chenci
 * @version 2017-09-28
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/userPerformance")
public class UserPerformanceController extends BaseController {
	@Autowired
	private UserMeasureService userMeasureService;
	@Autowired
	private UserCategoryService userCategoryService;
	@Autowired
	private UserPerformanceService userPerformanceService;
	

	@ResponseBody
	@RequestMapping(value = "rest/updateMarkedValue")
	//更新属性值标注，包括level、markedvalue。自动添加更新日期
	public Map<String,String> updateMarkedValue( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double markedValue, 
			@RequestParam(required=true) int level, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("level", level);
		params.put("isReady", 0);//注意：实际将同时设置isReady=0，分析系统将自动读取
		params.put("markedValue", markedValue);
		params.put("updateDate", new Date());
		userPerformanceService.updateMarkedValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "marked value updated.");
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "rest/updateControlValue")
	//更新属性值标注，包括level、controlvalue。自动添加更新日期
	public Map<String,String> updateControlValue( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double controlValue, 
			@RequestParam(required=true) int level, 
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("level", level);
		params.put("controlValue", controlValue);
		params.put("updateDate", new Date());
		userPerformanceService.updateControlValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "control value updated.");
		return result;
	}
	
	@ModelAttribute
	public UserPerformance get(@RequestParam(required=false) String id) {
		UserPerformance entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userPerformanceService.get(id);
		}
		if (entity == null){
			entity = new UserPerformance();
		}
		return entity;
	}
	
	@RequiresPermissions("ope:userPerformance:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserPerformance userPerformance,String treeId,String treeModule,String topType,  HttpServletRequest request, HttpServletResponse response, Model model) {
		if(treeModule.equals("measure")){
			userPerformance.setMeasure(new UserMeasure(treeId));
		}else{//否则提示选择属性
			model.addAttribute("message","选择属性查看标注。");
			return "treeData/none";
		}
		Page<UserPerformance> page = userPerformanceService.findPage(new Page<UserPerformance>(request, response), userPerformance);
		model.addAttribute("page", page);
		model.addAttribute("pid", treeId);
		model.addAttribute("pType", treeModule);
		return "modules/ope/userPerformanceList";
	}

	@RequiresPermissions("ope:userPerformance:view")
	@RequestMapping(value = "form")
	public String form(UserPerformance userPerformance,String pid,String pType,  Model model) {
		UserMeasure measure=new UserMeasure();
		if(pType.equals("measure")){
			measure = userMeasureService.get(pid);
			userPerformance.setMeasure(measure);
		}else {//否则提示选择属性
			model.addAttribute("message","选择属性查看标注。");
			return "treeData/none";
		}
		model.addAttribute("pid", pid);
		model.addAttribute("pType", pType);
		model.addAttribute("userPerformance", userPerformance);
		return "modules/ope/userPerformanceForm";
	}

	@RequiresPermissions("ope:userPerformance:edit")
	@RequestMapping(value = "save")
	public String save(UserPerformance userPerformance,String pid,String pType,  Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userPerformance)){
			return form(userPerformance,pid,pType,model);
		}
		if(userPerformance.getMeasure() == null){//不知道为啥，前端传进来的measure信息丢失了，手动补一次
			userPerformance.setMeasure(userMeasureService.get(pid));
		}
		userPerformance.setIsReady(0);//强制同步
		userPerformanceService.save(userPerformance);
		addMessage(redirectAttributes, "保存标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/userPerformance/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("ope:userPerformance:edit")
	@RequestMapping(value = "delete")
	public String delete(UserPerformance userPerformance,String pid,String pType,  RedirectAttributes redirectAttributes) {
		userPerformanceService.delete(userPerformance);
		addMessage(redirectAttributes, "删除标注成功");
		return "redirect:"+Global.getAdminPath()+"/ope/userPerformance/?treeId="+pid+"&treeModule="+pType+"&repage";
	}
	

	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(UserMeasure measure, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserMeasure> list =userMeasureService.findList(measure);
		for (int i=0; i<list.size(); i++){
			UserMeasure e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
//			if (type != null && "3".equals(type)){
//				map.put("isParent", true);
//			}
			mapList.add(map);
			
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "userPerformances")
	public List<Map<String, Object>> listUserMeasureByCategory(@RequestParam(required=true) String category,HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserMeasure> list =userMeasureService.findByCategory(category);
		for (int i=0; i<list.size(); i++){
			UserMeasure e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("category", e.getCategory().getId());
			map.put("name", e.getName());
			map.put("property", e.getProperty());
			mapList.add(map);
			
		}
		return mapList;
	}
	
	@RequiresPermissions("ope:userPerformance:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","ope/userPerformance");
		model.addAttribute("title","关键属性");
		return "treeData/index";
	}
	
	@RequiresPermissions("ope:userPerformance:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","ope/userPerformance");
		model.addAttribute("title","属性及分类");
		List<UserCategory> userCategoryTree = userCategoryService.findTree();
		model.addAttribute("list", userPerformanceService.getTree(userCategoryTree));
		return "treeData/tree";
	}
	
	@RequiresPermissions("ope:userPerformance:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请选择属性节点。");
		return "treeData/none";
	}
	

	/**
	 * 查询属性分类及属性。返回树结构，其中属性作为叶子节点。
	 * @param extId
	 * @param response
	 * @return
	 */
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeDataWithLeaf(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserCategory> categories = userCategoryService.findList(new UserCategory());
		for (int i=0; i<categories.size(); i++){
			UserCategory e = categories.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
				//查询该类别下的属性
				UserMeasure query = new UserMeasure();
				query.setCategory(e);
				List<UserMeasure> props = userMeasureService.findList(query);
				for(UserMeasure prop:props) {
					Map<String, Object> leafNode = Maps.newHashMap();
					leafNode.put("id", prop.getId());
					leafNode.put("pId", e.getId());
					leafNode.put("name", prop.getName());
					mapList.add(leafNode); 
				}
			}
		}
		return mapList;
	}
	
}