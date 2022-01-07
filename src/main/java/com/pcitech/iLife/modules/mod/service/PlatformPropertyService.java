/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.PlatformProperty;
import com.pcitech.iLife.modules.mod.dao.PlatformPropertyDao;

/**
 * 电商平台属性映射Service
 * @author ilife
 * @version 2022-01-07
 */
@Service
@Transactional(readOnly = true)
public class PlatformPropertyService extends CrudService<PlatformPropertyDao, PlatformProperty> {

	public PlatformProperty get(String id) {
		return super.get(id);
	}
	
	public List<PlatformProperty> findList(PlatformProperty platformProperty) {
		return super.findList(platformProperty);
	}
	
	public Page<PlatformProperty> findPage(Page<PlatformProperty> page, PlatformProperty platformProperty) {
		return super.findPage(page, platformProperty);
	}
	
	@Transactional(readOnly = false)
	public void save(PlatformProperty platformProperty) {
		super.save(platformProperty);
	}
	
	@Transactional(readOnly = false)
	public void delete(PlatformProperty platformProperty) {
		super.delete(platformProperty);
	}
	
}