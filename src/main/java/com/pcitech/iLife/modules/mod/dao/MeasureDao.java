/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;

/**
 * 商品属性DAO接口
 * @author chenci
 * @version 2017-09-22
 */
@MyBatisDao
public interface MeasureDao extends CrudDao<Measure> {
	public List<Measure> findByCategoryId(String category);
}