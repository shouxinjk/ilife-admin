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
import com.pcitech.iLife.modules.diy.service.GuideBookProposalService;
import com.pcitech.iLife.modules.diy.service.GuideBookService;
import com.pcitech.iLife.modules.diy.service.ProposalSchemeService;
import com.pcitech.iLife.modules.mod.entity.CategoryNeed;

/**
 * 定制指南主题关联Controller
 * @author chenci
 * @version 2022-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/guideBookProposal")
public class GuideBookProposalController extends BaseController {

	@Autowired
	private GuideBookProposalService guideBookProposalService;
	@Autowired
	private GuideBookService guideBookService;
	@Autowired
	private ProposalSchemeService proposalSchemeService;
	
	@ModelAttribute
	public GuideBookProposal get(@RequestParam(required=false) String id) {
		GuideBookProposal entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = guideBookProposalService.get(id);
		}
		if (entity == null){
			entity = new GuideBookProposal();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:guideBookProposal:view")
	@RequestMapping(value = {"list", ""})
	public String list(GuideBookProposal guideBookProposal, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<GuideBookProposal> page = guideBookProposalService.findPage(new Page<GuideBookProposal>(request, response), guideBookProposal); 
		model.addAttribute("page", page);
		model.addAttribute("guideBookProposal", guideBookProposal);
		return "modules/diy/guideBookProposalList";
	}

	@RequiresPermissions("diy:guideBookProposal:view")
	@RequestMapping(value = "form")
	public String form(GuideBookProposal guideBookProposal, Model model) {
		model.addAttribute("guideBookProposal", guideBookProposal);
		return "modules/diy/guideBookProposalForm";
	}

	@RequiresPermissions("diy:guideBookProposal:edit")
	@RequestMapping(value = "save")
	public String save(GuideBookProposal guideBookProposal, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, guideBookProposal)){
			return form(guideBookProposal, model);
		}
		guideBookProposalService.save(guideBookProposal);
		addMessage(redirectAttributes, "保存定制指南主题关联成功");
		return "redirect:"+Global.getAdminPath()+"/diy/guideBookProposal/?repage";
	}
	
	@RequiresPermissions("diy:guideBookProposal:edit")
	@RequestMapping(value = "delete")
	public String delete(GuideBookProposal guideBookProposal, RedirectAttributes redirectAttributes) {
		guideBookProposalService.delete(guideBookProposal);
		addMessage(redirectAttributes, "删除定制指南主题关联成功");
		return "redirect:"+Global.getAdminPath()+"/diy/guideBookProposal/?repage";
	}


	/**
	 * 批量保存指南到定制主题
	 * @param  {schemeId:xxx, guideIds:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("diy:guideBookProposal:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddNeeds(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String schemeId = json.getString("schemeId");
		JSONArray guideIds = json.getJSONArray("guideIds");
		logger.debug("got params.[schemeId]"+schemeId+" [guideIds]"+guideIds);
		for(int i=0;i<guideIds.size();i++) {
			String guideId = guideIds.getString(i);
			GuideBookProposal guideBookProposal = new GuideBookProposal();
			guideBookProposal.setProposal(proposalSchemeService.get(schemeId));
			guideBookProposal.setGuide(guideBookService.get(guideId));
			guideBookProposal.setCreateDate(new Date());
			guideBookProposal.setUpdateDate(new Date());
			try {
				guideBookProposalService.save(guideBookProposal);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("error insert guide-proposal records.[schemeId]"+schemeId+" [guideIds]"+guideIds);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("schemeId", schemeId);
		return result;
	}
	
	
}