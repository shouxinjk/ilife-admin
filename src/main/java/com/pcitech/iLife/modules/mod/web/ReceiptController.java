/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.pcitech.iLife.modules.mod.entity.Receipt;
import com.pcitech.iLife.modules.mod.service.ReceiptService;

/**
 * 收款管理Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/receipt")
public class ReceiptController extends BaseController {

	@Autowired
	private ReceiptService receiptService;
	
	@ModelAttribute
	public Receipt get(@RequestParam(required=false) String id) {
		Receipt entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = receiptService.get(id);
		}
		if (entity == null){
			entity = new Receipt();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:receipt:view")
	@RequestMapping(value = {"list", ""})
	public String list(Receipt receipt, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Receipt> page = receiptService.findPage(new Page<Receipt>(request, response), receipt); 
		model.addAttribute("page", page);
		return "modules/mod/receiptList";
	}

	@RequiresPermissions("mod:receipt:view")
	@RequestMapping(value = "form")
	public String form(Receipt receipt, Model model) {
		model.addAttribute("receipt", receipt);
		return "modules/mod/receiptForm";
	}

	@RequiresPermissions("mod:receipt:edit")
	@RequestMapping(value = "save")
	public String save(Receipt receipt, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, receipt)){
			return form(receipt, model);
		}
		receiptService.save(receipt);
		addMessage(redirectAttributes, "保存收款管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/receipt/?repage";
	}
	
	@RequiresPermissions("mod:receipt:edit")
	@RequestMapping(value = "delete")
	public String delete(Receipt receipt, RedirectAttributes redirectAttributes) {
		receiptService.delete(receipt);
		addMessage(redirectAttributes, "删除收款管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/receipt/?repage";
	}

}