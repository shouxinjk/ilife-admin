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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.entity.BoardItem;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.service.BoardItemService;
import com.pcitech.iLife.modules.mod.service.BoardService;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.util.Util;

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
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private BoardItemService boardItemService;
	
	
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
	
	@ResponseBody
	@RequestMapping(value = "rest/boards/{brokerId}", method = RequestMethod.GET)
	public List<Board> getBoardByBrokerId(@PathVariable String brokerId,@RequestParam int offset,@RequestParam int size,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("brokerId", brokerId);
		map.put("offset", offset);
		map.put("size", size);
		return boardService.findByBrokerId(map);
	}
	
	//查询所有board
	@ResponseBody
	@RequestMapping(value = "rest/all-boards", method = RequestMethod.GET)
	public List<Board> getAllBoards(@RequestParam int offset,@RequestParam int size,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("offset", offset);
		map.put("size", size);
		return boardService.findAllBoards(map);
	}
	
	//新建board
	@ResponseBody
	@RequestMapping(value = "rest/board", method = RequestMethod.POST)
	public Map<String, Object> createNewBoard(@RequestBody Board board,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		if(board.getId()==null || board.getId().trim().length()==0) {//如果没有ID则随机补充ID，并新建
			board.setId(Util.get32UUID());
			board.setIsNewRecord(true);
		}
		boardService.save(board);
		result.put("status",true);
		result.put("description","Board created successfully");
		Board newBoard = boardService.get(board);
		result.put("data", newBoard);
		return result;
	}
	
	//修改board
	@ResponseBody
	@RequestMapping(value = "rest/board/{id}", method = RequestMethod.PUT)
	public Map<String, Object> modifyBoard(@PathVariable String id,@RequestBody Board board,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		Board old = boardService.get(id);
		if(old == null) {
			result.put("status",false);
			result.put("description","Board does not exist. id:"+id);
		}else {
			old.setTitle(board.getTitle());
			old.setDescription(board.getDescription());
			old.setKeywords(board.getKeywords());
			old.setTags(board.getTags());
			boardService.save(old);
			result.put("status",true);
			result.put("description","Board modified successfully");
			Board newBoard = boardService.get(old);
			result.put("data", newBoard);
		}
		return result;
	}
	
	//获取指定board
	@ResponseBody
	@RequestMapping(value = "rest/board/{id}", method = RequestMethod.GET)
	public Map<String, Object> getBoardById(@PathVariable String id,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		Board old = boardService.get(id);
		if(old == null) {
			result.put("status",false);
			result.put("description","Board does not exist. id:"+id);
		}else {
			result.put("status",true);
			result.put("description","Board found successfully");
			result.put("data", old);
		}
		return result;
	}
	
	//复制board：保持内容完全一致，只修改broker信息
	@ResponseBody
	@RequestMapping(value = "rest/board/clone/{id}/{brokerId}", method = RequestMethod.POST)
	public Map<String, Object> cloneBoardById(@PathVariable String id,@PathVariable String brokerId,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		Board old = boardService.get(id);
		Broker broker = brokerService.get(brokerId);
		if(old == null) {
			result.put("status",false);
			result.put("description","Board does not exist. [id]"+id);
		}else if(broker == null) {
			result.put("status",false);
			result.put("description","Broker does not exist. [id]"+brokerId);
		}else {
			Board board = old;
			board.setTitle(board.getTitle()+" by "+broker.getName());
			//String idstr = Util.md5(id+brokerId);
			String idstr = Util.get32UUID();//不限制克隆条数，采用随机ID
			board.setBroker(broker);
			board.setId(idstr);
			board.setIsNewRecord(true);//新建board
			boardService.save(board);
			board = boardService.get(idstr);
			//查询boardItem并建立到新的board下
			List<BoardItem> items = boardItemService.findByBoardId(idstr);
			for(BoardItem item:items) {
				item.setId(Util.get32UUID());
				item.setIsNewRecord(true);//新建boarditem
				item.setBoard(board);
				boardItemService.save(item);
			}
			result.put("status",true);
			result.put("description","Board cloned successfully");
			result.put("data", board);
		}
		return result;
	}
	
}