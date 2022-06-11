/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.wx.entity.WxGroupTask;
import com.pcitech.iLife.modules.wx.service.WxGroupTaskService;

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
	 */
	@ResponseBody
	@RequestMapping(value = "rest/byNickname", method = RequestMethod.GET)
	public List<WxGroupTask> getBrokerByNickname(@RequestParam(required=true) String nickname) {
		 return wxGroupTaskService.getByNickname(nickname);//根据nickname获取达人
	}

}