/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.SysTenant;
import com.pcitech.iLife.modules.mod.dao.SysTenantDao;

/**
 * 租户Service
 * @author ilife
 * @version 2023-06-18
 */
@Service
@Transactional(readOnly = true)
public class SysTenantService extends CrudService<SysTenantDao, SysTenant> {

	public SysTenant get(String id) {
		return super.get(id);
	}
	
	public List<SysTenant> findList(SysTenant sysTenant) {
		return super.findList(sysTenant);
	}
	
	public Page<SysTenant> findPage(Page<SysTenant> page, SysTenant sysTenant) {
		return super.findPage(page, sysTenant);
	}
	
	@Transactional(readOnly = false)
	public void save(SysTenant sysTenant) {
		super.save(sysTenant);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysTenant sysTenant) {
		super.delete(sysTenant);
	}
	
}