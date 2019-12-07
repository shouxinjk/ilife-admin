/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

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

import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Board;
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
	
	@ResponseBody
	@RequestMapping(value = "rest/board-item", method = RequestMethod.POST)
	public Map<String, Object> createNewBoardItem(@RequestBody BoardItem item,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		boardItemService.save(item);
		result.put("status",true);
		result.put("description","Board item created successfully");
		BoardItem newItem = boardItemService.get(item);
		result.put("data", newItem);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/board-item/{id}", method = RequestMethod.PUT)
	public Map<String, Object> modifyBoardItem(@PathVariable String id,@RequestBody BoardItem item,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		BoardItem old = boardItemService.get(id);
		if(old == null) {
			result.put("status",false);
			result.put("description","Board item does not exist. id:"+id);
		}else {
			old.setTitle(item.getTitle());
			old.setDescription(item.getDescription());
			boardItemService.save(old);
			result.put("status",true);
			result.put("description","Board item modified successfully");
			BoardItem newItem = boardItemService.get(old);
			result.put("data", newItem); 
		}
		return result;
	}
	
	//Nginx跨域配置DELETE方法不支持，只能通过PUT方法进行
	@ResponseBody
	@RequestMapping(value = "rest/board-item/x/{id}", method = RequestMethod.PUT)
	public Map<String, Object> removeBoardItem(@PathVariable String id,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		BoardItem old = boardItemService.get(id);
		if(old == null) {
			result.put("status",false);
			result.put("description","Board item does not exist. id:"+id);
		}else {
			boardItemService.delete(old);
			result.put("status",true);
			result.put("description","Board item removed successfully");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "rest/board-items/{boardId}", method = RequestMethod.GET)
	public List<BoardItem> getItemsByBoard(@PathVariable String boardId,HttpServletRequest request, HttpServletResponse response, Model model) {
		return boardItemService.findByBoardId(boardId);
	}
	
}