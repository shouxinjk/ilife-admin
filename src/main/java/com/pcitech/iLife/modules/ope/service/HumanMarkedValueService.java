/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.ope.entity.HumanMarkedValue;
import com.pcitech.iLife.modules.ope.dao.HumanMarkedValueDao;

/**
 * 数据标注Service
 * @author chenci
 * @version 2017-09-28
 */
@Service
@Transactional(readOnly = true)
public class HumanMarkedValueService extends CrudService<HumanMarkedValueDao, HumanMarkedValue> {

	@Autowired
	HumanMarkedValueDao humanMarkedValueDao;
	
	public HumanMarkedValue get(String id) {
		return super.get(id);
	}
	
	public List<HumanMarkedValue> findList(HumanMarkedValue humanMarkedValue) {
		return super.findList(humanMarkedValue);
	}
	
	public Page<HumanMarkedValue> findPage(Page<HumanMarkedValue> page, HumanMarkedValue humanMarkedValue) {
		return super.findPage(page, humanMarkedValue);
	}
	
	@Transactional(readOnly = false)
	public void save(HumanMarkedValue humanMarkedValue) {
		super.save(humanMarkedValue);
	}
	
	@Transactional(readOnly = false)
	public void delete(HumanMarkedValue humanMarkedValue) {
		super.delete(humanMarkedValue);
	}
	
}