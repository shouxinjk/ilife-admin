/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.ItemEvaluationDimension;

/**
 * 主观评价-维度DAO接口
 * @author qchzhu
 * @version 2018-12-14
 */
@MyBatisDao
public interface ItemEvaluationDimensionDao extends CrudDao<ItemEvaluationDimension> {
	
}