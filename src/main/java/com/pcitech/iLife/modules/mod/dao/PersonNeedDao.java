/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.PersonNeed;

/**
 * 用户需要构成DAO接口
 * @author ilife
 * @version 2022-02-17
 */
@MyBatisDao
public interface PersonNeedDao extends CrudDao<PersonNeed> {
	//更新weight
	public void updateWeight(Map<String,Object> params);
}