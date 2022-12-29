/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.ope.entity.HumanMarkedDict;

/**
 * 用户标注字典DAO接口
 * @author ilife
 * @version 2022-12-29
 */
@MyBatisDao
public interface HumanMarkedDictDao extends CrudDao<HumanMarkedDict> {
	
}