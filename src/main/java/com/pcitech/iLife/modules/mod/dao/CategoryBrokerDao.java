/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.CategoryBroker;

/**
 * 类目专家授权DAO接口
 * @author ilife
 * @version 2022-12-27
 */
@MyBatisDao
public interface CategoryBrokerDao extends CrudDao<CategoryBroker> {
	
}