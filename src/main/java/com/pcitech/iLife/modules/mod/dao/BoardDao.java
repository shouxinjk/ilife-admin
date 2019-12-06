/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Board;

/**
 * 内容看板管理DAO接口
 * @author qchzhu
 * @version 2019-10-14
 */
@MyBatisDao
public interface BoardDao extends CrudDao<Board> {
	//根据brokerId查找创建的board。含分页参数
	public List<Board> findByBrokerId(Map<String,Object> params);
}