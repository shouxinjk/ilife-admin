/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.text.ParseException;
import java.util.Date;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.wx.entity.WxAdvertise;
import com.pcitech.iLife.modules.wx.entity.WxPaymentAd;
import com.pcitech.iLife.modules.wx.entity.WxPaymentPoint;
import com.pcitech.iLife.modules.wx.entity.WxPoints;
import com.pcitech.iLife.modules.wx.entity.WxTopping;
import com.pcitech.iLife.modules.wx.service.WxPaymentPointService;
import com.pcitech.iLife.modules.wx.service.WxPointsService;
import com.pcitech.iLife.util.Util;

/**
 * 阅豆付款Controller
 * @author ilife
 * @version 2022-03-31
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxPaymentPoint")
public class WxPaymentPointController extends BaseController {

	@Autowired
	private WxPaymentPointService wxPaymentPointService;
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private WxPointsService wxPointsService;
	
	@ModelAttribute
	public WxPaymentPoint get(@RequestParam(required=false) String id) {
		WxPaymentPoint entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxPaymentPointService.get(id);
		}
		if (entity == null){
			entity = new WxPaymentPoint();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxPaymentPoint:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxPaymentPoint wxPaymentPoint, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxPaymentPoint> page = wxPaymentPointService.findPage(new Page<WxPaymentPoint>(request, response), wxPaymentPoint); 
		model.addAttribute("page", page);
		return "modules/wx/wxPaymentPointList";
	}

	@RequiresPermissions("wx:wxPaymentPoint:view")
	@RequestMapping(value = "form")
	public String form(WxPaymentPoint wxPaymentPoint, Model model) {
		model.addAttribute("wxPaymentPoint", wxPaymentPoint);
		return "modules/wx/wxPaymentPointForm";
	}

	@RequiresPermissions("wx:wxPaymentPoint:edit")
	@RequestMapping(value = "save")
	public String save(WxPaymentPoint wxPaymentPoint, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxPaymentPoint)){
			return form(wxPaymentPoint, model);
		}
		wxPaymentPointService.save(wxPaymentPoint);
		addMessage(redirectAttributes, "保存阅豆付款成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPaymentPoint/?repage";
	}
	
	@RequiresPermissions("wx:wxPaymentPoint:edit")
	@RequestMapping(value = "delete")
	public String delete(WxPaymentPoint wxPaymentPoint, RedirectAttributes redirectAttributes) {
		wxPaymentPointService.delete(wxPaymentPoint);
		addMessage(redirectAttributes, "删除阅豆付款成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPaymentPoint/?repage";
	}



	/**
	 * 支付成功后建立阅豆产品记录。由微信支付前端回传。参数：
	 * 
	 * 1，productId：阅豆产品ID
	 * 2，brokerId或brokerOpenid：达人信息
	 * 3，wxPay：{result_code:xxx,out_trade_no:xxx}
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/purchase", method = RequestMethod.POST)
	public JSONObject sellPointProduct(@RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
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
		//获取阅豆产品
		WxPoints wxPoint = wxPointsService.get(json.getString("productId").trim());
		//建立购买记录
		WxPaymentPoint wxPaymentPoint = new WxPaymentPoint();
		wxPaymentPoint.setAmount(wxPoint.getPrice());
		wxPaymentPoint.setBroker(broker);
		wxPaymentPoint.setPoints(wxPoint);
		wxPaymentPoint.setTradeNo(json.getString("out_trade_no"));
		wxPaymentPoint.setTradeState(json.getString("result_code"));
		wxPaymentPoint.setTransactionId("");//TODO 需要补充支付流水号
		wxPaymentPoint.setCreateDate(new Date());
		wxPaymentPoint.setUpdateDate(new Date());
		wxPaymentPoint.setPaymentDate(new Date());
		try {
			//更新达人阅豆
			broker.setPoints(broker.getPoints()+wxPoint.getPoints());
			brokerService.save(broker);
			//组织返回结果
			result.put("pointsRemain", broker.getPoints());
			result.put("success", true);
		}catch(Exception ex) {
			logger.error("failed save broker.",ex);
			result.put("msg", ex.getMessage());
			result.put("success", false);
		}
		result.put("points", wxPoint.getPoints());
		result.put("data", wxPaymentPoint);//返回的是临时记录
		return result;
	}
	
}