/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.util.Date;
import java.util.Map;

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
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.entity.WxGrouping;
import com.pcitech.iLife.modules.wx.service.WxGroupingService;

/**
 * 互助班车Controller
 * @author ilife
 * @version 2022-04-22
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxGrouping")
public class WxGroupingController extends BaseController {

	@Autowired
	private WxGroupingService wxGroupingService;
	
	@ModelAttribute
	public WxGrouping get(@RequestParam(required=false) String id) {
		WxGrouping entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxGroupingService.get(id);
		}
		if (entity == null){
			entity = new WxGrouping();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxGrouping:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxGrouping wxGrouping, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxGrouping> page = wxGroupingService.findPage(new Page<WxGrouping>(request, response), wxGrouping); 
		model.addAttribute("page", page);
		return "modules/wx/wxGroupingList";
	}

	@RequiresPermissions("wx:wxGrouping:view")
	@RequestMapping(value = "form")
	public String form(WxGrouping wxGrouping, Model model) {
		model.addAttribute("wxGrouping", wxGrouping);
		return "modules/wx/wxGroupingForm";
	}

	@RequiresPermissions("wx:wxGrouping:edit")
	@RequestMapping(value = "save")
	public String save(WxGrouping wxGrouping, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxGrouping)){
			return form(wxGrouping, model);
		}
		wxGroupingService.save(wxGrouping);
		addMessage(redirectAttributes, "保存互助班车成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGrouping/?repage";
	}
	
	@RequiresPermissions("wx:wxGrouping:edit")
	@RequestMapping(value = "delete")
	public String delete(WxGrouping wxGrouping, RedirectAttributes redirectAttributes) {
		wxGroupingService.delete(wxGrouping);
		addMessage(redirectAttributes, "删除互助班车成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGrouping/?repage";
	}
	
	/**
	 * 添加文章或公众号到班车
	 * 参数：
	 * code：班车号
	 * subjectType：主题类型
	 * subjectId：主题ID
	 * timeFrom: 开始时间，是long型，需要转换为Date
	 * timeTo：截止时间，是long型，需要转换为Date
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/grouping", method = RequestMethod.POST)
	public Map<String, Object> addArticleToGrouping(@RequestBody JSONObject json){
		logger.debug("try to insert grouping.[data]"+json);
		Map<String, Object> result = Maps.newHashMap();
		WxGrouping wxGrouping = new WxGrouping();
		wxGrouping.setCode(json.getString("code"));
		wxGrouping.setSubjectType(json.getString("subjectType"));
		wxGrouping.setSubjectId(json.getString("subjectId"));
		wxGrouping.setCreateDate(new Date());
		wxGrouping.setUpdateDate(new Date());
		try {
			Date from = new Date(json.getLong("timeFrom"));
			wxGrouping.setEventTimeFrom(from);
			Date date = new Date(json.getLong("timeFrom"));
			wxGrouping.setEventDate(date);
		}catch(Exception ex) {
			//do nothing
			logger.debug("failed parse time from.",ex);
		}
		try {
			Date to = new Date(json.getLong("timeTo"));
			wxGrouping.setEventTimeTo(to);
		}catch(Exception ex) {
			//do nothing
			logger.debug("failed parse time from.",ex);
		}
		wxGrouping.setEventTimeFrom(null);
		wxGroupingService.save(wxGrouping);
		result.put("data", wxGrouping);
		result.put("success", true);
		return result;
	}

}