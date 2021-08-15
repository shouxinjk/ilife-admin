/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import org.springframework.web.util.HtmlUtils; 

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.ViewTemplate;
import com.pcitech.iLife.modules.mod.service.ViewTemplateService;

/**
 * 模型展示模板Controller
 * @author qchzhu
 * @version 2021-08-15
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/viewTemplate")
public class ViewTemplateController extends BaseController {

	@Autowired
	private ViewTemplateService viewTemplateService;
	
	/**
	 * 根据类型获取相应的展示模板
	 * @return 模板定义字符串
	 */
	@ResponseBody
	@RequestMapping(value = "rest/type/{type}", method = RequestMethod.GET)
	public Map<String, Object> getViewTemplateByType(@PathVariable String type,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		ViewTemplate template = viewTemplateService.getByType(type);
		result.put("success",template==null?false:true);
		if(template !=null) {
			String expr = template.getExpression();
			String exprUnescape = expr;
			//unescape
			try {
				exprUnescape = HtmlUtils.htmlUnescape(expr);//free自由格式就做一下HTML Unescape就可以了
			}catch(Exception ex) {
				result.put("success",false);
				result.put("msg", "failed to unescape html. [msg]"+ex.getMessage());
			}
			result.put("data",exprUnescape);
			//parse json
			if("json".equalsIgnoreCase(template.getSubType())) {//JSON
				try {
					result.put("data", JSONObject.parse(exprUnescape));
				}catch(Exception ex) {
					result.put("success",false);
					result.put("msg", "failed to parse json. [msg]"+ex.getMessage());
				}
			}else if("xml".equalsIgnoreCase(template.getSubType()) ||
					"html".equalsIgnoreCase(template.getSubType())) {//xml or html
				result.put("data", exprUnescape.replaceAll("\\s+", ""));
			}else {//free style 
				//do nothing
			}
		}else {
			result.put("msg","Cannot find results by type:"+type);
		}
		return result;
	}
	
	@ModelAttribute
	public ViewTemplate get(@RequestParam(required=false) String id) {
		ViewTemplate entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = viewTemplateService.get(id);
		}
		if (entity == null){
			entity = new ViewTemplate();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:viewTemplate:view")
	@RequestMapping(value = {"list", ""})
	public String list(ViewTemplate viewTemplate, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ViewTemplate> page = viewTemplateService.findPage(new Page<ViewTemplate>(request, response), viewTemplate); 
		model.addAttribute("page", page);
		return "modules/mod/viewTemplateList";
	}

	@RequiresPermissions("mod:viewTemplate:view")
	@RequestMapping(value = "form")
	public String form(ViewTemplate viewTemplate, Model model) {
		model.addAttribute("viewTemplate", viewTemplate);
		return "modules/mod/viewTemplateForm";
	}

	@RequiresPermissions("mod:viewTemplate:edit")
	@RequestMapping(value = "save")
	public String save(ViewTemplate viewTemplate, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, viewTemplate)){
			return form(viewTemplate, model);
		}
		viewTemplateService.save(viewTemplate);
		addMessage(redirectAttributes, "保存模型展示模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/viewTemplate/?repage";
	}
	
	@RequiresPermissions("mod:viewTemplate:edit")
	@RequestMapping(value = "delete")
	public String delete(ViewTemplate viewTemplate, RedirectAttributes redirectAttributes) {
		viewTemplateService.delete(viewTemplate);
		addMessage(redirectAttributes, "删除模型展示模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/viewTemplate/?repage";
	}

}