/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;

/**
 * 外部诱因类别DAO接口
 * @author chenci
 * @version 2017-09-15
 */
@MyBatisDao
public interface OccasionCategoryDao extends TreeDao<OccasionCategory> {
	
}