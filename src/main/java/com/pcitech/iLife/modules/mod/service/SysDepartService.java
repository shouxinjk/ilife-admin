/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.SysDepart;
import com.pcitech.iLife.modules.mod.dao.SysDepartDao;

/**
 * 租户部门Service
 * @author ilife
 * @version 2023-06-30
 */
@Service
@Transactional(readOnly = true)
public class SysDepartService extends CrudService<SysDepartDao, SysDepart> {

	public SysDepart get(String id) {
		return super.get(id);
	}
	
	public List<SysDepart> findList(SysDepart sysDepart) {
		return super.findList(sysDepart);
	}
	
	public Page<SysDepart> findPage(Page<SysDepart> page, SysDepart sysDepart) {
		return super.findPage(page, sysDepart);
	}
	
	@Transactional(readOnly = false)
	public void save(SysDepart sysDepart) {
		super.save(sysDepart);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysDepart sysDepart) {
		super.delete(sysDepart);
	}
	
}