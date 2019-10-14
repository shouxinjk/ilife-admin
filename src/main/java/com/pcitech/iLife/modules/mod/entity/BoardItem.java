/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 看板条目明细管理Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class BoardItem extends DataEntity<BoardItem> {
	
	private static final long serialVersionUID = 1L;
	private String boardId;		// 对应Board ID
	private String title;		// 标题
	private String description;		// 描述
	
	public BoardItem() {
		super();
	}

	public BoardItem(String id){
		super(id);
	}

	@Length(min=1, max=64, message="对应Board ID长度必须介于 1 和 64 之间")
	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}
	
	@Length(min=1, max=255, message="标题长度必须介于 1 和 255 之间")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}