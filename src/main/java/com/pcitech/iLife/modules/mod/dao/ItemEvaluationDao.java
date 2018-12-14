/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.ItemEvaluation;

/**
 * 主观评价DAO接口
 * @author qchzhu
 * @version 2018-12-14
 */
@MyBatisDao
public interface ItemEvaluationDao extends TreeDao<ItemEvaluation> {
	
}