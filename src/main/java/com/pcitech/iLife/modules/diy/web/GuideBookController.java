/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.web;

import java.util.List;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.diy.entity.GuideBook;
import com.pcitech.iLife.modules.diy.entity.GuideTerm;
import com.pcitech.iLife.modules.diy.entity.ProposalScheme;
import com.pcitech.iLife.modules.diy.service.GuideBookService;
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.util.Util;

/**
 * 个性化定制指南Controller
 * @author chenci
 * @version 2022-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/diy/guideBook")
public class GuideBookController extends BaseController {

	@Autowired
	private GuideBookService guideBookService;
	
	//过滤获取指南列表：分页可自行设置page
	/**
	{
		page:{
			pageNo:xxx,
			pageSize:xxx
		}
	}
	 */
	@ResponseBody
	@RequestMapping(value = "rest/guide-books", method = RequestMethod.POST)
	public List<GuideBook> listPagedList(@RequestBody GuideBook guideBook) {
		if(guideBook.getPage()!=null)
			return guideBookService.findPage(guideBook.getPage(), guideBook).getList();
		else
			return guideBookService.findList(guideBook);
	}
	
	@ModelAttribute
	public GuideBook get(@RequestParam(required=false) String id) {
		GuideBook entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = guideBookService.get(id);
		}
		if (entity == null){
			entity = new GuideBook();
		}
		return entity;
	}
	
	@RequiresPermissions("diy:guideBook:view")
	@RequestMapping(value = {"list", ""})
	public String list(GuideBook guideBook, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<GuideBook> page = guideBookService.findPage(new Page<GuideBook>(request, response), guideBook); 
		model.addAttribute("page", page);
		return "modules/diy/guideBookList";
	}
	

	//新增或修改
	@ResponseBody
	@RequestMapping(value = "rest/guide", method = RequestMethod.POST)
	public JSONObject upsert( @RequestBody GuideBook guideBook) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		String id = Util.get32UUID();
		if(guideBook.getId()==null||guideBook.getId().trim().length()==0) {//认为是新增
			guideBook.setId(id);
			guideBook.setIsNewRecord(true);
		}
		try {
			guideBookService.save(guideBook);
			result.put("data", guideBookService.get(guideBook));
			result.put("success", true);
		}catch(Exception ex) {
			result.put("success", false);
			result.put("error", ex.getMessage());
		}
		return result;
	}

	
	/**
	 * 根据定制指南ID查询所有待添加指南列表
	 * @param schemeId
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("diy:guideBook:view")
	@RequestMapping(value = {"list2"})
	public String listPendingItemsForProposal(String schemeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<GuideBook> guideBooks = guideBookService.findPendingListForProposal(schemeId);
		model.addAttribute("guideBooks", guideBooks);
		model.addAttribute("schemeId", schemeId);
		return "modules/diy/guideBookList2";
	}

	@RequiresPermissions("diy:guideBook:view")
	@RequestMapping(value = "form")
	public String form(GuideBook guideBook, Model model) {
		model.addAttribute("guideBook", guideBook);
		return "modules/diy/guideBookForm";
	}

	@RequiresPermissions("diy:guideBook:edit")
	@RequestMapping(value = "save")
	public String save(GuideBook guideBook, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, guideBook)){
			return form(guideBook, model);
		}
		guideBookService.save(guideBook);
		addMessage(redirectAttributes, "保存个性化定制指南成功");
		return "redirect:"+Global.getAdminPath()+"/diy/guideBook/?repage";
	}
	
	@RequiresPermissions("diy:guideBook:edit")
	@RequestMapping(value = "delete")
	public String delete(GuideBook guideBook, RedirectAttributes redirectAttributes) {
		guideBookService.delete(guideBook);
		addMessage(redirectAttributes, "删除个性化定制指南成功");
		return "redirect:"+Global.getAdminPath()+"/diy/guideBook/?repage";
	}

	/**
	 * 查询所有可用定制指南列表
	 * @param guideBook
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(GuideBook guideBook, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<GuideBook> list = guideBookService.findList(guideBook);
		for (int i=0; i<list.size(); i++){
			GuideBook e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
			mapList.add(map);
			
		}
		return mapList;
	}
	
}