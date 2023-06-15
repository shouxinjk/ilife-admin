/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
import com.pcitech.iLife.modules.diy.entity.TenantPoints;
import com.pcitech.iLife.modules.diy.service.TenantPointsService;
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
    @Autowired
    private TenantPointsService tenantPointsService;
    
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
	 * 租户购买虚拟豆
	 * 支付成功后建立阅豆产品记录。由微信支付前端回传。参数：
	 * 
	 * 1，productId：阅豆产品ID
	 * 2，tenantId： 租户ID
	 * 3，wxPay：{result_code:xxx,out_trade_no:xxx}
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/tenant-purchase", method = RequestMethod.POST)
	public JSONObject purchasePointProductByTenant(@RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		Integer tenantId = json.getInteger("tenantId");
		//获取TenantId
		if(tenantId==null){
			//糟糕了。竟然没有买主，这个就荒唐了，先记到平台租户下
			tenantId = 0;
			logger.warn("no tenantId commit.");
			result.put("warn-tenantId", "no tenantId info.");
		}
		//获取阅豆产品
		WxPoints wxPoint = wxPointsService.get(json.getString("productId").trim());
		//建立购买记录：同时支持支付后回调，需要查询记录是否已经存在，根据out_trade_no完成
		//更新租户points: 先根据购买out_trade_no查询得到租户及虚拟豆信息
		WxPaymentPoint wxPaymentPoint = new WxPaymentPoint();
		wxPaymentPoint.setTradeNo(json.getString("out_trade_no"));
		List<WxPaymentPoint> wxPaymentPoints = wxPaymentPointService.findList(wxPaymentPoint);
		if(wxPaymentPoints!=null && wxPaymentPoints.size()>0) { //购买记录已经存在：后端已经接收到微信支付callback
			wxPaymentPoint = wxPaymentPoints.get(0);//使用已经存在的记录
		}else { //否则新建记录
			wxPaymentPoint.setAmount(wxPoint.getPrice());
			wxPaymentPoint.setTenantId(json.getInteger("tenantId"));
			wxPaymentPoint.setPoints(wxPoint);
			wxPaymentPoint.setTradeNo(json.getString("out_trade_no"));
			wxPaymentPoint.setTradeState(json.getString("result_code"));
			wxPaymentPoint.setTransactionId(json.getString("transaction_id"));//默认为空，待微信支付后更新
			wxPaymentPoint.setCreateDate(new Date());
			wxPaymentPoint.setUpdateDate(new Date());
			wxPaymentPoint.setPaymentDate(new Date());
			wxPaymentPointService.save(wxPaymentPoint);//保存记录
		}

		//更新租户阅豆
		TenantPoints tenantPoints = new TenantPoints();
		tenantPoints.setId(""+json.getInteger("tenantId"));//ID与tenantId完全一致
		tenantPoints.setTenantId(json.getInteger("tenantId"));
		tenantPoints.setPoints(wxPoint.getPoints()); //只需要设置新购买的增量
		tenantPointsService.updatePoints(tenantPoints);
		//组织返回结果
		result.put("success", true);
		result.put("points", wxPoint.getPoints());
		result.put("data", wxPaymentPoint);//返回的是临时记录
		
		return result;
	}

	/**
	 * 达人购买虚拟豆
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
		wxPaymentPoint.setTransactionId("");//默认为空，待微信支付后更新
		wxPaymentPoint.setCreateDate(new Date());
		wxPaymentPoint.setUpdateDate(new Date());
		wxPaymentPoint.setPaymentDate(new Date());
		wxPaymentPointService.save(wxPaymentPoint);//保存记录
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