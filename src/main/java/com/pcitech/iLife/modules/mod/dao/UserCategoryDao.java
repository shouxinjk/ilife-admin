/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.UserCategory;

/**
 * 用户属性分类DAO接口
 * @author qchzhu
 * @version 2019-03-13
 */
@MyBatisDao
public interface UserCategoryDao extends TreeDao<UserCategory> {
	public String getUserCategoryNames(String userCategoryIds);
}