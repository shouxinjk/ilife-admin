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
import com.pcitech.iLife.modules.mod.entity.BoardItem;
import com.pcitech.iLife.modules.mod.service.BoardItemService;

/**
 * 看板条目明细管理Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/boardItem")
public class BoardItemController extends BaseController {

	@Autowired
	private BoardItemService boardItemService;
	
	@ModelAttribute
	public BoardItem get(@RequestParam(required=false) String id) {
		BoardItem entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = boardItemService.get(id);
		}
		if (entity == null){
			entity = new BoardItem();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:boardItem:view")
	@RequestMapping(value = {"list", ""})
	public String list(BoardItem boardItem, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BoardItem> page = boardItemService.findPage(new Page<BoardItem>(request, response), boardItem); 
		model.addAttribute("page", page);
		return "modules/mod/boardItemList";
	}

	@RequiresPermissions("mod:boardItem:view")
	@RequestMapping(value = "form")
	public String form(BoardItem boardItem, Model model) {
		model.addAttribute("boardItem", boardItem);
		return "modules/mod/boardItemForm";
	}

	@RequiresPermissions("mod:boardItem:edit")
	@RequestMapping(value = "save")
	public String save(BoardItem boardItem, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, boardItem)){
			return form(boardItem, model);
		}
		boardItemService.save(boardItem);
		addMessage(redirectAttributes, "保存看板条目明细管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/boardItem/?repage";
	}
	
	@RequiresPermissions("mod:boardItem:edit")
	@RequestMapping(value = "delete")
	public String delete(BoardItem boardItem, RedirectAttributes redirectAttributes) {
		boardItemService.delete(boardItem);
		addMessage(redirectAttributes, "删除看板条目明细管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/boardItem/?repage";
	}

}