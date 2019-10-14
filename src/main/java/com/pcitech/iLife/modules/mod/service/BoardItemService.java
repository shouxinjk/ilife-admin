/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.BoardItem;
import com.pcitech.iLife.modules.mod.dao.BoardItemDao;

/**
 * 看板条目明细管理Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class BoardItemService extends CrudService<BoardItemDao, BoardItem> {

	public BoardItem get(String id) {
		return super.get(id);
	}
	
	public List<BoardItem> findList(BoardItem boardItem) {
		return super.findList(boardItem);
	}
	
	public Page<BoardItem> findPage(Page<BoardItem> page, BoardItem boardItem) {
		return super.findPage(page, boardItem);
	}
	
	@Transactional(readOnly = false)
	public void save(BoardItem boardItem) {
		super.save(boardItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(BoardItem boardItem) {
		super.delete(boardItem);
	}
	
}