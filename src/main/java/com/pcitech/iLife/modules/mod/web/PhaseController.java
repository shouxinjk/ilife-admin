/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.PhaseNeed;
import com.pcitech.iLife.modules.mod.service.PhaseService;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 人生阶段Controller
 * @author chenci
 * @version 2017-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/phase")
@ApiIgnore
public class PhaseController extends BaseController {

	@Autowired
	private PhaseService phaseService;
	
	@ModelAttribute
	public Phase get(@RequestParam(required=false) String id) {
		Phase entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = phaseService.get(id);
		}
		if (entity == null){
			entity = new Phase();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:phase:view")
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Phase> list = Lists.newArrayList();
		List<Phase> sourcelist = phaseService.findTree();
		Phase.sortList(list, sourcelist, "1",true);
        model.addAttribute("list", list);
		return "modules/mod/phaseList";
	}

	@RequiresPermissions("mod:phase:view")
	@RequestMapping(value = "form")
	public String form(Phase phase, Model model) {
		if (phase.getParent()==null||phase.getParent().getId()==null){
			phase.setParent(new Phase("1"));
		}
		Phase parent = phaseService.get(phase.getParent().getId());
		phase.setParent(parent);
		// 获取排序号，最末节点排序号+30
		if (StringUtils.isBlank(phase.getId())){
			List<Phase> list = Lists.newArrayList();
			List<Phase> sourcelist = phaseService.findTree();
			Phase.sortList(list, sourcelist, phase.getParentId(), false);
			if (list.size() > 0){
				phase.setSort(list.get(list.size()-1).getSort() + 30);
			}
		}
		model.addAttribute("phase", phase);
		return "modules/mod/phaseForm";
	}

	@RequiresPermissions("mod:phase:edit")
	@RequestMapping(value = "save")
	public String save(Phase phase, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, phase)){
			return form(phase, model);
		}
		phaseService.save(phase);
		addMessage(redirectAttributes, "保存人生阶段成功");
		return "redirect:"+Global.getAdminPath()+"/mod/phase/?repage";
	}
	
	@RequiresPermissions("mod:phase:edit")
	@RequestMapping(value = "delete")
	public String delete(Phase phase, RedirectAttributes redirectAttributes) {
		phaseService.delete(phase);
		addMessage(redirectAttributes, "删除人生阶段成功");
		return "redirect:"+Global.getAdminPath()+"/mod/phase/?repage";
	}

	/**
	 * 批量修改排序
	 */
	@RequiresPermissions("mod:phase:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
    	int len = ids.length;
    	Phase[] entitys = new Phase[len];
    	for (int i = 0; i < len; i++) {
    		entitys[i] = phaseService.get(ids[i]);
    		entitys[i].setSort(sorts[i]);
    		phaseService.save(entitys[i]);
    	}
    	addMessage(redirectAttributes, "保存排序成功!");
		return "redirect:" + adminPath + "/mod/phase/";
	}
	
	//查询阶段列表，按照优先级排序
	@ResponseBody
	@RequestMapping(value = "rest/phases", method = RequestMethod.GET)
	public List<Phase> listPhases() {
		Phase phase = new Phase();
		return phaseService.findList(phase);
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