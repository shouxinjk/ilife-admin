/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.SysRole;
import com.pcitech.iLife.modules.mod.dao.SysRoleDao;

/**
 * 系统角色Service
 * @author ilife
 * @version 2023-06-30
 */
@Service
@Transactional(readOnly = true)
public class SysRoleService extends CrudService<SysRoleDao, SysRole> {

	public SysRole get(String id) {
		return super.get(id);
	}
	
	public List<SysRole> findList(SysRole sysRole) {
		return super.findList(sysRole);
	}
	
	public Page<SysRole> findPage(Page<SysRole> page, SysRole sysRole) {
		return super.findPage(page, sysRole);
	}
	
	@Transactional(readOnly = false)
	public void save(SysRole sysRole) {
		super.save(sysRole);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysRole sysRole) {
		super.delete(sysRole);
	}
	
}