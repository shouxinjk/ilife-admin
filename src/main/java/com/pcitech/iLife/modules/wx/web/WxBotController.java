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
import com.pcitech.iLife.modules.wx.service.WxBotService;
import com.pcitech.iLife.util.Util;

/**
 * 微信机器人Controller
 * @author ilife
 * @version 2022-06-07
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxBot")
public class WxBotController extends BaseController {

	@Autowired
	private WxBotService wxBotService;
	@Autowired
	private BrokerService brokerService;
	
	@ModelAttribute
	public WxBot get(@RequestParam(required=false) String id) {
		WxBot entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxBotService.get(id);
		}
		if (entity == null){
			entity = new WxBot();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxBot:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxBot wxBot, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxBot> page = wxBotService.findPage(new Page<WxBot>(request, response), wxBot); 
		model.addAttribute("page", page);
		return "modules/wx/wxBotList";
	}

	@RequiresPermissions("wx:wxBot:view")
	@RequestMapping(value = "form")
	public String form(WxBot wxBot, Model model) {
		model.addAttribute("wxBot", wxBot);
		return "modules/wx/wxBotForm";
	}

	@RequiresPermissions("wx:wxBot:edit")
	@RequestMapping(value = "save")
	public String save(WxBot wxBot, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxBot)){
			return form(wxBot, model);
		}
		wxBotService.save(wxBot);
		addMessage(redirectAttributes, "保存微信机器人成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxBot/?repage";
	}
	
	@RequiresPermissions("wx:wxBot:edit")
	@RequestMapping(value = "delete")
	public String delete(WxBot wxBot, RedirectAttributes redirectAttributes) {
		wxBotService.delete(wxBot);
		addMessage(redirectAttributes, "删除微信机器人成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxBot/?repage";
	}
	
	/**
	 * 根据brokerId获取指定达人已开通机器人
	 */
	@ResponseBody
	@RequestMapping(value = "rest/byBrokerId/{brokerId}", method = RequestMethod.GET)
	public Map<String,Object> getBotByBrokerId(@PathVariable String brokerId) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		Broker broker = brokerService.get(brokerId);
		WxBot wxBot = new WxBot();
		if(broker==null) {//如果为空则返回空白
			result.put("msg", "cannot find broker by id:"+brokerId);
			return result;
		}
		wxBot.setBroker(broker);
		List<WxBot> wxBots = wxBotService.findList(wxBot);
		if(wxBots.size()>0) {
			result.put("success", true);
			result.put("data", wxBots.get(0));
		}else {
			result.put("msg", "no bot found by broker id:"+brokerId);
		}
		return result;
	}
	
	/**
	 * 根据brokerId请求开通机器人
	 */
	@ResponseBody
	@RequestMapping(value = "rest/botByBrokerId/{brokerId}", method = RequestMethod.POST)
	public Map<String,Object> createBotByBrokerId(@PathVariable String brokerId, @RequestBody JSONObject json) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		Broker broker = brokerService.get(brokerId);
		WxBot wxBot = new WxBot();
		if(broker==null) {//如果为空则返回空白
			result.put("msg", "cannot find broker by id:"+brokerId);
			return result;
		}
		wxBot.setBroker(broker);
		List<WxBot> wxBots = wxBotService.findList(wxBot);
		if(wxBots.size()>0) {//如果有就直接返回
			result.put("success", true);
			result.put("data", wxBots.get(0));
			result.put("msg", "got existed bot.");
		}else {//否则新建一个
			//在broker上生成激活码
			String code = Util.get6bitCode(brokerId);//根据达人ID生成
			broker.setToken(code);
			brokerService.save(broker);
			//新建bot
			String botId = Util.get32UUID();
			wxBot.setId(botId);
			wxBot.setIsNewRecord(true);
			wxBot.setStatus("inactive");
			wxBot.setName("BOT-"+broker.getNickname());
			wxBot.setType(json.getString("type")!=null?json.getString("type"):"web");//默认设为web
			wxBot.setEffectFrom(json.getDate("effectFrom")!=null?json.getDate("effectFrom"):new Date());
			wxBot.setExpireOn(json.getDate("expireOn")!=null?json.getDate("expireOn"):null);
			wxBot.setCreateDate(new Date());
			wxBot.setUpdateDate(new Date());
			wxBotService.save(wxBot);
			result.put("success", true);
			result.put("data", wxBotService.get(botId));
			result.put("msg", "new bot created successfully.");
		}
		return result;
	}

}