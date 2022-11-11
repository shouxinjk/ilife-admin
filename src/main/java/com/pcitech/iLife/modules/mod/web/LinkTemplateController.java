/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.LinkTemplate;
import com.pcitech.iLife.modules.mod.service.LinkTemplateService;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * 链接模板Controller
 * @author qchzhu
 * @version 2021-09-01
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/linkTemplate")
public class LinkTemplateController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(LinkTemplateController.class);
	@Autowired
	private LinkTemplateService linkTemplateService;
	
	@ModelAttribute
	public LinkTemplate get(@RequestParam(required=false) String id) {
		LinkTemplate entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = linkTemplateService.get(id);
		}
		if (entity == null){
			entity = new LinkTemplate();
		}
		return entity;
	}
	
	/**
	 * 输入URL，转换为标准URL格式。处理逻辑:
	 * 获取所有连接规则，使用condition逐个匹配，如果符合则执行  expression 得到结果URL返回
	 * 
	 * 注意：必须用post 方法。避免链接中带有参数导致缺失
	 * @param url 输入URL
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/convert", method = RequestMethod.POST)
	public Map<String,Object> convertUrl( @RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);//默认认为处理都失败了，我们比较谦虚的哈

		String url = json.getString("url");
		result.put("sUrl", url);//原始链接放回去
		if(url==null || url.trim().length()==0) {
			result.put("msg", "url is required.");
			return result;
		}
		logger.debug("try to covert url.[url]"+url);
		
		//获取所有待处理规则
		List<LinkTemplate> templates = linkTemplateService.findByPriority();
		Binding binding = new Binding();
		binding.setVariable("url", url);
		GroovyShell shell = new GroovyShell(binding);
		for(LinkTemplate template:templates) {//没啥技巧，就一个个比对呗
			logger.debug("try to match url.[rule]"+template.getCondition());
        	String  script = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(template.getCondition()); //注意：必须转义，双引号等无法引用。
	        Object value = shell.evaluate(script);//返回：boolean，即ture/false
	        logger.debug("match result.[result]"+value);
	        if(Boolean.valueOf(""+value)) {//如果命中了，则处理URL
	        	script = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(template.getExpression()); //注意：必须转义，双引号等无法引用。
	        	value = shell.evaluate(script);//返回：string，已经完成组装为目标URL了
	        	result.put("success", true);
	        	result.put("url", ""+value);
	        	break;//搞定了就不继续了，否则好傻啊
	        }else {
	        	logger.debug("does not match.continue.[result]"+value);
	        }
		}
		return result;
	}
	
	@RequiresPermissions("mod:linkTemplate:view")
	@RequestMapping(value = {"list", ""})
	public String list(LinkTemplate linkTemplate, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<LinkTemplate> page = linkTemplateService.findPage(new Page<LinkTemplate>(request, response), linkTemplate); 
		model.addAttribute("page", page);
		return "modules/mod/linkTemplateList";
	}

	@RequiresPermissions("mod:linkTemplate:view")
	@RequestMapping(value = "form")
	public String form(LinkTemplate linkTemplate, Model model) {
		model.addAttribute("linkTemplate", linkTemplate);
		return "modules/mod/linkTemplateForm";
	}

	@RequiresPermissions("mod:linkTemplate:edit")
	@RequestMapping(value = "save")
	public String save(LinkTemplate linkTemplate, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, linkTemplate)){
			return form(linkTemplate, model);
		}
		linkTemplateService.save(linkTemplate);
		addMessage(redirectAttributes, "保存链接模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/linkTemplate/?repage";
	}
	
	@RequiresPermissions("mod:linkTemplate:edit")
	@RequestMapping(value = "delete")
	public String delete(LinkTemplate linkTemplate, RedirectAttributes redirectAttributes) {
		linkTemplateService.delete(linkTemplate);
		addMessage(redirectAttributes, "删除链接模板成功");
		return "redirect:"+Global.getAdminPath()+"/mod/linkTemplate/?repage";
	}

}