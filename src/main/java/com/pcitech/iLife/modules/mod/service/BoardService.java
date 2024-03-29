/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.dao.BoardDao;

/**
 * 内容看板管理Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class BoardService extends CrudService<BoardDao, Board> {

	@Autowired
	BoardDao boardDao;
	
	public Board get(String id) {
		return super.get(id);
	}
	
	public List<Board> findAllBoards(Map<String,Object> params) {
		return boardDao.findAllBoards(params);
	}
	
	public List<Board> findByBrokerId(Map<String,Object> params) {
		return boardDao.findByBrokerId(params);
	}
	
	public List<Board> findByOpenid(Map<String,Object> params) {
		return boardDao.findByOpenid(params);
	}
	
	public List<Board> findListByDate(Map<String,Object> params) {
		return boardDao.findListByDate(params);
	}
	
	public List<Board> findList(Board board) {
		return super.findList(board);
	}
	
	public Page<Board> findPage(Page<Board> page, Board board) {
		return super.findPage(page, board);
	}
	
	@Transactional(readOnly = false)
	public void save(Board board) {
		super.save(board);
	}
	
	@Transactional(readOnly = false)
	public void delete(Board board) {
		super.delete(board);
	}
	
}