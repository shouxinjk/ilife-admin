/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.LifeStyleCategory;

/**
 * vals分类DAO接口
 * @author chenci
 * @version 2018-08-05
 */
@MyBatisDao
public interface LifeStyleCategoryDao extends TreeDao<LifeStyleCategory> {
	
}