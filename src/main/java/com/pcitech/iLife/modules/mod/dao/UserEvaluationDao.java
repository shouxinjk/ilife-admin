/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;

/**
 * 用户主观评价DAO接口
 * @author qchzhu
 * @version 2019-03-13
 */
@MyBatisDao
public interface UserEvaluationDao extends CrudDao<UserEvaluation> {
	
}