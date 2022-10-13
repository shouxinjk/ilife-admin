/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.pcitech.iLife.modules.mod.service.DictMetaService;
import com.pcitech.iLife.modules.mod.service.DictValueService;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;

/**
 * 业务字典Controller
 * @author chenci
 * @version 2022-10-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/dictValue")
public class DictValueController extends BaseController {

	@Autowired
	private DictValueService dictValueService;
	@Autowired
	private DictMetaService dictMetaService;
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public DictValue get(@RequestParam(required=false) String id) {
		DictValue entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dictValueService.get(id);
		}
		if (entity == null){
			entity = new DictValue();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:dictValue:view")
	@RequestMapping(value = {"list"})
	public String list(DictValue dictValue,String treeId,String pId, HttpServletRequest request, HttpServletResponse response, Model model) {
		//如果指定字典类型则根据类型过滤，否则直接查询所有待标注记录
		if("user".equals(pId) || "item".equals(pId)){
			dictValue.setDictMeta(new DictMeta(treeId));
		}
		dictValue.setIsMarked(1);//显示待标注记录
		Page<DictValue> page = dictValueService.findPage(new Page<DictValue>(request, response), dictValue); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);//记录当前选中的dictMetaId
		model.addAttribute("pId", pId);
		return "modules/mod/dictValueList";
	}
	
	@RequiresPermissions("mod:dictValue:view")
	@RequestMapping(value = {"listPending", ""})
	public String listPending(DictValue dictValue,String treeId,String pId, HttpServletRequest request, HttpServletResponse response, Model model) {
		//如果指定字典类型则根据类型过滤，否则直接查询所有待标注记录
		if("user".equals(pId) || "item".equals(pId)){
			dictValue.setDictMeta(new DictMeta(treeId));
		}
		dictValue.setIsMarked(0);//显示待标注记录
		Page<DictValue> page = dictValueService.findPage(new Page<DictValue>(request, response), dictValue); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);//记录当前选中的dictMetaId
		model.addAttribute("pId", pId);
		return "modules/mod/dictValueListPending";
	}

	@RequiresPermissions("mod:dictValue:view")
	@RequestMapping(value = "form")
	public String form(DictValue dictValue, Model model) {
		model.addAttribute("dictValue", dictValue);
		return "modules/mod/dictValueForm";
	}

	@RequiresPermissions("mod:dictValue:edit")
	@RequestMapping(value = "save")
	public String save(DictValue dictValue, Model model, RedirectAttributes redirectAttributes) {
		//冗余设置dictKey。便于分析系统
		DictMeta dictMeta = dictMetaService.get(dictValue.getDictMeta());
		if(dictMeta!=null) {
			dictValue.setDictKey(dictMeta.getDictKey());
			dictValue.setDictMeta(dictMeta);
		}
		if (!beanValidator(model, dictValue)){
			return form(dictValue, model);
		}
		dictValueService.save(dictValue);
		addMessage(redirectAttributes, "保存业务字典成功");
		return "redirect:"+Global.getAdminPath()+"/mod/dictValue/?repage";
	}
	
	@RequiresPermissions("mod:dictValue:edit")
	@RequestMapping(value = "delete")
	public String delete(DictValue dictValue, RedirectAttributes redirectAttributes) {
		dictValueService.delete(dictValue);
		addMessage(redirectAttributes, "删除业务字典成功");
		return "redirect:"+Global.getAdminPath()+"/mod/dictValue/?repage";
	}

	@ResponseBody
	@RequestMapping(value = "rest/updateMarkedValue")
	//更新属性值标注，包括level、markedvalue。自动添加更新日期
	public Map<String,Object> updateMarkedValue( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double markedValue, 
			@RequestParam(required=true) int level, 
			HttpServletResponse response) {
		Map<String,Object> result = Maps.newHashMap();
		response.setContentType("application/json; charset=UTF-8");
		result.put("success",false);
		
		DictValue dictValue = dictValueService.get(id);
		dictValue.setIsMarked(1);
		dictValue.setMarkedValue(markedValue);
		dictValue.setUpdateDate(new Date());
		try {
			dictValueService.save(dictValue);
		}catch(Exception ex) {
			result.put("msg", "error occured while update item.[msg]"+ex.getMessage());
		}
		
		result.put("success",true);
		
		result.put("result", "marked value updated.");
		return result;
	}
	
	//组织显示左侧字典定义树，包含user字典及item字典
	private List<JSONObject> getDictTree(){
		//列表
		List<JSONObject> list = Lists.newArrayList();
		
		//准备根节点
		JSONObject root = new JSONObject();
		root.put("id", "0");
		root.put("name", "业务字典");
		root.put("parent", null);
		root.put("module", "");
		root.put("businessId", "0");
		list.add(root);
		//查询所有业务字典，按照类型分别组织
		Dict dict = new Dict();
		dict.setType("dict_type");//查找所有字典type
		List<Dict> dictTypes = dictService.findList(dict);
		for(Dict dictType:dictTypes) {
			JSONObject node = new JSONObject();
			node.put("id", dictType.getValue());
			node.put("module", "dictType");
			node.put("businessId", dictType.getValue());//仅用于前端参数传递
			node.put("name", dictType.getLabel());
			node.put("parent", root);
			list.add(node);
			//查询具体字典定义
			DictMeta dictMeta = new DictMeta();
			dictMeta.setType(dictType.getValue());
			List<DictMeta> dictMetas = dictMetaService.findList(dictMeta);
			for(DictMeta item:dictMetas) {
				JSONObject itemNode = new JSONObject();
				itemNode.put("id", item.getId());
				node.put("module", "dictMeta");
				itemNode.put("businessId", item.getId());//仅用于前端参数传递
				itemNode.put("name", item.getName());
				itemNode.put("parent", node);
				list.add(itemNode);
			}
		}
		return list;
	}

	@RequiresPermissions("mod:dictValue:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/dictValue");
		model.addAttribute("title","字典类型");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:dictValue:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/dictValue");
		model.addAttribute("title","字典类型");
		List<JSONObject> dictTree = getDictTree();
		model.addAttribute("list", dictTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:dictValue:view")
	@RequestMapping(value = "none")
	public String none(HttpServletRequest request, HttpServletResponse response, Model model) {
		//默认直接查询所有待标注记录
		DictValue dictValue = new DictValue();
		dictValue.setIsMarked(0);//显示待标注记录
		Page<DictValue> page = dictValueService.findPage(new Page<DictValue>(request, response), dictValue);
		model.addAttribute("page", page);
		return "modules/mod/dictValueListPending";
	}
	
	
}