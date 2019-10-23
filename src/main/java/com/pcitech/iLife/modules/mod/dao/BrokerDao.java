/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 达人管理DAO接口
 * @author qchzhu
 * @version 2019-10-14
 */
@MyBatisDao
public interface BrokerDao extends CrudDao<Broker> {
	//根据openid获取指定达人信息
	public Broker getByOpenid(String openid);
	
	//根据id查询达人收益信息
	public Map<String,Object> getMoney(String id);
}