/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.IntTenantSoftware;
import com.pcitech.iLife.modules.mod.dao.IntTenantSoftwareDao;

/**
 * 租户订阅产品Service
 * @author ilife
 * @version 2023-06-18
 */
@Service
@Transactional(readOnly = true)
public class IntTenantSoftwareService extends CrudService<IntTenantSoftwareDao, IntTenantSoftware> {

	public IntTenantSoftware get(String id) {
		return super.get(id);
	}
	
	public List<IntTenantSoftware> findList(IntTenantSoftware intTenantSoftware) {
		return super.findList(intTenantSoftware);
	}
	
	public Page<IntTenantSoftware> findPage(Page<IntTenantSoftware> page, IntTenantSoftware intTenantSoftware) {
		return super.findPage(page, intTenantSoftware);
	}
	
	@Transactional(readOnly = false)
	public void save(IntTenantSoftware intTenantSoftware) {
		super.save(intTenantSoftware);
	}
	
	@Transactional(readOnly = false)
	public void delete(IntTenantSoftware intTenantSoftware) {
		super.delete(intTenantSoftware);
	}
	
}