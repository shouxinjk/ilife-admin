/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.DictMeta;
import com.pcitech.iLife.modules.mod.entity.DictValue;
import com.pcitech.iLife.modules.mod.service.DictMetaService;
import com.pcitech.iLife.modules.mod.service.DictValueService;
import com.pcitech.iLife.modules.ope.entity.HumanMarkedDict;
import com.pcitech.iLife.modules.ope.service.HumanMarkedDictService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.Util;

/**
 * 用户标注字典Controller
 * @author ilife
 * @version 2022-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/humanMarkedDict")
public class HumanMarkedDictController extends BaseController {

	@Autowired
	private HumanMarkedDictService humanMarkedDictService;
	@Autowired
	private DictValueService dictValueService;
	@Autowired
	private DictService dictService;
	@Autowired
	private DictMetaService dictMetaService;
	
	@ResponseBody
	@RequestMapping(value = "rest/score", method = RequestMethod.POST)
	//增加或修改标注记录，要包含measure,category,score,openid,nickname信息
	public JSONObject updateScore( @RequestBody HumanMarkedDict markedDict) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(markedDict.getDictMeta()==null || markedDict.getDictValue()==null) {
			result.put("msg", "dictMeta and dictValue are required.");
			return result;
		}
		if(markedDict.getId()==null || markedDict.getId().trim().length()==0) { //如果是新的则直接设置ID
			String idHash = markedDict.getDictMeta().getId()+
					markedDict.getCategory()==null?"":markedDict.getCategory().getId()+
					markedDict.getDictValue().getId()+
					markedDict.getOpenid();
			String id = Util.md5(idHash);//构建唯一ID
			markedDict.setId(id);
			markedDict.setIsNewRecord(true);//新纪录，直接创建
		}
		//保存标注值
		try {
			humanMarkedDictService.save(markedDict);
		}catch(Exception ex) {
			result.put("msg", ex.getMessage());
			return result;
		}
		//汇总标注值更新到dictValue
		DictValue dictValue = dictValueService.get(markedDict.getDictValue());
		if(dictValue==null) {
			result.put("msg", "cannot find dictValue by id."+markedDict.getDictValue().getId());
			return result;
		}
		//查询得到对应performance的所有标注记录，查询前清空openid、nickname、originalvalue等记录
		markedDict.setOpenid("");
		markedDict.setNickname("");
		markedDict.setOriginalValue("");
		List<HumanMarkedDict> markedDicts = humanMarkedDictService.findList(markedDict);
		
		//根据阈值控制是否直接修改属性值
		int threshold = 10;//默认10人以上打分才开始计算
		Dict dict = new Dict();
		dict.setType("threshold");
		dict.setLabel("enableHumanMarkValue");
		List<Dict> dicts = dictService.findList(dict);
		if(dicts.size()>0) {
			try {
				threshold = Integer.parseInt(dicts.get(0).getValue());
			}catch(Exception ex) {
				//do nothing
			}
		}
		
		if(markedDicts.size()>0) {
			double sum = 0;
			for(HumanMarkedDict value:markedDicts)sum+=value.getScore();
			dictValue.setMarkers(markedDicts.size());
			dictValue.setMarkedValue(sum/markedDicts.size());
			dictValueService.save(dictValue);
			result.put("msg", "dictValue updated.");
		}
		result.put("success", true);
		return result;
	}
	
	
	@ModelAttribute
	public HumanMarkedDict get(@RequestParam(required=false) String id) {
		HumanMarkedDict entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = humanMarkedDictService.get(id);
		}
		if (entity == null){
			entity = new HumanMarkedDict();
		}
		return entity;
	}
	
	@RequiresPermissions("ope:humanMarkedDict:view")
	@RequestMapping(value = {"list", ""})
	public String list(HumanMarkedDict humanMarkedDict, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<HumanMarkedDict> page = humanMarkedDictService.findPage(new Page<HumanMarkedDict>(request, response), humanMarkedDict); 
		model.addAttribute("page", page);
		return "modules/ope/humanMarkedDictList";
	}

	@RequiresPermissions("ope:humanMarkedDict:view")
	@RequestMapping(value = "form")
	public String form(HumanMarkedDict humanMarkedDict, Model model) {
		model.addAttribute("humanMarkedDict", humanMarkedDict);
		return "modules/ope/humanMarkedDictForm";
	}

	@RequiresPermissions("ope:humanMarkedDict:edit")
	@RequestMapping(value = "save")
	public String save(HumanMarkedDict humanMarkedDict, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, humanMarkedDict)){
			return form(humanMarkedDict, model);
		}
		humanMarkedDictService.save(humanMarkedDict);
		addMessage(redirectAttributes, "保存用户标注字典成功");
		return "redirect:"+Global.getAdminPath()+"/ope/humanMarkedDict/?repage";
	}
	
	@RequiresPermissions("ope:humanMarkedDict:edit")
	@RequestMapping(value = "delete")
	public String delete(HumanMarkedDict humanMarkedDict, RedirectAttributes redirectAttributes) {
		humanMarkedDictService.delete(humanMarkedDict);
		addMessage(redirectAttributes, "删除用户标注字典成功");
		return "redirect:"+Global.getAdminPath()+"/ope/humanMarkedDict/?repage";
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

	@RequiresPermissions("ope:humanMarkedDict:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","ope/humanMarkedDict");
		model.addAttribute("title","字典类型");
		return "treeData/index";
	}
	
	@RequiresPermissions("ope:humanMarkedDict:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","ope/humanMarkedDict");
		model.addAttribute("title","字典类型");
		List<JSONObject> dictTree = getDictTree();
		model.addAttribute("list", dictTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("ope:humanMarkedDict:view")
	@RequestMapping(value = "none")
	public String none(HttpServletRequest request, HttpServletResponse response, Model model) {
		//默认直接查询所有标注值
		HumanMarkedDict humanMarkedDict = new HumanMarkedDict();
		Page<HumanMarkedDict> page = humanMarkedDictService.findPage(new Page<HumanMarkedDict>(request, response), humanMarkedDict);
		model.addAttribute("page", page);
		return "modules/ope/humanMarkedDictList";
	}

}