/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Capability;
import com.pcitech.iLife.modules.mod.dao.CapabilityDao;

/**
 * 资本标注Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class CapabilityService extends CrudService<CapabilityDao, Capability> {

	public Capability get(String id) {
		return super.get(id);
	}
	
	public List<Capability> findList(Capability capability) {
		return super.findList(capability);
	}
	
	public Page<Capability> findPage(Page<Capability> page, Capability capability) {
		return super.findPage(page, capability);
	}
	
	@Transactional(readOnly = false)
	public void save(Capability capability) {
		super.save(capability);
	}
	
	@Transactional(readOnly = false)
	public void delete(Capability capability) {
		super.delete(capability);
	}
	
}