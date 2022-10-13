/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.DictValue;

/**
 * 业务字典DAO接口
 * @author chenci
 * @version 2022-10-13
 */
@MyBatisDao
public interface DictValueDao extends CrudDao<DictValue> {
	
}