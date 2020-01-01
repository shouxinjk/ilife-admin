/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.BrokerPerformance;
import com.pcitech.iLife.modules.mod.dao.BrokerPerformanceDao;

/**
 * 推广效果Service
 * @author qchzhu
 * @version 2019-12-31
 */
@Service
@Transactional(readOnly = true)
public class BrokerPerformanceService extends CrudService<BrokerPerformanceDao, BrokerPerformance> {

	@Autowired
	BrokerPerformanceDao brokerPerformanceDao;
	
	public BrokerPerformance get(String id) {
		return super.get(id);
	}
	
	public void insertByBroker(String type) {
		brokerPerformanceDao.insertByBroker(type);
	}
	
	public List<BrokerPerformance> findListByTaskType(String type){
		return brokerPerformanceDao.findListByTaskType(type);
	}
	
	public Map<String,Object> getPerformanceCalcResult(Map<String,Object> params){
		return brokerPerformanceDao.getPerformanceCalcResult(params);
	}
	
	public List<BrokerPerformance> findList(BrokerPerformance performance) {
		return super.findList(performance);
	}
	
	public Page<BrokerPerformance> findPage(Page<BrokerPerformance> page, BrokerPerformance performance) {
		return super.findPage(page, performance);
	}
	
	@Transactional(readOnly = false)
	public void save(BrokerPerformance performance) {
		super.save(performance);
	}
	
	@Transactional(readOnly = false)
	public void delete(BrokerPerformance performance) {
		super.delete(performance);
	}
	
}