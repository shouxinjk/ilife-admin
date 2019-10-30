/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.CommissionScheme;

/**
 * 佣金规则设置DAO接口
 * @author qchzhu
 * @version 2019-10-14
 */
@MyBatisDao
public interface CommissionSchemeDao extends CrudDao<CommissionScheme> {
	public CommissionScheme getByQuery(CommissionScheme scheme);
}