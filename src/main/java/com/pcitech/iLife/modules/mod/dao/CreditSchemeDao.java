/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.CreditScheme;

/**
 * 积分规则DAO接口
 * @author qchzhu
 * @version 2019-10-30
 */
@MyBatisDao
public interface CreditSchemeDao extends CrudDao<CreditScheme> {
	public CreditScheme getByQuery(CreditScheme query);
}