/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.util.Date;
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
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.wx.entity.WxGroup;
import com.pcitech.iLife.modules.wx.entity.WxGroupTask;
import com.pcitech.iLife.modules.wx.service.WxGroupService;
import com.pcitech.iLife.modules.wx.service.WxGroupTaskService;
import com.pcitech.iLife.util.Util;

/**
 * 微信群Controller
 * @author ilife
 * @version 2022-06-07
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxGroup")
public class WxGroupController extends BaseController {

	@Autowired
	private WxGroupService wxGroupService;
	@Autowired
	private WxGroupTaskService wxGroupTaskService;
	@Autowired
	private BrokerService brokerService;
	
	@ModelAttribute
	public WxGroup get(@RequestParam(required=false) String id) {
		WxGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxGroupService.get(id);
		}
		if (entity == null){
			entity = new WxGroup();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxGroup:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxGroup wxGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxGroup> page = wxGroupService.findPage(new Page<WxGroup>(request, response), wxGroup); 
		model.addAttribute("page", page);
		return "modules/wx/wxGroupList";
	}

	@RequiresPermissions("wx:wxGroup:view")
	@RequestMapping(value = "form")
	public String form(WxGroup wxGroup, Model model) {
		model.addAttribute("wxGroup", wxGroup);
		return "modules/wx/wxGroupForm";
	}

	@RequiresPermissions("wx:wxGroup:edit")
	@RequestMapping(value = "save")
	public String save(WxGroup wxGroup, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxGroup)){
			return form(wxGroup, model);
		}
		wxGroupService.save(wxGroup);
		addMessage(redirectAttributes, "保存微信群成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGroup/?repage";
	}
	
	@RequiresPermissions("wx:wxGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(WxGroup wxGroup, RedirectAttributes redirectAttributes) {
		wxGroupService.delete(wxGroup);
		addMessage(redirectAttributes, "删除微信群成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGroup/?repage";
	}
	
	/**
	 * 同步微信群。
	 * 如果已经存在，则不做处理。
	 * 如果不存在则建立群，并且建立默认群任务
	 * 参数：
	 * gname: 群名称
	 * gid: 群ID
	 * token: 激活码
	 */
	@ResponseBody
	@RequestMapping(value = "rest/sync", method = RequestMethod.POST)
	public Map<String,Object> updateGroupTask(@RequestBody JSONObject data) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		WxGroup wxGroup = new WxGroup();
		wxGroup.setGid(data.getString("gid"));
		if(wxGroupService.findList(wxGroup).size()>0) {
			result.put("msg", "group exists. skipped.");
			result.put("success", true);
			return result;
		}
		//新建群
		Broker broker = brokerService.get(data.getString("brokerId"));
		String id = Util.md5(data.getString("gid"));
		wxGroup.setId(id);
		wxGroup.setIsNewRecord(true);
		wxGroup.setBroker(broker);
		wxGroup.setName(data.getString("gname"));
		wxGroup.setMembers(data.getInteger("members"));
		wxGroup.setToken(data.getString("token"));
		wxGroup.setStatus("active");
		wxGroup.setCreateDate(new Date());
		wxGroup.setUpdateDate(new Date());
		wxGroupService.save(wxGroup);
		
		//新建群任务
		WxGroupTask wxGroupTask = new WxGroupTask();
		wxGroupTask.setBroker(broker);
		wxGroupTask.setWxgroup(wxGroup);
		wxGroupTask.setType("sendItem");
		wxGroupTask.setTags("*");
		wxGroupTask.setStatus("active");
		wxGroupTask.setCron("0 */15 * * * ?");
		wxGroupTask.setName("默认任务");
		wxGroupTask.setCreateDate(new Date());
		wxGroupTask.setUpdateDate(new Date());
		wxGroupTaskService.save(wxGroupTask);
		
		result.put("msg", "group and group task created.");
		result.put("success", true);
		return result;
	}

	/**
	 * 修改微信名称
	 * 参数：
	 * old 原有名称
	 * new 修改后名称
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/changeTopic", method = RequestMethod.POST)
	public Map<String,Object> changeGroupName(@RequestBody JSONObject data) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		WxGroup wxGroup = wxGroupService.findGroupByName(data.getString("old"));
		if(wxGroup == null) {
			result.put("msg", "group doesnot exist.");
		}else {
			wxGroup.setName(data.getString("new"));
			wxGroupService.save(wxGroup);
			result.put("msg", "group name updated.");
			result.put("success", true);
		}
		return result;
	}

	/**
	 * 根据brokerId加载微信群
	 * 如果broker为空，则加载所有活跃状态微信群
	 */
	@ResponseBody
	@RequestMapping(value = "rest/listByBrokerId", method = RequestMethod.GET)
	public List<WxGroup> listGroupByBrokerId(@RequestParam String brokerId) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		WxGroup wxGroup = new WxGroup();
		wxGroup.setStatus("active");//仅支持活跃微信群
		if(brokerId!=null&&brokerId.trim().length()>0) {
			Broker broker = brokerService.get(brokerId);
			wxGroup.setBroker(broker);
		}
		//return wxGroupService.findList(wxGroup);
		return wxGroupService.findFeaturedGroup(wxGroup);
	}
	
}