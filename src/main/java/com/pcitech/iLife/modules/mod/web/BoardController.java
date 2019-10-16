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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.service.BoardService;

/**
 * 内容看板管理Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/board")
public class BoardController extends BaseController {

	@Autowired
	private BoardService boardService;
	
	@ModelAttribute
	public Board get(@RequestParam(required=false) String id) {
		Board entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = boardService.get(id);
		}
		if (entity == null){
			entity = new Board();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:board:view")
	@RequestMapping(value = {"list", ""})
	public String list(Board board, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Board> page = boardService.findPage(new Page<Board>(request, response), board); 
		model.addAttribute("page", page);
		return "modules/mod/boardList";
	}

	@RequiresPermissions("mod:board:view")
	@RequestMapping(value = "form")
	public String form(Board board, Model model) {
		model.addAttribute("board", board);
		return "modules/mod/boardForm";
	}

	@RequiresPermissions("mod:board:edit")
	@RequestMapping(value = "save")
	public String save(Board board, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, board)){
			return form(board, model);
		}
		boardService.save(board);
		addMessage(redirectAttributes, "保存内容看板管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/board/?repage";
	}
	
	@RequiresPermissions("mod:board:edit")
	@RequestMapping(value = "delete")
	public String delete(Board board, RedirectAttributes redirectAttributes) {
		boardService.delete(board);
		addMessage(redirectAttributes, "删除内容看板管理成功");
		return "redirect:"+Global.getAdminPath()+"/mod/board/?repage";
	}

	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Board board, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Board> list =boardService.findList(board);
		for (int i=0; i<list.size(); i++){
			Board e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getTitle());
			mapList.add(map);
			
		}
		return mapList;
	}	
	
}