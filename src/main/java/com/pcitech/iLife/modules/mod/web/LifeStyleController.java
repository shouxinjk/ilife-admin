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
import com.pcitech.iLife.modules.mod.entity.LifeStyle;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.LifeStyleService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.OccasionService;

/**
 * VALS模型Controller
 * @author chenci
 * @version 2017-09-22
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/lifeStyle")
public class LifeStyleController extends BaseController {

	@Autowired
	private LifeStyleService lifeStyleService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private MotivationService motivationService;
	@Autowired
	private OccasionService occasionService;
	
	@ModelAttribute
	public LifeStyle get(@RequestParam(required=false) String id) {
		LifeStyle entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = lifeStyleService.get(id);
		}
		if (entity == null){
			entity = new LifeStyle();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:lifeStyle:view")
	@RequestMapping(value = {"list", ""})
	public String list(LifeStyle lifeStyle, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<LifeStyle> list = Lists.newArrayList();
		List<LifeStyle> sourcelist = lifeStyleService.findTree();
		LifeStyle.sortList(list, sourcelist, "1",true);
		for(LifeStyle lifeStyle2:list){
			lifeStyle2.setMotivationNames(motivationService.getMotivationNames(lifeStyle2.getMotivationIds()));
			lifeStyle2.setOccasionNames(occasionService.getOccasionNames(lifeStyle2.getOccasionIds()));
			lifeStyle2.setItemCategoryNames(itemCategoryService.getItemCategoryNames(lifeStyle2.getItemCategoryIds()));
		}
        model.addAttribute("list", list);
		return "modules/mod/lifeStyleList";
	}

	@RequiresPermissions("mod:lifeStyle:view")
	@RequestMapping(value = "form")
	public String form(LifeStyle lifeStyle, Model model) {
		if (lifeStyle.getParent()==null||lifeStyle.getParent().getId()==null){
			lifeStyle.setParent(new LifeStyle("1"));
		}
		LifeStyle parent = lifeStyleService.get(lifeStyle.getParent().getId());
		lifeStyle.setParent(parent);
		lifeStyle.setMotivationNames(motivationService.getMotivationNames(lifeStyle.getMotivationIds()));
		lifeStyle.setOccasionNames(occasionService.getOccasionNames(lifeStyle.getOccasionIds()));
		lifeStyle.setItemCategoryNames(itemCategoryService.getItemCategoryNames(lifeStyle.getItemCategoryIds()));
		// 获取排序号，最末节点排序号+30
		if (StringUtils.isBlank(lifeStyle.getId())){
			List<LifeStyle> list = Lists.newArrayList();
			List<LifeStyle> sourcelist = lifeStyleService.findTree();
			LifeStyle.sortList(list, sourcelist, lifeStyle.getParentId(), false);
			if (list.size() > 0){
				lifeStyle.setSort(list.get(list.size()-1).getSort() + 30);
			}
		}
		model.addAttribute("lifeStyle", lifeStyle);
		return "modules/mod/lifeStyleForm";
	}

	@RequiresPermissions("mod:lifeStyle:edit")
	@RequestMapping(value = "save")
	public String save(LifeStyle lifeStyle, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, lifeStyle)){
			return form(lifeStyle, model);
		}
		lifeStyleService.save(lifeStyle);
		addMessage(redirectAttributes, "保存VALS模型成功");
		return "redirect:"+Global.getAdminPath()+"/mod/lifeStyle/?repage";
	}
	
	@RequiresPermissions("mod:lifeStyle:edit")
	@RequestMapping(value = "delete")
	public String delete(LifeStyle lifeStyle, RedirectAttributes redirectAttributes) {
		lifeStyleService.delete(lifeStyle);
		addMessage(redirectAttributes, "删除VALS模型成功");
		return "redirect:"+Global.getAdminPath()+"/mod/lifeStyle/?repage";
	}

	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<LifeStyle> list = lifeStyleService.findTree();
		for (int i=0; i<list.size(); i++){
			LifeStyle e = list.get(i);
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
	
	/**
	 * 批量修改排序
	 */
	@RequiresPermissions("mod:itemCategory:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
    	int len = ids.length;
    	LifeStyle[] entitys = new LifeStyle[len];
    	for (int i = 0; i < len; i++) {
    		entitys[i] = lifeStyleService.get(ids[i]);
    		entitys[i].setSort(sorts[i]);
    		lifeStyleService.save(entitys[i]);
    	}
    	addMessage(redirectAttributes, "保存排序成功!");
		return "redirect:" + adminPath + "/mod/lifeStyle/";
	}
}