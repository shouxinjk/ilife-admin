/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;

/**
 * 用户属性定义DAO接口
 * @author qchzhu
 * @version 2019-03-13
 */
@MyBatisDao
public interface UserMeasureDao extends CrudDao<UserMeasure> {
	public List<UserMeasure> findByCategoryId(String category);
}