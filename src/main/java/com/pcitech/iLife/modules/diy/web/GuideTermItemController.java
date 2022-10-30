/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.web;

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
import com.pcitech.iLife.modules.diy.entity.GuideBookProposal;
import com.pcitech.iLife.modules.diy.entity.GuideTermItem;
import com.pcitech.iLife.modules.diy.service.GuideTermItemService;
import com.pcitech.iLife.modules.diy.service.GuideTermService;
import com.pcitech.iLife.modules.ope.service.ItemService;

/**
 * 指南规则条目关联Controller
 * @author chenci
 * @version 2022-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/guideTermItem")
public class GuideTermItemController extends BaseController {

	@Autowired
	private GuideTermItemService guideTermItemService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private GuideTermService guideTermService;
	
	@ModelAttribute
	public GuideTermItem get(@RequestParam(required=false) String id) {
		GuideTermItem entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = guideTermItemService.get(id);
		}
		if (entity == null){
			entity = new GuideTermItem();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:guideTermItem:view")
	@RequestMapping(value = {"list", ""})
	public String list(GuideTermItem guideTermItem, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<GuideTermItem> page = guideTermItemService.findPage(new Page<GuideTermItem>(request, response), guideTermItem); 
		model.addAttribute("page", page);
		return "modules/diy/guideTermItemList";
	}

	@RequiresPermissions("diy:guideTermItem:view")
	@RequestMapping(value = "form")
	public String form(GuideTermItem guideTermItem, Model model) {
		model.addAttribute("guideTermItem", guideTermItem);
		return "modules/diy/guideTermItemForm";
	}

	@RequiresPermissions("diy:guideTermItem:edit")
	@RequestMapping(value = "save")
	public String save(GuideTermItem guideTermItem, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, guideTermItem)){
			return form(guideTermItem, model);
		}
		guideTermItemService.save(guideTermItem);
		addMessage(redirectAttributes, "保存指南规则条目关联成功");
		return "redirect:"+Global.getAdminPath()+"/diy/guideTermItem/?repage";
	}
	
	@RequiresPermissions("diy:guideTermItem:edit")
	@RequestMapping(value = "delete")
	public String delete(GuideTermItem guideTermItem, RedirectAttributes redirectAttributes) {
		guideTermItemService.delete(guideTermItem);
		addMessage(redirectAttributes, "删除指南规则条目关联成功");
		return "redirect:"+Global.getAdminPath()+"/diy/guideTermItem/?repage";
	}
	
	/**
	 * 批量保存条目到指南条目
	 * @param  {termId:xxx, itemIds:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("diy:guideTermItem:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddNeeds(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String termId = json.getString("termId");
		JSONArray itemIds = json.getJSONArray("itemIds");
		logger.debug("got params.[termId]"+termId+" [itemIds]"+itemIds);
		for(int i=0;i<itemIds.size();i++) {
			String itemId = itemIds.getString(i);
			GuideTermItem guideTermItem = new GuideTermItem();
			guideTermItem.setTerm(guideTermService.get(termId));
			guideTermItem.setItem(itemService.get(itemId));
			guideTermItem.setCreateDate(new Date());
			guideTermItem.setUpdateDate(new Date());
			try {
				guideTermItemService.save(guideTermItem);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("error insert guide-term-item records.[termId]"+termId+" [itemIds]"+itemIds);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("termId", termId);
		return result;
	}

}