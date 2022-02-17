/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Channel;

/**
 * 频道管理DAO接口
 * @author ilife
 * @version 2022-02-17
 */
@MyBatisDao
public interface ChannelDao extends CrudDao<Channel> {
	
}