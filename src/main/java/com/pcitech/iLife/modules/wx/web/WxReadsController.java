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
import org.springframework.web.bind.annotation.PathVariable;
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
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.wx.entity.WxReads;
import com.pcitech.iLife.modules.wx.entity.WxTopping;
import com.pcitech.iLife.modules.wx.service.WxReadsService;

/**
 * 阅读记录Controller
 * @author ilife
 * @version 2022-03-31
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxReads")
public class WxReadsController extends BaseController {

	@Autowired
	private WxReadsService wxReadsService;
	@Autowired
	private BrokerService brokerService;
	
	@ModelAttribute
	public WxReads get(@RequestParam(required=false) String id) {
		WxReads entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxReadsService.get(id);
		}
		if (entity == null){
			entity = new WxReads();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxReads:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxReads wxReads, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxReads> page = wxReadsService.findPage(new Page<WxReads>(request, response), wxReads); 
		model.addAttribute("page", page);
		return "modules/wx/wxReadsList";
	}

	@RequiresPermissions("wx:wxReads:view")
	@RequestMapping(value = "form")
	public String form(WxReads wxReads, Model model) {
		model.addAttribute("wxReads", wxReads);
		return "modules/wx/wxReadsForm";
	}

	@RequiresPermissions("wx:wxReads:edit")
	@RequestMapping(value = "save")
	public String save(WxReads wxReads, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxReads)){
			return form(wxReads, model);
		}
		wxReadsService.save(wxReads);
		addMessage(redirectAttributes, "保存阅读记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxReads/?repage";
	}
	
	@RequiresPermissions("wx:wxReads:edit")
	@RequestMapping(value = "delete")
	public String delete(WxReads wxReads, RedirectAttributes redirectAttributes) {
		wxReadsService.delete(wxReads);
		addMessage(redirectAttributes, "删除阅读记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxReads/?repage";
	}


	/**
	 * 按照倒序获取时间阅读列表。参数包括 readerBrokerId,readerOpenid,articleId。其中readerBrokerId,readerOpenid可以只传递其中一个
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/latest", method = RequestMethod.GET)
	public List<WxReads> listReadingList( @RequestParam String readerBrokerId,@RequestParam String readerOpenid,@RequestParam String articleId) {
		Map<String,Object> params = Maps.newHashMap();
		//获取broker info
		if(readerBrokerId!=null && readerBrokerId.trim().length()>0) {
			params.put("brokerId", readerBrokerId.trim());
		}else if(readerOpenid!=null && readerOpenid.trim().length()>0){
			Broker broker = null;
			broker = brokerService.getByOpenid(readerOpenid);
			if(broker!=null)
				params.put("brokerId", broker.getId());
			else
				params.put("brokerOpenid", readerOpenid);
		}else {
			//do nothing
		}
		
		if(articleId!=null && articleId.trim().length()>0) {
			params.put("articleId", articleId.trim());
		}

		return wxReadsService.findReadingList(params);
	}
	

	/**
	 * 获取班车互阅列表。参数：code
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/grouping/{code}", method = RequestMethod.GET)
	public List<WxReads> getGroupingList( @PathVariable String code) {
		Map<String,Object> params = Maps.newHashMap();
		
		if(code!=null && code.trim().length()>0) {
			params.put("grouping", code.trim());
		}else {//如果没有code则返回空白列表
			return Lists.newArrayList();
		}

		return wxReadsService.findGroupingList(params);
	}
}