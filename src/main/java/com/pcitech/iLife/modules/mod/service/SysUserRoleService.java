/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.SysUserRole;
import com.pcitech.iLife.modules.mod.dao.SysUserRoleDao;

/**
 * 用户角色关联Service
 * @author ilife
 * @version 2023-06-30
 */
@Service
@Transactional(readOnly = true)
public class SysUserRoleService extends CrudService<SysUserRoleDao, SysUserRole> {

	public SysUserRole get(String id) {
		return super.get(id);
	}
	
	public List<SysUserRole> findList(SysUserRole sysUserRole) {
		return super.findList(sysUserRole);
	}
	
	public Page<SysUserRole> findPage(Page<SysUserRole> page, SysUserRole sysUserRole) {
		return super.findPage(page, sysUserRole);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUserRole sysUserRole) {
		super.save(sysUserRole);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUserRole sysUserRole) {
		super.delete(sysUserRole);
	}
	
}