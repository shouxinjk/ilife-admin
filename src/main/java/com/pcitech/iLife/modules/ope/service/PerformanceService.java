/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.ope.dao.PerformanceDao;

/**
 * 标注Service
 * @author chenci
 * @version 2017-09-28
 */
@Service
@Transactional(readOnly = true)
public class PerformanceService extends CrudService<PerformanceDao, Performance> {

	public Performance get(String id) {
		return super.get(id);
	}
	
	public List<Performance> findList(Performance performance) {
		return super.findList(performance);
	}
	
	public Page<Performance> findPage(Page<Performance> page, Performance performance) {
		return super.findPage(page, performance);
	}
	
	@Transactional(readOnly = false)
	public void save(Performance performance) {
		super.save(performance);
	}
	
	@Transactional(readOnly = false)
	public void delete(Performance performance) {
		super.delete(performance);
	}
	
}