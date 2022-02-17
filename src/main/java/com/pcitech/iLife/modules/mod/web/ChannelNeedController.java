/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Channel;
import com.pcitech.iLife.modules.mod.entity.ChannelNeed;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.service.ChannelNeedService;
import com.pcitech.iLife.modules.mod.service.ChannelService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.ope.entity.Person;

/**
 * 频道需要构成Controller
 * @author ilife
 * @version 2022-02-17
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/channelNeed")
public class ChannelNeedController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(ChannelNeedController.class);
	@Autowired
	private ChannelNeedService channelNeedService;
	@Autowired
	private MotivationService motivationService;
	@Autowired
	private ChannelService channelService;
	
	@ModelAttribute
	public ChannelNeed get(@RequestParam(required=false) String id) {
		ChannelNeed entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = channelNeedService.get(id);
		}
		if (entity == null){
			entity = new ChannelNeed();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:channelNeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(ChannelNeed channelNeed, HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.error("try to list channel needs.[channelNeed]"+channelNeed);
		if(channelNeed.getChannel()==null) {
			channelNeed.setChannel(new Channel());
		}
		if(channelNeed.getNeed()==null) {
			channelNeed.setNeed(new Motivation());
		}
		Page<ChannelNeed> page = channelNeedService.findPage(new Page<ChannelNeed>(request, response), channelNeed); 
		model.addAttribute("page", page);
		return "modules/mod/channelNeedList";
	}

	@RequiresPermissions("mod:channelNeed:view")
	@RequestMapping(value = "form")
	public String form(ChannelNeed channelNeed, Model model) {
		logger.error("try to init channel need form.[channelNeed]"+channelNeed);
		//预加载channel
		if(channelNeed.getChannel()==null) {
			channelNeed.setChannel(new Channel());
		}else if(channelNeed.getChannel().getId()!=null) {//从前端仅传递了id，则重新加载
			Channel p = channelService.get(channelNeed.getChannel().getId());
			if(p!=null) {
				channelNeed.setChannel(p);
			}else {
				channelNeed.setChannel(new Channel());
			}
		}else {
			channelNeed.setChannel(new Channel());
		}
		//预加载need
		if(channelNeed.getNeed()==null) {
			channelNeed.setNeed(new Motivation());
		}else if(channelNeed.getNeed().getId()!=null) {//从前端仅传递了id，则重新加载
			Motivation need = motivationService.get(channelNeed.getNeed().getId());
			if(need!=null) {
				channelNeed.setNeed(need);
			}else {
				channelNeed.setNeed(new Motivation());
			}
		}else {
			channelNeed.setNeed(new Motivation());
		}
		//预设值新建条目排序值
		if(channelNeed.getId()==null)
			channelNeed.setSort("10");//设置默认排序
		model.addAttribute("channelNeed", channelNeed);
		return "modules/mod/channelNeedForm";
	}

	@RequiresPermissions("mod:channelNeed:edit")
	@RequestMapping(value = "save")
	public String save(ChannelNeed channelNeed, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, channelNeed)){
			return form(channelNeed, model);
		}
		channelNeedService.save(channelNeed);
		addMessage(redirectAttributes, "保存频道需要构成成功");
		return "redirect:"+Global.getAdminPath()+"/mod/channelNeed/?channel.id="+channelNeed.getChannel().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:channelNeed:edit")
	@RequestMapping(value = "delete")
	public String delete(ChannelNeed channelNeed, RedirectAttributes redirectAttributes) {
		channelNeedService.delete(channelNeed);
		addMessage(redirectAttributes, "删除频道需要构成成功");
		return "redirect:"+Global.getAdminPath()+"/mod/channelNeed/?channel.id="+channelNeed.getChannel().getId()+"&repage";
	}

}