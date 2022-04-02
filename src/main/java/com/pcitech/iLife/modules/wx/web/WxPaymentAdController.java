/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.wx.entity.WxPaymentAd;
import com.pcitech.iLife.modules.wx.entity.WxPoints;
import com.pcitech.iLife.modules.wx.service.WxAdvertiseService;
import com.pcitech.iLife.modules.wx.service.WxPaymentAdService;
import com.pcitech.iLife.util.Util;

/**
 * 置顶广告付款Controller
 * @author ilife
 * @version 2022-03-31
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxPaymentAd")
public class WxPaymentAdController extends BaseController {
	SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	private WxPaymentAdService wxPaymentAdService;
	@Autowired
	private WxAdvertiseService wxAdvertiseService;
	@Autowired
	private BrokerService brokerService;
	
	@ModelAttribute
	public WxPaymentAd get(@RequestParam(required=false) String id) {
		WxPaymentAd entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxPaymentAdService.get(id);
		}
		if (entity == null){
			entity = new WxPaymentAd();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxPaymentAd:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxPaymentAd wxPaymentAd, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxPaymentAd> page = wxPaymentAdService.findPage(new Page<WxPaymentAd>(request, response), wxPaymentAd); 
		model.addAttribute("page", page);
		return "modules/wx/wxPaymentAdList";
	}

	@RequiresPermissions("wx:wxPaymentAd:view")
	@RequestMapping(value = "form")
	public String form(WxPaymentAd wxPaymentAd, Model model) {
		model.addAttribute("wxPaymentAd", wxPaymentAd);
		return "modules/wx/wxPaymentAdForm";
	}

	@RequiresPermissions("wx:wxPaymentAd:edit")
	@RequestMapping(value = "save")
	public String save(WxPaymentAd wxPaymentAd, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxPaymentAd)){
			return form(wxPaymentAd, model);
		}
		wxPaymentAdService.save(wxPaymentAd);
		addMessage(redirectAttributes, "保存指定广告付款成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPaymentAd/?repage";
	}
	
	@RequiresPermissions("wx:wxPaymentAd:edit")
	@RequestMapping(value = "delete")
	public String delete(WxPaymentAd wxPaymentAd, RedirectAttributes redirectAttributes) {
		wxPaymentAdService.delete(wxPaymentAd);
		addMessage(redirectAttributes, "删除指定广告付款成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxPaymentAd/?repage";
	}	

	/**
	 * 查询指定时间段内已经售出的广告列表
	 */
	@ResponseBody
	@RequestMapping(value = "rest/sold-in-advance/{days}", method = RequestMethod.GET)
	public List<WxPaymentAd> findSoldAds(@PathVariable int days) {
		return wxPaymentAdService.findSoldAds(days);
	}
	
	/**
	 * 支付成功后创建广告位记录。由微信支付前端回传。参数：
	 * 
	 * 1，brokerId:xxx如果没有id，则根据openid查找
	 *    brokerOpenid:xxx,
	 * 2，subjectType：xxx//article或account：展示内容类型
	 *    subjectId:xxx //文章ID或公众号ID
	 * 3，ads:[{date:xxx,id:xxx,price:xxx}]
	 * 4，wxPay：{result_code:xxx,out_trade_no:xxx}
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/purchase", method = RequestMethod.POST)
	public JSONObject sellAds(@RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		JSONArray retData = new JSONArray();
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
		//循环构建所有广告
		JSONArray ads = json.getJSONArray("ads");
		for(int i=0;i<ads.size();i++) {
			JSONObject item = ads.getJSONObject(i);
			WxPaymentAd ad = new WxPaymentAd();
			ad.setBroker(broker);
			ad.setSubjectId(json.getString("subjectId"));
			ad.setTradeNo(json.getString("out_trade_no"));
			ad.setTradeState(json.getString("result_code"));
			ad.setTransactionId("");//TODO:根据微信支付返回结果补充
			ad.setAdvertise(wxAdvertiseService.get(item.getString("id")));
			ad.setAmount(item.getString("id"));
			try {
				ad.setAdvertiseDate(dateFormatShort.parse(item.getString("date")));
			}catch(Exception ex) {
				result.put("warn-date-"+i, "wrong advertise date.");
				logger.error("failed parse advertise date.",ex);
			}
			//手动组织ID：达人ID+展示主题类型+展示主题ID+展示日期+广告位ID
			String id  = Util.md5(broker==null?Util.get32UUID():broker.getId() + json.getString("subjectType") + json.getString("subjectId") + item.getString("date") + item.getString("id") );
			ad.setCreateDate(new Date());
			ad.setUpdateDate(new Date());
			ad.setId(id);
			ad.setIsNewRecord(true);
			wxPaymentAdService.save(ad);
			retData.add(wxPaymentAdService.get(id));
		}
		result.put("data", retData);
		result.put("success", true);
		return result;
	}
	
}