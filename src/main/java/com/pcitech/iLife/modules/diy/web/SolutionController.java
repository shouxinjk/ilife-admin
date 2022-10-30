/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.web;

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
import com.pcitech.iLife.modules.diy.entity.ProposalSection;
import com.pcitech.iLife.modules.diy.entity.Solution;
import com.pcitech.iLife.modules.diy.service.SolutionService;

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
	
}