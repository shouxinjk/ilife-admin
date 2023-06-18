/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.IntPackagePricePlan;

/**
 * 订阅套餐明细DAO接口
 * @author ilife
 * @version 2023-06-18
 */
@MyBatisDao
public interface IntPackagePricePlanDao extends CrudDao<IntPackagePricePlan> {
	
}