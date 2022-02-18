/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.entity.Channel;
import com.pcitech.iLife.modules.mod.entity.ChannelNeed;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.service.ChannelNeedService;
import com.pcitech.iLife.modules.mod.service.ChannelService;

/**
 * 频道管理Controller
 * @author ilife
 * @version 2022-02-17
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/channel")
public class ChannelController extends BaseController {

	@Autowired
	private ChannelService channelService;
	@Autowired
	private ChannelNeedService channelNeedService;
	
	@ModelAttribute
	public Channel get(@RequestParam(required=false) String id) {
		Channel entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = channelService.get(id);
		}
		if (entity == null){
			entity = new Channel();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:channel:view")
	@RequestMapping(value = {"list", ""})
	public String list(Channel channel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Channel> page = channelService.findPage(new Page<Channel>(request, response), channel); 
		model.addAttribute("page", page);
		return "modules/mod/channelList";
	}

	@RequiresPermissions("mod:channel:view")
	@RequestMapping(value = "form")
	public String form(Channel channel, Model model) {
		model.addAttribute("channel", channel);
		return "modules/mod/channelForm";
	}

	@RequiresPermissions("mod:channel:edit")
	@RequestMapping(value = "save")
	public String save(Channel channel, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, channel)){
			return form(channel, model);
		}
		channelService.save(channel);
		addMessage(redirectAttributes, "保存频道管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/channel/?repage";
	}
	
	@RequiresPermissions("mod:channel:edit")
	@RequestMapping(value = "delete")
	public String delete(Channel channel, RedirectAttributes redirectAttributes) {
		channelService.delete(channel);
		addMessage(redirectAttributes, "删除频道管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/channel/?repage";
	}
	
	//返回列表供选择
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Channel channel, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Channel> list =channelService.findList(channel);
		for (int i=0; i<list.size(); i++){
			Channel e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
			mapList.add(map);
			
		}
		return mapList;
	}
	
	//查询所有启用的频道：用于展示到前端
	@ResponseBody
	@RequestMapping(value = "rest/channels/{status}", method = RequestMethod.GET)
	public List<Channel> getChannelsByStatus(@PathVariable String status) {
		Channel channel = new Channel();
		channel.setStatus(status);
		return channelService.findListByStatus(channel);
	}
	
	//查询频道下的需要构成列表
	@ResponseBody
	@RequestMapping(value = "rest/needs/{channelId}", method = RequestMethod.GET)
	public List<ChannelNeed> getNeedsByChannelId(@PathVariable String channelId) {
		Channel channel = new Channel();
		channel.setId(channelId);
		ChannelNeed channelNeed = new ChannelNeed();
		channelNeed.setChannel(channel);
		channelNeed.setNeed(new Motivation());//必须设置
		return channelNeedService.findList(channelNeed);
	}

}