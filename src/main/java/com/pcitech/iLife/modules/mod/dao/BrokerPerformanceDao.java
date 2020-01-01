/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.BrokerPerformance;

/**
 * 推广效果DAO接口
 * @author qchzhu
 * @version 2019-12-31
 */
@MyBatisDao
public interface BrokerPerformanceDao extends CrudDao<BrokerPerformance> {
	
	//查询所有broker并建立初始Performance任务
	public void insertByBroker(String type);
	
	//查询所有待处理performance任务
	public List<BrokerPerformance> findListByTaskType(String type);
	
	//计算performance
	public Map<String,Object> getPerformanceCalcResult(Map<String,Object> params);
	
}