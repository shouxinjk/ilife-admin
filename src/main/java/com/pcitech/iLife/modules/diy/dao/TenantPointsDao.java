/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.diy.entity.TenantPoints;

/**
 * 租户余额信息DAO接口
 * @author ilife
 * @version 2023-06-15
 */
@MyBatisDao
public interface TenantPointsDao extends CrudDao<TenantPoints> {
	public void updatePoints(TenantPoints tenantPoints);
}