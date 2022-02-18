/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.entity.Channel;

/**
 * 频道管理DAO接口
 * @author ilife
 * @version 2022-02-17
 */
@MyBatisDao
public interface ChannelDao extends CrudDao<Channel> {
	//根据status查询，按照sort降序排列
	public List<Channel> findListByStatus(Channel channel);
}