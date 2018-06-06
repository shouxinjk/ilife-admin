/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Capital;
import com.pcitech.iLife.modules.mod.dao.CapitalDao;

/**
 * 资本类型Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class CapitalService extends CrudService<CapitalDao, Capital> {

	public Capital get(String id) {
		return super.get(id);
	}
	
	public List<Capital> findList(Capital capital) {
		return super.findList(capital);
	}
	
	public Page<Capital> findPage(Page<Capital> page, Capital capital) {
		return super.findPage(page, capital);
	}
	
	@Transactional(readOnly = false)
	public void save(Capital capital) {
		super.save(capital);
	}
	
	@Transactional(readOnly = false)
	public void delete(Capital capital) {
		super.delete(capital);
	}
	
}