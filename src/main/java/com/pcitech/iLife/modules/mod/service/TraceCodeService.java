/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.TraceCode;
import com.pcitech.iLife.modules.mod.dao.TraceCodeDao;

/**
 * 推广位Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class TraceCodeService extends CrudService<TraceCodeDao, TraceCode> {

	public TraceCode get(String id) {
		return super.get(id);
	}
	
	public List<TraceCode> findList(TraceCode traceCode) {
		return super.findList(traceCode);
	}
	
	public Page<TraceCode> findPage(Page<TraceCode> page, TraceCode traceCode) {
		return super.findPage(page, traceCode);
	}
	
	@Transactional(readOnly = false)
	public void save(TraceCode traceCode) {
		super.save(traceCode);
	}
	
	@Transactional(readOnly = false)
	public void delete(TraceCode traceCode) {
		super.delete(traceCode);
	}
	
}