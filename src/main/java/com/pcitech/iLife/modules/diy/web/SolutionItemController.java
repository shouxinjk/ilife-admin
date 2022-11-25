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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.diy.entity.Solution;
import com.pcitech.iLife.modules.diy.entity.SolutionItem;
import com.pcitech.iLife.modules.diy.service.SolutionItemService;

/**
 * 个性定制方案条目Controller
 * @author chenci
 * @version 2022-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/solutionItem")
public class SolutionItemController extends BaseController {

	@Autowired
	private SolutionItemService solutionItemService;
	
	@ModelAttribute
	public SolutionItem get(@RequestParam(required=false) String id) {
		SolutionItem entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = solutionItemService.get(id);
		}
		if (entity == null){
			entity = new SolutionItem();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:solutionItem:view")
	@RequestMapping(value = {"list", ""})
	public String list(SolutionItem solutionItem, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SolutionItem> page = solutionItemService.findPage(new Page<SolutionItem>(request, response), solutionItem); 
		model.addAttribute("page", page);
		return "modules/diy/solutionItemList";
	}

	@RequiresPermissions("diy:solutionItem:view")
	@RequestMapping(value = "form")
	public String form(SolutionItem solutionItem, Model model) {
		model.addAttribute("solutionItem", solutionItem);
		return "modules/diy/solutionItemForm";
	}

	@RequiresPermissions("diy:solutionItem:edit")
	@RequestMapping(value = "save")
	public String save(SolutionItem solutionItem, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, solutionItem)){
			return form(solutionItem, model);
		}
		solutionItemService.save(solutionItem);
		addMessage(redirectAttributes, "保存个性定制方案条目成功");
		return "redirect:"+Global.getAdminPath()+"/diy/solutionItem/?repage";
	}
	
	@RequiresPermissions("diy:solutionItem:edit")
	@RequestMapping(value = "delete")
	public String delete(SolutionItem solutionItem, RedirectAttributes redirectAttributes) {
		solutionItemService.delete(solutionItem);
		addMessage(redirectAttributes, "删除个性定制方案条目成功");
		return "redirect:"+Global.getAdminPath()+"/diy/solutionItem/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/solution-items/{solutionId}", method = RequestMethod.GET)
	public List<SolutionItem> getItemsByBoard(@PathVariable String solutionId,HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Solution solution = new Solution();
		solution.setId(solutionId);
		
		SolutionItem solutionItem = new SolutionItem();
		solutionItem.setSolution(solution);
		
		return solutionItemService.findList(solutionItem);
	}

}