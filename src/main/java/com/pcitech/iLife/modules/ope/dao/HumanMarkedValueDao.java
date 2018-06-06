/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.ope.entity.HumanMarkedValue;

/**
 * 数据标注DAO接口
 * @author chenci
 * @version 2017-09-28
 */
@MyBatisDao
public interface HumanMarkedValueDao extends CrudDao<HumanMarkedValue> {
	
}