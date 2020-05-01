/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.TagCategory;
import com.pcitech.iLife.modules.mod.entity.UserTagCategory;

/**
 * 用户标签分类DAO接口
 * @author qchzhu
 * @version 2020-04-30
 */
@MyBatisDao
public interface UserTagCategoryDao extends TreeDao<UserTagCategory> {
	public int updateChildrenType(UserTagCategory entity); 
}