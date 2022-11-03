/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import com.pcitech.iLife.modules.mod.entity.DictMeta;
import com.pcitech.iLife.modules.mod.entity.DictValue;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.service.DictMetaService;
import com.pcitech.iLife.modules.mod.service.DictValueService;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.Util;

/**
 * 业务字典定义Controller
 * @author chenci
 * @version 2022-10-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/dictMeta")
public class DictMetaController extends BaseController {

	@Autowired
	private DictMetaService dictMetaService;
	@Autowired
	private DictService dictService;
	@Autowired
	private DictValueService dictValueService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private MeasureService measureService;
	
	@ModelAttribute
	public DictMeta get(@RequestParam(required=false) String id) {
		DictMeta entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dictMetaService.get(id);
		}
		if (entity == null){
			entity = new DictMeta();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:dictMeta:view")
	@RequestMapping(value = {"list", ""})
	public String list(DictMeta dictMeta, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<DictMeta> page = dictMetaService.findPage(new Page<DictMeta>(request, response), dictMeta); 
		model.addAttribute("page", page);
		return "modules/mod/dictMetaList";
	}

	@RequiresPermissions("mod:dictMeta:view")
	@RequestMapping(value = "form")
	public String form(DictMeta dictMeta, Model model) {
		model.addAttribute("dictMeta", dictMeta);
		return "modules/mod/dictMetaForm";
	}

	@RequiresPermissions("mod:dictMeta:edit")
	@RequestMapping(value = "save")
	public String save(DictMeta dictMeta, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, dictMeta)){
			return form(dictMeta, model);
		}
		dictMetaService.save(dictMeta);
		
		//对于更新需要根据是否目录相关处理数据
		if(dictMeta.getId()!=null && dictMeta.getId().trim().length()>0) {
			Map<String,Object> params = Maps.newHashMap();
			params.put("dictId", dictMeta.getId());
			if("0".equals(dictMeta.getIgnoreCategory())) {//目录相关
				//目录相关：则禁用所有无目录数据条目，并启用有目录数据条目
				//先禁用目录无关数据记录
				params.put("filter", "noCategory");
				params.put("delFlag", "1");
				dictValueService.batchUpdateCategoryInfo(params);
				//然后启用目录相关数据记录
				params.put("filter", "hasCategory");
				params.put("delFlag", "0");
				dictValueService.batchUpdateCategoryInfo(params);
			}else {
				//目录无关：禁用所有目录相关条目，并启用目录无关条目，同时需要根据唯一值补充目录无关条目
				//先禁用目录相关数据记录
				params.put("filter", "hasCategory");
				params.put("delFlag", "1");
				dictValueService.batchUpdateCategoryInfo(params);
				//然后启用目录无关数据记录
				params.put("filter", "noCategory");
				params.put("delFlag", "0");
				dictValueService.batchUpdateCategoryInfo(params);
				//加载补充目录无关数值
				List<Map<String,Object>> values = dictValueService.findDistinctValuesByDict(dictMeta.getId());
				for(Map<String,Object> value:values) {//逐条建立目录无关数值
					DictValue node = new DictValue();
					node.setOriginalValue(""+value.get("originalValue"));
					node.setDictMeta(dictMeta);
					node.setCategory(null);//无目录设置
					List<DictValue> nodes = dictValueService.getByCategoryCheck(node);
					if(nodes==null || nodes.size()==0) {//仅在没有数据时新建，否则不予处理
						node.setIsNewRecord(true);
						node.setId(Util.md5(dictMeta.getId()+value.get("originalValue")));
						try {node.setIsMarked(Integer.parseInt(""+value.get("isMarked")));}catch(Exception ex) {node.setIsMarked(0);}
						try {node.setMarkedValue(Double.parseDouble(""+value.get("markedValue")));}catch(Exception ex) {node.setMarkedValue(5.0);}
						try {node.setScore(Double.parseDouble(""+value.get("score")));}catch(Exception ex) {node.setScore(5.0);}
						try {dictValueService.save(node);}catch(Exception ex) {}
					}
				}
			}
		}
		
		addMessage(redirectAttributes, "保存业务字典定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/dictMeta/?repage";
	}
	
	@RequiresPermissions("mod:dictMeta:edit")
	@RequestMapping(value = "delete")
	public String delete(DictMeta dictMeta, RedirectAttributes redirectAttributes) {
		dictMetaService.delete(dictMeta);
		addMessage(redirectAttributes, "删除业务字典定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/dictMeta/?repage";
	}
	
}