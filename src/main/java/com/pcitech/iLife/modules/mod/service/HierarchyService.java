/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Hierarchy;
import com.pcitech.iLife.modules.mod.dao.HierarchyDao;

/**
 * 社会分层Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class HierarchyService extends CrudService<HierarchyDao, Hierarchy> {

	public Hierarchy get(String id) {
		return super.get(id);
	}
	
	public List<Hierarchy> findList(Hierarchy hierarchy) {
		return super.findList(hierarchy);
	}
	
	public Page<Hierarchy> findPage(Page<Hierarchy> page, Hierarchy hierarchy) {
		return super.findPage(page, hierarchy);
	}
	
	@Transactional(readOnly = false)
	public void save(Hierarchy hierarchy) {
		super.save(hierarchy);
	}
	
	@Transactional(readOnly = false)
	public void delete(Hierarchy hierarchy) {
		super.delete(hierarchy);
	}
	
}