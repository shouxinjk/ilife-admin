/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.dict.web;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.dict.entity.DictBrand;
import com.pcitech.iLife.modules.dict.service.DictBrandService;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.util.Util;

import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 品牌字典管理Controller
 * @author iLife
 * @version 2021-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/dict/dictBrand")
public class DictBrandController extends BaseController {

	@Autowired
	private DictBrandService dictBrandService;
	@Autowired
	private MeasureService measureService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	
	@ModelAttribute
	public DictBrand get(@RequestParam(required=false) String id) {
		DictBrand entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dictBrandService.get(id);
		}
		if (entity == null){
			entity = new DictBrand();
		}
		return entity;
	}
	
	@RequiresPermissions("dict:dictBrand:view")
	@RequestMapping(value = {"list", ""})
	public String list(DictBrand dictBrand,String treeId,String pId,String treeModule,String topType,  HttpServletRequest request, HttpServletResponse response, Model model) {
		dictBrand.setCategory(new ItemCategory(treeId));//增加类目过滤。注意：仅在选中节点为Measure时，其pId是CategoryId
		Page<DictBrand> page = dictBrandService.findPage(new Page<DictBrand>(request, response), dictBrand); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);//记录当前选中的measureId
		model.addAttribute("pId", pId);//记录当前选中的categoryId
		model.addAttribute("pType", treeModule);
		return "modules/dict/dictBrandList";
	}

	@RequiresPermissions("dict:dictBrand:view")
	@RequestMapping(value = "form")
	public String form(DictBrand dictBrand,String treeId,String pId,String pType,  Model model) {
		model.addAttribute("treeId", treeId);//记录当前选中的measureId
		model.addAttribute("pId", pId);//记录当前选中的categoryId
		model.addAttribute("pType", pType);
		dictBrand.setCategory(itemCategoryService.get(treeId));//pId记录当前选中的categoryId
		model.addAttribute("dictBrand", dictBrand);
		return "modules/dict/dictBrandForm";
	}

	@RequiresPermissions("dict:dictBrand:edit")
	@RequestMapping(value = "save")
	public String save(DictBrand dictBrand,String treeId,String pId,String pType,  Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, dictBrand)){
			return form(dictBrand, treeId, pId, pType,  model);
		}
		if(dictBrand.getCategory() == null){//不知道为啥，前端传进来的measure信息丢失了，手动补一次
			dictBrand.setCategory(itemCategoryService.get(treeId));
		}
		dictBrandService.save(dictBrand);
		addMessage(redirectAttributes, "保存品牌字典成功");
		return "redirect:"+Global.getAdminPath()+"/dict/dictBrand/?treeId="+treeId+"&pId="+pId+"&treeModule="+pType+"&repage";
	}
	
	@RequiresPermissions("dict:dictBrand:edit")
	@RequestMapping(value = "delete")
	public String delete(DictBrand dictBrand,String treeId,String pId,String pType,  RedirectAttributes redirectAttributes) {
		dictBrandService.delete(dictBrand);
		addMessage(redirectAttributes, "删除品牌字典成功");
		return "redirect:"+Global.getAdminPath()+"/dict/dictBrand/?treeId="+treeId+"&pId="+pId+"&treeModule="+pType+"&repage";
	}
	

	@RequiresPermissions("dict:dictBrand:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","dict/dictBrand");
		model.addAttribute("title","所属类目");
		return "treeData/index";
	}
	
	@RequiresPermissions("dict:dictBrand:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","dict/dictBrand");
		model.addAttribute("title","所属类目");
		List<ItemCategory> itemCategoryTree = itemCategoryService.findTree();
		model.addAttribute("list", dictBrandService.getTree(itemCategoryTree));
		return "treeData/tree";
	}
	
	@RequiresPermissions("dict:dictBrand:view")
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
		List<ItemCategory> categories = itemCategoryService.findList(new ItemCategory());
		for (int i=0; i<categories.size(); i++){
			ItemCategory e = categories.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "rest/updateMarkedValue")
	//更新属性值标注：markedvalue。自动添加更新日期
	public Map<String,String> updateMarkedValue( @RequestParam(required=true) String id, 
			@RequestParam(required=true) double markedValue,
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("markedValue", markedValue);
		params.put("updateDate", new Date());
		dictBrandService.updateMarkedValue(params);
		
		Map<String,String> result = Maps.newHashMap();
		result.put("result", "marked value updated.");
		return result;
	}
	
	/**
	 * 添加品牌数据
	 */
	@ResponseBody
	@RequestMapping(value = "rest", method = RequestMethod.POST)
	public Map<String, Object> addNewBrand(/*@PathVariable String id,*/@RequestBody DictBrand brand, HttpServletRequest request, HttpServletResponse response, Model model) throws WxErrorException{
		Map<String, Object> result = Maps.newHashMap();
		result.put("status",false);
		result.put("description","Failed add brand");
		brand.setCreateDate(new Date());
		brand.setUpdateDate(new Date());
		if(brand.getId()==null)
			brand.setId(Util.md5(brand.getCategory()!=null?brand.getCategory().getId():""+brand.getLabel()));
		brand.setIsNewRecord(true);
		try {//直接作为新纪录存储，可能出现保存失败，直接忽略
			dictBrandService.save(brand);
		}catch(Exception ex) {
			result.put("msg", ex.getMessage());
		}
		result.put("status",true);
		result.put("description","Brand saved successfully");
		result.put("data", brand);
		return result;
	}

}