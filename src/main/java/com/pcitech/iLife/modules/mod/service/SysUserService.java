/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.SysUser;
import com.pcitech.iLife.modules.mod.dao.SysUserDao;

/**
 * SaaS用户Service
 * @author ilife
 * @version 2023-06-30
 */
@Service
@Transactional(readOnly = true)
public class SysUserService extends CrudService<SysUserDao, SysUser> {

	public SysUser get(String id) {
		return super.get(id);
	}
	
	public List<SysUser> findList(SysUser sysUser) {
		return super.findList(sysUser);
	}
	
	public Page<SysUser> findPage(Page<SysUser> page, SysUser sysUser) {
		return super.findPage(page, sysUser);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUser sysUser) {
		super.save(sysUser);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUser sysUser) {
		super.delete(sysUser);
	}
	
}