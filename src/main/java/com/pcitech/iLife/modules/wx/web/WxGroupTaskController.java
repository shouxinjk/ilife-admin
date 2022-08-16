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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.wx.entity.WxBot;
import com.pcitech.iLife.modules.wx.entity.WxGroupTask;
import com.pcitech.iLife.modules.wx.service.WxGroupTaskService;
import com.pcitech.iLife.util.Util;

/**
 * 微信群任务Controller
 * @author ilife
 * @version 2022-06-11
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxGroupTask")
public class WxGroupTaskController extends BaseController {

	@Autowired
	private WxGroupTaskService wxGroupTaskService;
	@Autowired
	private BrokerService brokerService;
	
	@ModelAttribute
	public WxGroupTask get(@RequestParam(required=false) String id) {
		WxGroupTask entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxGroupTaskService.get(id);
		}
		if (entity == null){
			entity = new WxGroupTask();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxGroupTask:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxGroupTask wxGroupTask, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxGroupTask> page = wxGroupTaskService.findPage(new Page<WxGroupTask>(request, response), wxGroupTask); 
		model.addAttribute("page", page);
		return "modules/wx/wxGroupTaskList";
	}

	@RequiresPermissions("wx:wxGroupTask:view")
	@RequestMapping(value = "form")
	public String form(WxGroupTask wxGroupTask, Model model) {
		model.addAttribute("wxGroupTask", wxGroupTask);
		return "modules/wx/wxGroupTaskForm";
	}

	@RequiresPermissions("wx:wxGroupTask:edit")
	@RequestMapping(value = "save")
	public String save(WxGroupTask wxGroupTask, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxGroupTask)){
			return form(wxGroupTask, model);
		}
		wxGroupTaskService.save(wxGroupTask);
		addMessage(redirectAttributes, "保存微信群任务成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGroupTask/?repage";
	}
	
	@RequiresPermissions("wx:wxGroupTask:edit")
	@RequestMapping(value = "delete")
	public String delete(WxGroupTask wxGroupTask, RedirectAttributes redirectAttributes) {
		wxGroupTaskService.delete(wxGroupTask);
		addMessage(redirectAttributes, "删除微信群任务成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGroupTask/?repage";
	}
	
	/**
	 * 根据nickname获取指定达人微信群任务
	 * 仅返回active任务
	 */
	@ResponseBody
	@RequestMapping(value = "rest/byNickname", method = RequestMethod.GET)
	public List<WxGroupTask> getBrokerByNickname(@RequestParam(required=true) String nickname) {
		 return wxGroupTaskService.getByNickname(nickname);//根据nickname获取达人
	}
	
	/**
	 * 根据达人ID获取所有微信群任务，包括active及inactive
	 */
	@ResponseBody
	@RequestMapping(value = "rest/byBrokerId/{brokerId}", method = RequestMethod.GET)
	public List<WxGroupTask> listBrokerGroupTasks(@PathVariable String brokerId) {
		Broker broker = brokerService.get(brokerId);
		if(broker==null) {//如果为空则返回空白列表
			return Lists.newArrayList();
		}
		WxGroupTask wxGroupTask = new WxGroupTask();
		wxGroupTask.setBroker(broker);
		wxGroupTask.setStatus("active");
		return wxGroupTaskService.findList(wxGroupTask);//根据brokerId获取列表
	}

	/**
	 * 获取指定ID微信群任务
	 */
	@ResponseBody
	@RequestMapping(value = "rest/byId/{id}", method = RequestMethod.GET)
	public Map<String,Object> getGroupTask(@PathVariable String id) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		WxGroupTask wxGroupTask = wxGroupTaskService.get(id);
		if(wxGroupTask==null) {
			result.put("msg", "cannot find group task by id."+id);
			return result;
		}
		result.put("data", wxGroupTask);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 更新微信群任务
	 */
	@ResponseBody
	@RequestMapping(value = "rest/byId/{id}", method = RequestMethod.POST)
	public Map<String,Object> updateGroupTask(@PathVariable String id, @RequestBody WxGroupTask data) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		if(wxGroupTaskService.get(id)==null) {
			result.put("msg", "cannot find group task by id."+id);
			return result;
		}
		data.setId(id);//优先采用path指定的id
		data.setUpdateDate(new Date());
		wxGroupTaskService.save(data);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 更新微信群任务cron
	 * 参数：
	 * id：群任务ID
	 * cron：群任务cron表达式
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/cron/{id}", method = RequestMethod.POST)
	public Map<String,Object> updateCron(@PathVariable String id, @RequestBody JSONObject data) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		
		//根据id查询groupTask
		WxGroupTask wxGroupTask = wxGroupTaskService.get(id);
		if(wxGroupTask==null) {
			result.put("msg", "cannot find group task by id."+id);
			return result;
		}
		wxGroupTask.setCron(data.getString("cron"));
		wxGroupTask.setCronDesc(data.getString("cronDesc"));
		wxGroupTask.setUpdateDate(new Date());
		wxGroupTaskService.save(wxGroupTask);
		result.put("success", true);
		result.put("data", wxGroupTask);
		return result;
	}
}