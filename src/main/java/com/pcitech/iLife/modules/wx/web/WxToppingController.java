/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.pcitech.iLife.modules.wx.entity.WxAdvertise;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.entity.WxTopping;
import com.pcitech.iLife.modules.wx.service.WxAdvertiseService;
import com.pcitech.iLife.modules.wx.service.WxArticleService;
import com.pcitech.iLife.modules.wx.service.WxToppingService;
import com.pcitech.iLife.util.Util;

/**
 * 置顶记录Controller
 * @author ilife
 * @version 2022-04-02
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxTopping")
public class WxToppingController extends BaseController {
	SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat timeFormatShort = new SimpleDateFormat("HH:mm:ss");
	@Autowired
	private WxToppingService wxToppingService;
	@Autowired
	private WxAdvertiseService wxAdvertiseService;
	@Autowired
	private WxArticleService wxArticleService;
	@Autowired
	private BrokerService brokerService;
	
	@ModelAttribute
	public WxTopping get(@RequestParam(required=false) String id) {
		WxTopping entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxToppingService.get(id);
		}
		if (entity == null){
			entity = new WxTopping();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxTopping:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxTopping wxTopping, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxTopping> page = wxToppingService.findPage(new Page<WxTopping>(request, response), wxTopping); 
		model.addAttribute("page", page);
		return "modules/wx/wxToppingList";
	}

	@RequiresPermissions("wx:wxTopping:view")
	@RequestMapping(value = "form")
	public String form(WxTopping wxTopping, Model model) {
		model.addAttribute("wxTopping", wxTopping);
		return "modules/wx/wxToppingForm";
	}

	@RequiresPermissions("wx:wxTopping:edit")
	@RequestMapping(value = "save")
	public String save(WxTopping wxTopping, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxTopping)){
			return form(wxTopping, model);
		}
		wxToppingService.save(wxTopping);
		addMessage(redirectAttributes, "保存置顶记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxTopping/?repage";
	}
	
	@RequiresPermissions("wx:wxTopping:edit")
	@RequestMapping(value = "delete")
	public String delete(WxTopping wxTopping, RedirectAttributes redirectAttributes) {
		wxToppingService.delete(wxTopping);
		addMessage(redirectAttributes, "删除置顶记录成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxTopping/?repage";
	}
	
	/**
	 *  设置文章或公众号临时置顶
	 * 	broker：id或openid至少传递一个
	 * 	subjectType:指定类别：article、account
	 * 	subjectId：指定内容ID：文章或公众号ID
	 *  advertiseDate：日期类型，开始展示日期，一般是当前日期
	 *  advertiseDuration：持续毫秒数。从当前时间开始持续展示时长
	 *  points：消耗的点数
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/poor-topping/{subjectType}", method = RequestMethod.POST)
	public Map<String, Object> publishArticle(@PathVariable String subjectType, @RequestBody JSONObject json){
		logger.debug("try to topping.[subjectType]"+subjectType);
		Map<String, Object> result = Maps.newHashMap();
		
		//获取broker info
		Broker broker = null;
		if(json.getString("brokerId")!=null && json.getString("brokerId").trim().length()>0){
			broker = brokerService.get(json.getString("brokerId").trim());
		}else if(json.getString("brokerOpenid")!=null && json.getString("brokerOpenid").trim().length()>0){
			broker = brokerService.getByOpenid(json.getString("brokerId").trim());
		}else {
			//糟糕了。竟然没有买主，这个就荒唐了，我也不知道咋整。直接入库吧，只是broker为空
			logger.warn("no broker info commit.");
			result.put("warn-broker", "no broker info.");
		}
		
		Date advertiseTimeFrom = new Date();
		long milliSecs = advertiseTimeFrom.getTime();
		long advertiseDuration = 10*1000;//默认为10秒
		try {
			advertiseDuration = Long.parseLong(json.getString("advertiseDuration"));
			advertiseDuration += 1000;//默认赠送1秒钟，消除网络处理误差
		}catch(Exception ex) {
			logger.error("failed parse advertise duration. must be milli-seconds.[input]"+json.getString("advertiseDuration"));
		}
		Date advertiseTimeTo = new Date(milliSecs+advertiseDuration);
		
		
		String id  = Util.md5(broker==null?Util.get32UUID():broker.getId() 
				+ subjectType 
				+ json.getString("subjectId") 
				+ json.getString("advertiseDate") 
				+ timeFormatShort.format(advertiseTimeFrom) 
				+ timeFormatShort.format(advertiseTimeTo) 
				);
		
		int points = 10;//默认消耗10点
		try {
			points = Integer.parseInt(json.getString("points"));
		}catch(Exception ex) {
			logger.error("failed parse points. required integer.[input]"+json.getString("points"));
		}
		
		//扣除阅豆
		broker.setPoints(broker.getPoints()-points);
		brokerService.save(broker);

		//将记录同时写入置顶记录明细，便于后续查询置顶记录
		WxTopping topping = new WxTopping();
		topping.setBroker(broker);
		try {
			topping.setAdvertiseDate(dateFormatShort.parse(json.getString("advertiseDate")));
		} catch (ParseException ex) {
			logger.error("failed parse advertise date.[advertise date]"+json.getString("advertiseDate"),ex);
		}
		topping.setAdvertiseTimeFrom(advertiseTimeFrom);
		topping.setAdvertiseTimeTo(advertiseTimeTo);
		topping.setAdvertisePrice(points);//单位为分
		topping.setAdvertiseType("points");
		topping.setAdvertiseWeight(1);//固定为权重为1，仅高于0
		topping.setSubjectType(subjectType );
		topping.setSubjectId(json.getString("subjectId"));
		topping.setCreateDate(new Date());
		topping.setUpdateDate(new Date());
		topping.setId(id);
		topping.setIsNewRecord(true);
		try {
			wxToppingService.save(topping);
		}catch(Exception ex) {
			logger.error("error create topping record.",ex);
		}
		return result;
	}
	
	/**
	 * 获取指定文章或公众号的置顶记录，过滤支持：达人(brokerId)、广告类别(advertiseType)、类别(subjectType)、条目(subjectId)
	 * 
	 * 支持传递brokerOpenid
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/upcoming", method = RequestMethod.GET)
	public List<WxTopping> listTopplingList( @RequestParam String brokerId,@RequestParam String brokerOpenid,@RequestParam String advertiseType,@RequestParam String subjectType, @RequestParam String subjectId) {
		Map<String,Object> params = Maps.newHashMap();
		//获取broker info
		if(brokerId!=null && brokerId.trim().length()>0) {
			params.put("brokerId", brokerId.trim());
		}else if(brokerOpenid!=null && brokerOpenid.trim().length()>0){
			Broker broker = null;
			broker = brokerService.getByOpenid(brokerOpenid);
			if(broker!=null)
				params.put("brokerId", broker.getId());
			else
				return Lists.newArrayList();
		}else {
			//do nothing
		}
		
		if(advertiseType!=null && advertiseType.trim().length()>0) {
			params.put("advertiseType", advertiseType.trim());
		}
		
		if(subjectType!=null && subjectType.trim().length()>0) {
			params.put("subjectType", subjectType.trim());
		}
		
		if(subjectId!=null && subjectId.trim().length()>0) {
			params.put("subjectId", subjectId.trim());
		}
		return wxToppingService.findUpcomingList(params);
	}

}