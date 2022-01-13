/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import com.pcitech.iLife.modules.mod.entity.UserTagCategory;
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.entity.UserTag;
import com.pcitech.iLife.modules.mod.service.UserTagCategoryService;
import com.pcitech.iLife.modules.mod.service.UserTagService;

/**
 * 标签Controller
 * @author chenci
 * @version 2017-09-27
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userTag")
public class UserTagController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(UserTagController.class);
	@Autowired
	private UserTagService userTagService;
	@Autowired
	private UserTagCategoryService userTagCategoryService;
	
	@ModelAttribute
	public UserTag get(@RequestParam(required=false) String id) {
		UserTag entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userTagService.get(id);
		}
		if (entity == null){
			entity = new UserTag();
		}
		return entity;
	}
	
	//获取用户标签供标注
	//@param types: 逗号分隔的标签类型。不传递则查询所有标签
	@ResponseBody
	@RequestMapping(value = "rest/tags", method = RequestMethod.GET)
	public List<UserTag> listUserTagsBySubject( HttpServletRequest request,@RequestParam(required=false) String types, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<UserTag> tagList = userTagService.findListBySubject(types);
		return tagList;
	}
	
	//选中标签后自动设置属性
	//废弃：直接从前端访问ArangoDB完成
	/**
	 * 
	 * @deprecated
	 * @param json
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/tag", method = RequestMethod.PUT)
	public Map<String,Object> changeTag( @RequestBody Map<String,Object> json,  HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String,Object> result = Maps.newHashMap();
		logger.debug("checking parameters.",json);
		if(json.get("type") == null || json.get("tagId")==null|| json.get("id") == null) {
			result.put("success", false);
			result.put("msg", "Wrong parameters. parameters must include type(user or persona), tagId, id(id of user/persona).");
			return result;
		}
		UserTag tag = userTagService.get(json.get("tagId").toString());
		if(tag == null) {
			logger.debug("cannot find tag by id.",json);
			result.put("success", false);
			result.put("msg", "Cannot find tag by id.[id]"+json.get("tagId"));
		}else {//执行Groovy脚本设置属性值
			if("persona".equalsIgnoreCase(json.get("type").toString())) {
				String aql = "FOR doc in persona_personas filter doc._key='"+json.get("id")+"' update doc with {"+tag.getExpression()+"} in persona_personas return NEW";
				logger.debug("try to update persona by AQL.",aql);
			}else if("user".equalsIgnoreCase(json.get("type").toString())) {
				
			}else {//传的是个啥，重新传参
				
			}
		}
		return result;
	}	
	
	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserTag userTag,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		userTag.setUserTagCategory(new UserTagCategory(treeId));
		Page<UserTag> page = userTagService.findPage(new Page<UserTag>(request, response), userTag); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/userTagList";
	}

	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = "form")
	public String form(UserTag userTag,Model model) {
		if(userTag.getUserTagCategory()!=null&&StringUtils.isNoneBlank(userTag.getUserTagCategory().getId())){
			userTag.setUserTagCategory(userTagCategoryService.get(userTag.getUserTagCategory().getId()));
		}
		model.addAttribute("userTag", userTag);
		return "modules/mod/userTagForm";
	}

	@RequiresPermissions("mod:userTag:edit")
	@RequestMapping(value = "save")
	public String save(UserTag userTag, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userTag)){
			return form(userTag, model);
		}
		userTagService.save(userTag);
		addMessage(redirectAttributes, "保存标签成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userTag/?treeId="+userTag.getUserTagCategory().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:userTag:edit")
	@RequestMapping(value = "delete")
	public String delete(UserTag userTag, String treeId,RedirectAttributes redirectAttributes) {
		userTagService.delete(userTag);
		addMessage(redirectAttributes, "删除标签成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userTag/?treeId="+treeId+"&repage";
	}

	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/userTag");
		model.addAttribute("title","标签");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/userTag");
		model.addAttribute("title","分类");
		model.addAttribute("list", userTagCategoryService.findTree());
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:userTag:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个分类。");
		return "treeData/none";
	}
}