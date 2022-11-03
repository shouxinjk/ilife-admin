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
import com.pcitech.iLife.modules.mod.service.DictMetaService;
import com.pcitech.iLife.modules.mod.service.DictValueService;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.Util;

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
	@Autowired
	private ItemCategoryService itemCategoryService;
	
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
		if (!beanValidator(model, dictValue)){
			return form(dictValue, model);
		}
		
		//需要根据dictMeta是否类目相关进行处理：如果是类目无关则去掉category信息，否则保留category
		DictMeta dictMeta = dictMetaService.get(dictValue.getDictMeta());
		if("1".equals(dictMeta.getIgnoreCategory())) {
			dictValue.setCategory(null);//如果是类目无关则取消类目设置
		}
		
		//对于新建记录采用categoryId、dict_id、originavalue进行唯一识别
		if(dictValue.getId()==null || dictValue.getId().trim().length()==0) {
			
			StringBuffer sb = new StringBuffer();
			if(dictValue.getCategory()!=null&&dictValue.getCategory().getId()!=null&&dictValue.getCategory().getId().trim().length()>0)
				sb.append(dictValue.getCategory().getId());
			sb.append(dictValue.getDictMeta().getId());
			sb.append(dictValue.getOriginalValue());
			dictValue.setId(Util.md5(sb.toString()));
			dictValue.setIsNewRecord(true);
			
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
	
	/**
	 * 字典数据是否与类目相关联。
	 * @param id 字典值ID
	 * @param type 取消目录关联或启用目录关联，可选值：0-ignore、1-restore
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/category",method = RequestMethod.DELETE)
	//批量更新是否目录无关
	public Map<String,Object> updateCategoryInfo( @RequestParam(required=true) String id,  
			@RequestParam String type,  
			HttpServletResponse response) {
		Map<String,Object> result = Maps.newHashMap();
		response.setContentType("application/json; charset=UTF-8");
		result.put("success",false);
		DictValue dictValue = dictValueService.get(id);
		
		//根据类型处理DictMeta
		DictMeta dictMeta = dictMetaService.get(dictValue.getDictMeta());
		if(dictMeta.getIgnoreCategory().equalsIgnoreCase(type)) {
			//do nothing
			//如果dictMeta的类目无关设置与参数相同则忽略
		}else if("0".equalsIgnoreCase(type) || "1".equalsIgnoreCase(type)) {//否则更新dictMeta并修改
			dictMeta.setIgnoreCategory(type);
			dictMetaService.save(dictMeta);//同步修改dictMeta
			dictValue.setDictMeta(dictMeta);
		}else {
			//参数无法识别，直接忽略
		}
		
		batchUpdateCategoryInfo(dictValue.getDictMeta());
		
		result.put("success",true);
		result.put("result", "category info updated.");
		return result;
	}
	
	//批量修改是否目录相关
	private void batchUpdateCategoryInfo(DictMeta dictMeta) {
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
	

	/**
	 * 根据CategoryId、measureId从指定字典获取推荐值。用于自动提示。
	 * @param dictId 字典ID 必选
	 * @param query {
	 * 	categoryId : xxx, 可选
	 * 	measureId : xxx, 当前不需要。字典不针对单个属性提供
	 *  q: xxx 输入查询字符串，可选。为空匹配全部
	 *  size: xxx 可选。不填则返回全部
	 * }
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/search/{dictId}", method = RequestMethod.GET)
	public JSONObject searchValues(@PathVariable String dictId, @RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		
		DictValue query = new DictValue();
		
		DictMeta dictMeta = dictMetaService.get(dictId);//查询字典
		if( dictMeta == null || dictMeta.getId() == null || dictMeta.getId().trim().length()==0) {
			result.put("msg", "dictId is required.");
			return result;
		}
		query.setDictMeta(dictMeta);
		
		//检查属性信息
		/**
		Measure measure = measureService.get(json.getString("measureId"));
		if(measure == null) {
			result.put("msg", "measureId is required.");
			return result;
		}
		//**/
		
		//检查是否目录相关，如果目录相关则设置categoryId参数
		if("0".equals(dictMeta.getIgnoreCategory())) {//目录相关
			ItemCategory itemCategory = itemCategoryService.get(json.getString("categoryId"));
			if(itemCategory == null) {
				result.put("msg", "the Dict is category specified.  categoryId is required.");
				return result;
			}
			query.setCategory(itemCategory);
		}
		//设置搜索字符串
		if(json.getString("q")!=null && json.getString("q").trim().length()>0) {
			query.setOriginalValue(json.getString("q").trim());
		}
		//返回条数：注意数据库查询中直接得到全部，仅控制返回数量
		int size = -1;
		try {
			size = Integer.parseInt(json.getString("size"));
		}catch(Exception ex) {
			//do nothing
		}		
		//查询得到推荐值
		List<DictValue> dictValues = dictValueService.findList(query);
		List<String> suggestions = Lists.newArrayList();
		int count = 1;
		for(DictValue dictValue:dictValues) {
			if(size>0 && count>size)
				break;
			suggestions.add(dictValue.getOriginalValue());
			count++;
		}
		result.put("success", false);
		result.put("data", suggestions);
		return result;
	}
	
}