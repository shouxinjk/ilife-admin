/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.ilife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.ilife.modules.mod.entity.Subscription;

/**
 * SaaS订阅DAO接口
 * @author ilife
 * @version 2023-06-13
 */
@MyBatisDao
public interface SubscriptionDao extends CrudDao<Subscription> {
	
}