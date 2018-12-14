/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.ItemDimensionMeasure;

/**
 * 客观评价明细DAO接口
 * @author qchzhu
 * @version 2018-12-12
 */
@MyBatisDao
public interface ItemDimensionMeasureDao extends CrudDao<ItemDimensionMeasure> {
	
}