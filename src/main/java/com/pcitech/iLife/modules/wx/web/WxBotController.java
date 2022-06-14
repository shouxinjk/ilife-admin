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
	@RequestMapping(value = "rest/byBrokerId/{brokerId}", method = RequestMethod.POST)
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


	/**
	 * 机器人启动时信息同步。
	 * 同步内容主要为botId及qrcode
	 * 对于重新启动或退出后重新登录的情况，包含有oldBotId，直接根据oldBotId更新即可。更新内容包括botId、qrcodeUrl。并发送通知到指定达人通知重新登录
	 * 对于新建机器人，启动后将只有botId，默认将优先自动分配给botId为空的记录；如果没有botId为空的记录则新建一个机器人记录
	 * 
	 * 参数:
	 * botId：必须。是当前启动bot实例ID。更新到到wechatyid 
	 * oldBotId：可选。有则更新原有记录，否则新建
	 * status: 可选。状态
	 * qrcodeUrl：可选。二维码链接
	 */
	@ResponseBody
	@RequestMapping(value = "rest/sync", method = RequestMethod.POST)
	public Map<String,Object> syncBotInfo( @RequestBody JSONObject json) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		
		//参数检查，必须有botId
		if(json.getString("botId")==null || json.getString("botId").trim().length()==0) {
			result.put("msg", "bot id cannot be empty.");
			return result;
		}

		//先检查是否有oldBotId
		if(json.getString("oldBotId")!=null && json.getString("oldBotId").trim().length()>0) {//表示是一个已有bot重启或重新登录
			logger.debug("try to update existed bot.[oldBotId]"+json.getString("oldBotId"));
			WxBot wxBot = new WxBot();
			wxBot.setWechatyId(json.getString("oldBotId"));
			List<WxBot> wxBots = wxBotService.findList(wxBot);
			if(wxBots == null || wxBots.size()==0) {
				logger.debug("cannot find bot by wechatyId:"+json.getString("oldBotId"));
				//后续按照新bot处理。
			}else {
				wxBot = wxBots.get(0);
				wxBot.setWechatyId(json.getString("botId"));
				if(json.getString("qrcodeUrl")!=null)
					wxBot.setQrcodeUrl(json.getString("qrcodeUrl"));
				if(json.getString("status")!=null)
					wxBot.setStatus(json.getString("status"));
				wxBot.setUpdateDate(new Date());
				wxBotService.save(wxBot);
				//TODO 需要发送通知给对应达人，扫码登录
				result.put("success", true);
				result.put("msg", "update botId on existed bot record. done.");
				return result;
			}
		}
		
		//其次更新当前botId 更新：此情况仅出现在bot重启，但能够自动登录时
		WxBot wxBot = new WxBot();
		wxBot.setWechatyId(json.getString("botId"));
		List<WxBot> wxBots = wxBotService.findList(wxBot);
		if(wxBots == null || wxBots.size()==0) {
			logger.debug("cannot find bot by wechatyId:"+json.getString("botId"));
			//后续按照新bot处理。
		}else {
			wxBot = wxBots.get(0);
			//wxBot.setWechatyId(json.getString("botId"));
			if(json.getString("qrcodeUrl")!=null)
				wxBot.setQrcodeUrl(json.getString("qrcodeUrl"));
			if(json.getString("status")!=null)
				wxBot.setStatus(json.getString("status"));
			if(json.getDate("heartBeat")!=null)
				wxBot.setHeartBeat(json.getDate("heartBeat"));
			wxBot.setUpdateDate(new Date());
			wxBotService.save(wxBot);
			result.put("success", true);
			result.put("msg", "update existed bot done.");
			return result;
		}
		
		//然后查看是否有botId为空的记录
		wxBot = wxBotService.getPendingBot();
		if(wxBot!=null) {
			wxBot.setWechatyId(json.getString("botId"));
			if(json.getString("qrcodeUrl")!=null)
				wxBot.setQrcodeUrl(json.getString("qrcodeUrl"));
			if(json.getString("status")!=null)
				wxBot.setStatus(json.getString("status"));
			wxBot.setUpdateDate(new Date());
			wxBotService.save(wxBot);
			//TODO 需要发送通知给对应达人，扫码登录
			result.put("success", true);
			result.put("msg", "assign bot to pending request done.");
			return result;
		}
		
		//最后新建一个bot记录。达人分配给system
		wxBot = new WxBot();
		Broker broker = brokerService.get("system");//默认分配给系统达人
		String botId = Util.get32UUID();
		wxBot.setBroker(broker);
		wxBot.setId(botId);
		wxBot.setIsNewRecord(true);
		wxBot.setName("待分配BOT");
		wxBot.setWechatyId(json.getString("botId"));
		if(json.getString("qrcodeUrl")!=null)
			wxBot.setQrcodeUrl(json.getString("qrcodeUrl"));
		if(json.getString("status")!=null)
			wxBot.setStatus(json.getString("status"));
		wxBot.setType(json.getString("type")!=null?json.getString("type"):"web");//默认设为web
		wxBot.setEffectFrom(json.getDate("effectFrom")!=null?json.getDate("effectFrom"):new Date());
		wxBot.setExpireOn(json.getDate("expireOn")!=null?json.getDate("expireOn"):null);
		wxBot.setCreateDate(new Date());
		wxBot.setUpdateDate(new Date());
		wxBotService.save(wxBot);
		result.put("success", true);
		result.put("data", wxBotService.get(botId));
		result.put("msg", "new bot created successfully.");
		return result;
	}

	/**
	 * 更新机器人心跳
	 * 
	 * 参数:
	 * botId：必须。是当前启动bot实例ID
	 * heartBeat：必须。是心跳检测时间
	 */
	@ResponseBody
	@RequestMapping(value = "rest/heartbeat", method = RequestMethod.POST)
	public Map<String,Object> syncHeartBeat( @RequestBody JSONObject json) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		
		//参数检查，必须有botId
		if(json.getString("botId")==null || json.getString("botId").trim().length()==0) {
			result.put("msg", "bot id cannot be empty.");
			return result;
		}

		//其次更新当前botId 更新：此情况仅出现在bot重启，但能够自动登录时
		WxBot wxBot = new WxBot();
		wxBot.setWechatyId(json.getString("botId"));
		List<WxBot> wxBots = wxBotService.findList(wxBot);
		if(wxBots == null || wxBots.size()==0) {
			logger.debug("cannot find bot by wechatyId:"+json.getString("botId"));
			result.put("msg", "cannot find bot by id."+json.getString("botId"));
			return result;
		}else {
			wxBot = wxBots.get(0);
			if(json.getString("status")!=null)
				wxBot.setStatus(json.getString("status"));
			if(json.getDate("heartBeat")!=null)
				wxBot.setHeartBeat(json.getDate("heartBeat"));
			else
				wxBot.setHeartBeat(new Date());
			wxBot.setUpdateDate(new Date());
			wxBotService.save(wxBot);
			result.put("success", true);
			result.put("msg", "heart beat updated.");
			return result;
		}
	}
}