/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.dao.MeasureDao;

/**
 * 商品属性Service
 * @author chenci
 * @version 2017-09-22
 */
@Service
@Transactional(readOnly = true)
public class MeasureService extends CrudService<MeasureDao, Measure> {

	public Measure get(String id) {
		return super.get(id);
	}
	
	public List<Measure> findList(Measure measure) {
		return super.findList(measure);
	}
	
	public List<Measure> findByCategory(String category) {
		return dao.findByCategoryId(category);
	}
	
	public Page<Measure> findPage(Page<Measure> page, Measure measure) {
		return super.findPage(page, measure);
	}
	
	@Transactional(readOnly = false)
	public void save(Measure measure) {
		super.save(measure);
	}
	
	@Transactional(readOnly = false)
	public void delete(Measure measure) {
		super.delete(measure);
	}
	
}