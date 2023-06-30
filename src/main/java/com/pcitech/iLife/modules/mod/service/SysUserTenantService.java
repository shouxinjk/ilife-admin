/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.SysUserTenant;
import com.pcitech.iLife.modules.mod.dao.SysUserTenantDao;

/**
 * 用户租户关联Service
 * @author ilife
 * @version 2023-06-30
 */
@Service
@Transactional(readOnly = true)
public class SysUserTenantService extends CrudService<SysUserTenantDao, SysUserTenant> {

	public SysUserTenant get(String id) {
		return super.get(id);
	}
	
	public List<SysUserTenant> findList(SysUserTenant sysUserTenant) {
		return super.findList(sysUserTenant);
	}
	
	public Page<SysUserTenant> findPage(Page<SysUserTenant> page, SysUserTenant sysUserTenant) {
		return super.findPage(page, sysUserTenant);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUserTenant sysUserTenant) {
		super.save(sysUserTenant);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUserTenant sysUserTenant) {
		super.delete(sysUserTenant);
	}
	
}