/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.diy.entity.ProposalScheme;
import com.pcitech.iLife.modules.diy.entity.ProposalSection;
import com.pcitech.iLife.modules.diy.entity.Solution;
import com.pcitech.iLife.modules.diy.entity.SolutionItem;
import com.pcitech.iLife.modules.diy.service.ProposalSchemeService;
import com.pcitech.iLife.modules.diy.service.ProposalSectionService;
import com.pcitech.iLife.modules.diy.service.SolutionItemService;
import com.pcitech.iLife.modules.diy.service.SolutionService;
import com.pcitech.iLife.util.Util;

/**
 * 个性定制方案Controller
 * @author chenci
 * @version 2022-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/solution")
public class SolutionController extends BaseController {

	@Autowired
	private SolutionService solutionService;
	@Autowired
	private SolutionItemService solutionItemService;
	@Autowired
	private ProposalSchemeService proposalSchemeService;
	@Autowired
	private ProposalSectionService proposalSectionService;
	
	@ModelAttribute
	public Solution get(@RequestParam(required=false) String id) {
		Solution entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = solutionService.get(id);
		}
		if (entity == null){
			entity = new Solution();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:solution:view")
	@RequestMapping(value = {"list", ""})
	public String list(Solution solution, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Solution> page = solutionService.findPage(new Page<Solution>(request, response), solution); 
		model.addAttribute("page", page);
		return "modules/diy/solutionList";
	}

	@RequiresPermissions("diy:solution:view")
	@RequestMapping(value = "form")
	public String form(Solution solution, Model model) {
		model.addAttribute("solution", solution);
		return "modules/diy/solutionForm";
	}

	@RequiresPermissions("diy:solution:edit")
	@RequestMapping(value = "save")
	public String save(Solution solution, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, solution)){
			return form(solution, model);
		}
		solutionService.save(solution);
		addMessage(redirectAttributes, "保存个性定制方案成功");
		return "redirect:"+Global.getAdminPath()+"/diy/solution/?repage";
	}
	
	@RequiresPermissions("diy:solution:edit")
	@RequestMapping(value = "delete")
	public String delete(Solution solution, RedirectAttributes redirectAttributes) {
		solutionService.delete(solution);
		addMessage(redirectAttributes, "删除个性定制方案成功");
		return "redirect:"+Global.getAdminPath()+"/diy/solution/?repage";
	}

	/**
	 * 查询所有可用定制方案列表
	 * @param solution
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Solution solution, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Solution> list = solutionService.findList(solution);
		for (int i=0; i<list.size(); i++){
			Solution e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
			mapList.add(map);
			
		}
		return mapList;
	}
	
	//启用、禁用
	@ResponseBody
	@RequestMapping(value = "rest/status/{solutionId}/{status}", method = RequestMethod.POST)
	public JSONObject changeStatus(@PathVariable String solutionId, @PathVariable Integer status) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( solutionId==null || solutionId.trim().length() == 0 || status==null ) {
			result.put("msg", "Both solutionId and status are required.");
			return result;
		}
		Solution solution = solutionService.get(solutionId);
		solution.setStatus(status);
		solutionService.save(solution);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 根据openid查询所有方案
	 * @param type: by: 创建用户； for：目标用户
	 * @param openid 用户openid
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/byOpenid/{type}/{openid}", method = RequestMethod.GET)
	public JSONObject listByCreateUser(@PathVariable String type, @PathVariable String openid) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( type==null || type.trim().length() == 0 || openid==null  || openid.trim().length() == 0) {
			result.put("msg", "Both type and openid are required.");
			return result;
		}
		Solution solution = new Solution();
		if("by".equalsIgnoreCase(type)) {
			solution.setByOpenid(openid);
		}else if("for".equalsIgnoreCase(type)) {
			solution.setForOpenid(openid);
		}else {
			result.put("msg", "Type must be one of by or for.");
			return result;
		}
		result.put("success", true);
		result.put("data", solutionService.findList(solution));
		return result;
	}
	
	/**
	 * 根据具体参数查找列表
	 * @param solution 查询条件
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/search", method = RequestMethod.POST)
	public JSONObject search(@RequestBody Solution solution) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("data", solutionService.findList(solution));
		return result;
	}
	
	/**
	 * 根据ID克隆定制方案
	 * @param params {byOpenid:xxx, forOpenid:xxx} 均为可选
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/clone/{solutionId}", method = RequestMethod.POST)
	public JSONObject clone(@PathVariable String solutionId, @RequestBody JSONObject params) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( solutionId==null || solutionId.trim().length() == 0) {
			result.put("msg", "solutionId is required.");
			return result;
		}
		//得到原有的方案及条目
		Solution solution = solutionService.get(solutionId);
		if( solution==null) {
			result.put("msg", "Solution not found.[id]"+solutionId);
			return result;
		}
		SolutionItem solutionItem = new SolutionItem();
		solutionItem.setSolution(solution);
		List<SolutionItem> solutionItems = solutionItemService.findList(solutionItem);
		
		//先克隆solution
		Solution oldSolution = solution;
		String id = Util.get32UUID();//重新设置ID作为新纪录
		solution.setRefer(oldSolution);//注意：要首先设置引用条目为原始记录
		solution.setIsNewRecord(true);
		solution.setName(solution.getName()+" copy");
		solution.setId(id);
		
		if(params.getString("byOpenid")!=null) {
			solution.setByOpenid(params.getString("byOpenid"));
		}else {
			solution.setByOpenid(null);
		}
		if(params.getString("forOpenid")!=null) {
			solution.setForOpenid(params.getString("forOpenid"));
		}else {
			solution.setForOpenid(null);
		}
		solution.setCreateDate(new Date());
		solution.setUpdateDate(new Date());
		solutionService.save(solution);
		
		//然后逐条克隆solutionItem
		for(SolutionItem item:solutionItems) {
			item.setSolution(solution);
			item.setIsNewRecord(true);
			item.setId(Util.get32UUID());//重新设置ID作为新纪录
			item.setCreateDate(new Date());
			item.setUpdateDate(new Date());
			solutionItemService.save(item);			
		}
		Solution nSolution = solutionService.get(id);
		SolutionItem nSolutionItem = new SolutionItem();
		nSolutionItem.setSolution(nSolution);
		
		result.put("success", true);
		result.put("solution", nSolution);//返回新建立的solution
		result.put("solutionItems", solutionItemService.findList(nSolutionItem));//返回新建立的solutionItem列表
		return result;
	}
	
	/**
	 * 根据Id查询solution详情
	 * @param solutionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/solution/{solutionId}", method = RequestMethod.GET)
	public JSONObject getDetail(@PathVariable String solutionId) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( solutionId==null || solutionId.trim().length() == 0) {
			result.put("msg", "solutionId is required.");
			return result;
		}
		//得到原有的方案及条目
		Solution solution = solutionService.get(solutionId);
		if( solution==null) {
			result.put("msg", "Solution not found.[id]"+solutionId);
			return result;
		}
		result.put("success", true);
		result.put("data", solution);
		return result;
	}
	
	/**
	 * 根据Id删除solution条目
	 * @param solutionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/solutionItem/{solutionItemId}", method = RequestMethod.POST)
	public JSONObject removeItem(@PathVariable String solutionItemId) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( solutionItemId==null || solutionItemId.trim().length() == 0) {
			result.put("msg", "solutionId is required.");
			return result;
		}
		//得到原有的方案及条目
		SolutionItem solutionItem = solutionItemService.get(solutionItemId);
		if( solutionItem==null) {
			result.put("msg", "SolutionItem not found.[id]"+solutionItemId);
			return result;
		}
		//直接删除
		solutionItemService.delete(solutionItem);
		result.put("success", true);
		return result;
	}
	
	//交换两个条目的priority
	@ResponseBody
	@RequestMapping(value = "rest/swap/{solutionItemId1}/{solutionItemId2}", method = RequestMethod.POST)
	public JSONObject swap(@PathVariable String solutionItemId1,@PathVariable String solutionItemId2) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( solutionItemId1==null || solutionItemId1.trim().length() == 0
				|| solutionItemId2==null || solutionItemId2.trim().length() == 0) {
			result.put("msg", "Both solutionItemId1 and solutionItemId2 are required.");
			return result;
		}
		//得到原有的方案及条目
		SolutionItem solutionItem1 = solutionItemService.get(solutionItemId1);
		if( solutionItem1==null) {
			result.put("msg", "SolutionItem1 not found.[id]"+solutionItemId1);
			return result;
		}
		SolutionItem solutionItem2 = solutionItemService.get(solutionItemId2);
		if( solutionItem2==null) {
			result.put("msg", "SolutionItem2 not found.[id]"+solutionItemId2);
			return result;
		}
		//交换priority
		double priority1 = solutionItem1.getPriority();
		double priority2 = solutionItem2.getPriority();
		solutionItem1.setPriority(priority2);
		solutionItem2.setPriority(priority1);
		solutionItemService.save(solutionItem1);
		solutionItemService.save(solutionItem2);
		result.put("success", true);
		return result;
	}

	/**
	 * 根据ID获取所有下级条目
	 * @param solutionID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/items/{solutionId}", method = RequestMethod.GET)
	public JSONObject listItems(@PathVariable String solutionId) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( solutionId==null || solutionId.trim().length() == 0) {
			result.put("msg", "solutionId is required.");
			return result;
		}
		//得到原有的方案及条目
		Solution solution = solutionService.get(solutionId);
		if( solution==null) {
			result.put("msg", "Solution not found.[id]"+solutionId);
			return result;
		}
		SolutionItem solutionItem = new SolutionItem();
		solutionItem.setSolution(solution);
		List<SolutionItem> solutionItems = solutionItemService.findList(solutionItem);
		
		result.put("success", true);
		result.put("data", solutionItems);
		return result;
	}
	
	/**
	 * 根据定制主题ID新建空白方案。直接查询主题，使用主题title、description创建。并同步根据主题获取section建立空白记录
	 * @param schemeId
	 * @param params  {byOpenid:xxx, forOpenid:xxx} 均为可选
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/blank/{schemeId}", method = RequestMethod.POST)
	public JSONObject createBlankSolutionFromScheme(@PathVariable String schemeId, @RequestBody JSONObject params) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( schemeId==null || schemeId.trim().length() == 0) {
			result.put("msg", "schemeId is required.");
			return result;
		}
		//得到主题定义
		ProposalScheme scheme = proposalSchemeService.get(schemeId);
		if( scheme==null) {
			result.put("msg", "ProposalScheme not found.[id]"+schemeId);
			return result;
		}
		
		//先创建solution
		Solution solution = new Solution();
		String id = Util.get32UUID();
		solution.setId(id);
		solution.setIsNewRecord(true);
		solution.setScheme(scheme);
		solution.setName(scheme.getName());
		solution.setDescription(scheme.getDescription());
		solution.setStatus(1);//默认为启用
		if(params.getString("byOpenid")!=null) {
			solution.setByOpenid(params.getString("byOpenid"));
		}else {
			solution.setByOpenid(null);
		}
		if(params.getString("forOpenid")!=null) {
			solution.setForOpenid(params.getString("forOpenid"));
		}else {
			solution.setForOpenid(null);
		}
		solutionService.save(solution);
		
		//根据scheme下的section创建默认分隔条目
		ProposalSection proposalSection  = new ProposalSection();
		proposalSection.setScheme(scheme);
		List<ProposalSection> sections = proposalSectionService.findList(proposalSection);
		for(ProposalSection section:sections) {
			SolutionItem item = new SolutionItem();
			item.setSolution(solution);
			item.setName(section.getName());
			item.setPriority(section.getPriority());
			item.setDescription(section.getDescription());
			item.setType("section");//固定为section类型，表示是分隔符
			solutionItemService.save(item);
		}
		
		Solution nSolution = solutionService.get(id);
		SolutionItem nSolutionItem = new SolutionItem();
		nSolutionItem.setSolution(nSolution);
		
		result.put("success", true);
		result.put("solution", nSolution);//返回新建立的solution
		result.put("solutionItems", solutionItemService.findList(nSolutionItem));//返回新建立的solutionItem列表
		return result;
	}
	
	/**
	 * 修改solution后保存
	 * @param solution
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/solution", method = RequestMethod.POST)
	public JSONObject saveSolutionDetail(@RequestBody Solution solution) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( solution==null || solution.getId() == null || solution.getId().trim().length() == 0) {
			result.put("msg", "invalid solution. id cannot be empty.");
			return result;
		}
		//得到原有的方案及条目
		Solution old = solutionService.get(solution);
		if( old==null) {
			result.put("msg", "Solution not found.[id]"+solution.getId());
			return result;
		}
		solution.setUpdateDate(new Date());
		solutionService.save(solution);//直接保存就完事了
		
		result.put("success", true);
		result.put("data", solution);
		return result;
	}
	
	/**
	 * 新建或修改solutionItem后保存：带有id则直接保存，否则为新增
	 * @param solutionItem
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/solutionItem/{solutionId}", method = RequestMethod.POST)
	public JSONObject saveSolutionItemDetail(@PathVariable String solutionId, @RequestBody SolutionItem solutionItem) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if( solutionItem==null || solutionId == null || solutionId.trim().length() == 0) {
			result.put("msg", "Both solutionId and solution object are required.");
			return result;
		}
		
		//得到原有的方案及条目
		Solution solution = solutionService.get(solutionId);
		if( solution==null) {
			result.put("msg", "Solution not found.[id]"+solutionId);
			return result;
		}
		solutionItem.setSolution(solution);
		if(solutionItem.getId()==null || solutionItem.getId().trim().length() == 0) { //新建条目设置id
			solutionItem.setId(Util.get32UUID());
			solutionItem.setIsNewRecord(true);
			solutionItem.setCreateDate(new Date());
		}
		solutionItem.setUpdateDate(new Date());
		solutionItemService.save(solutionItem);//直接保存就完事了
		
		result.put("success", true);
		result.put("data", solutionItem);
		return result;
	}
	
	
}