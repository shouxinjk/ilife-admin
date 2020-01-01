/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Clearing;

/**
 * 清算DAO接口
 * @author qchzhu
 * @version 2019-10-14
 */
@MyBatisDao
public interface ClearingDao extends CrudDao<Clearing> {
	public List<Clearing> findListByBroker(Map<String,Object> param);
	public List<Map<String,Object>> findPendingNotifyList();
}