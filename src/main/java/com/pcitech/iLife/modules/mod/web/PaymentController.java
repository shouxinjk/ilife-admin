/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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
import com.pcitech.iLife.modules.mod.entity.Payment;
import com.pcitech.iLife.modules.mod.service.PaymentService;
import com.pcitech.iLife.util.Util;

/**
 * 支付管理Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/payment")
public class PaymentController extends BaseController {

	@Autowired
	private PaymentService paymentService;
	
	@ModelAttribute
	public Payment get(@RequestParam(required=false) String id) {
		Payment entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = paymentService.get(id);
		}
		if (entity == null){
			entity = new Payment();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:payment:view")
	@RequestMapping(value = {"list", ""})
	public String list(Payment payment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Payment> page = paymentService.findPage(new Page<Payment>(request, response), payment); 
		model.addAttribute("page", page);
		return "modules/mod/paymentList";
	}

	@RequiresPermissions("mod:payment:view")
	@RequestMapping(value = "form")
	public String form(Payment payment, Model model) {
		model.addAttribute("payment", payment);
		return "modules/mod/paymentForm";
	}

	@RequiresPermissions("mod:payment:edit")
	@RequestMapping(value = "save")
	public String save(Payment payment, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, payment)){
			return form(payment, model);
		}
		paymentService.save(payment);
		addMessage(redirectAttributes, "保存支付管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/payment/?repage";
	}
	
	@RequiresPermissions("mod:payment:edit")
	@RequestMapping(value = "delete")
	public String delete(Payment payment, RedirectAttributes redirectAttributes) {
		paymentService.delete(payment);
		addMessage(redirectAttributes, "删除支付管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/payment/?repage";
	}
	
	/**
	 * 发起提现申请，建立支付记录
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/payment", method = RequestMethod.POST)
	public Map<String,Object> createPaymentInfo(@RequestBody Payment payment) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		String id = Util.get32UUID();
		payment.setId(id);
		payment.setIsNewRecord(true);
		payment.setStatus("pending");
		payment.setCreateDate(new Date());
		payment.setUpdateDate(new Date());
		paymentService.save(payment);
		payment = paymentService.get(id);
		result.put("success", true);
		result.put("data", payment);
		return result;
	}

}