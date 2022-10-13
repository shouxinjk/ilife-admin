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
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.TagCategory;
import com.pcitech.iLife.modules.mod.entity.Tags;
import com.pcitech.iLife.modules.mod.entity.UserCategory;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.entity.UserTag;
import com.pcitech.iLife.modules.mod.entity.UserTagCategory;
import com.pcitech.iLife.modules.mod.service.UserCategoryService;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;
import com.pcitech.iLife.modules.mod.service.UserTagCategoryService;
import com.pcitech.iLife.modules.mod.service.UserTagService;
import com.pcitech.iLife.util.Util;

/**
 * 用户属性定义Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userMeasure")
public class UserMeasureController extends BaseController {

	@Autowired
	private UserMeasureService userMeasureService;
	@Autowired
	private UserCategoryService userCategoryService;
	@Autowired
	private UserTagService userTagService;
	@Autowired
	private UserTagCategoryService userTagCategoryService;
	
	@ModelAttribute
	public UserMeasure get(@RequestParam(required=false) String id) {
		UserMeasure entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userMeasureService.get(id);
		}
		if (entity == null){
			entity = new UserMeasure();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserMeasure userMeasure,String treeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		userMeasure.setCategory(new UserCategory(treeId));
		Page<UserMeasure> page = userMeasureService.findPage(new Page<UserMeasure>(request, response), userMeasure); 
		model.addAttribute("page", page);
		model.addAttribute("treeId", treeId);
		return "modules/mod/userMeasureList";
	}

	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = "form")
	public String form(UserMeasure userMeasure, Model model) {
		if(userMeasure.getCategory()!=null&&StringUtils.isNoneBlank(userMeasure.getCategory().getId())){
			userMeasure.setCategory(userCategoryService.get(userMeasure.getCategory().getId()));
		}
		//检查默认key值，如果没有则随机设置
		if(userMeasure.getProperty()==null || userMeasure.getProperty().trim().length()==0)
			userMeasure.setProperty("p"+Util.get6bitCode("up"+userMeasure.getName()));//以p打头的7位字符串，大小写区分。保存时如果重复将报错
		
		model.addAttribute("userMeasure", userMeasure);
		return "modules/mod/userMeasureForm";
	}

	@RequiresPermissions("mod:userMeasure:edit")
	@RequestMapping(value = "save")
	public String save(UserMeasure userMeasure, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userMeasure)){
			return form(userMeasure, model);
		}
		userMeasureService.save(userMeasure);
		//将tag分别建立为主题
		if(userMeasure.getTags()!=null&&userMeasure.getTags().trim().length()>0) {
			saveTags(userMeasure);
		}
		addMessage(redirectAttributes, "保存用户属性定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userMeasure/?treeId="+userMeasure.getCategory().getId()+"&repage";
	}
	
	private void saveTags(UserMeasure measure) {
		List<UserMeasure> list =userMeasureService.findList(measure);//根据当前measure查询
		if(list==null || list.size()==0)
			return;
		UserMeasure target = list.get(0);
		String tags = target.getTags();
		if(tags == null || tags.trim().length()==0)
			return;
		
		//处理标签主题的默认分类节点
		UserTagCategory parent = new UserTagCategory();
		parent.setParent(new UserTagCategory("0"));//查找一级节点
		parent.setName("auto");//节点名称为auto
		List<UserTagCategory> parents = userTagCategoryService.findList(parent);
		if(parents!=null && parents.size()>0)
			parent = parents.get(0);//取一级节点的第一个作为目录
		else {//建立auto节点
			userTagCategoryService.save(parent);
			parents = userTagCategoryService.findList(parent);
			parent = parents.get(0);
		}
		
		//逐个建立标签主题
		String[] tagArray = tags.split("\\s+");
		for(String tag:tagArray) {
			UserTag item = new UserTag();
			item.setUserMeasure(measure);
			item.setName(tag);
			List<UserTag> exists = userTagService.findList(item);
			if(exists == null || exists.size()==0) {//仅在没有相同tag的时候才创建
				item.setType("auto");
				item.setUserTagCategory(parent);//放到第一个一级节点下，或者直接挂到根目录下
				userTagService.save(item);
			}
		}
	}
	
	@RequiresPermissions("mod:userMeasure:edit")
	@RequestMapping(value = "delete")
	public String delete(UserMeasure userMeasure, RedirectAttributes redirectAttributes) {
		userMeasureService.delete(userMeasure);
		addMessage(redirectAttributes, "删除用户属性定义成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userMeasure/?treeId="+userMeasure.getCategory().getId()+"&repage";
	}

	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(UserMeasure measure, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<UserMeasure> list =userMeasureService.findList(measure);
		for (int i=0; i<list.size(); i++){
			UserMeasure e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
//			if (type != null && "3".equals(type)){
//				map.put("isParent", true);
//			}
			mapList.add(map);
			
		}
		return mapList;
	}

	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("url","mod/userMeasure");
		model.addAttribute("title","用户属性");
		return "treeData/index";
	}
	
	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","mod/userMeasure");
		model.addAttribute("title","属性类型");
		List<UserCategory> userCategoryTree = userCategoryService.findTree();
		model.addAttribute("list", userCategoryTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("mod:userMeasure:view")
	@RequestMapping(value = "none")
	public String none(HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("message","请在左侧选择一个类型。");
		//默认直接查询所有记录
		UserMeasure measure = new UserMeasure();
		Page<UserMeasure> page = userMeasureService.findPage(new Page<UserMeasure>(request, response), measure);
		model.addAttribute("page", page);
//		return "treeData/none";
		return "modules/mod/userMeasureList";
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
		List<UserCategory> categories = userCategoryService.findList(new UserCategory());
		for (int i=0; i<categories.size(); i++){
			UserCategory e = categories.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
				//查询该类别下的用户属性
				UserMeasure query = new UserMeasure();
				query.setCategory(e);
				List<UserMeasure> props = userMeasureService.findList(query);
				for(UserMeasure prop:props) {
					Map<String, Object> leafNode = Maps.newHashMap();
					leafNode.put("id", prop.getId());
					leafNode.put("pId", e.getId());
					leafNode.put("name", prop.getName());
					mapList.add(leafNode);
				}
			}
		}
		return mapList;
	}
	
}