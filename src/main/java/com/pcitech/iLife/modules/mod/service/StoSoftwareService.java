/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.StoSoftware;
import com.pcitech.iLife.modules.mod.dao.StoSoftwareDao;

/**
 * SaaS产品Service
 * @author ilife
 * @version 2023-06-18
 */
@Service
@Transactional(readOnly = true)
public class StoSoftwareService extends CrudService<StoSoftwareDao, StoSoftware> {

	public StoSoftware get(String id) {
		return super.get(id);
	}
	
	public List<StoSoftware> findList(StoSoftware stoSoftware) {
		return super.findList(stoSoftware);
	}
	
	public Page<StoSoftware> findPage(Page<StoSoftware> page, StoSoftware stoSoftware) {
		return super.findPage(page, stoSoftware);
	}
	
	@Transactional(readOnly = false)
	public void save(StoSoftware stoSoftware) {
		super.save(stoSoftware);
	}
	
	@Transactional(readOnly = false)
	public void delete(StoSoftware stoSoftware) {
		super.delete(stoSoftware);
	}
	
}