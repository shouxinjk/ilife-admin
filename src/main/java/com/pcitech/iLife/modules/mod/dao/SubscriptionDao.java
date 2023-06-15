/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Subscription;

/**
 * SaaS订阅DAO接口
 * @author ilife
 * @version 2023-06-13
 */
@MyBatisDao
public interface SubscriptionDao extends CrudDao<Subscription> {
	//更新微信支付记录状态 
	public void updateWxTransactionInfoByTradeNo(Map<String,String> params);
}