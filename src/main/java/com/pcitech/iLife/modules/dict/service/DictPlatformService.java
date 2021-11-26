/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.dict.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.dict.entity.DictPlatform;
import com.pcitech.iLife.modules.dict.dao.DictPlatformDao;

/**
 * 电商平台字典管理Service
 * @author iLife
 * @version 2021-11-26
 */
@Service
@Transactional(readOnly = true)
public class DictPlatformService extends CrudService<DictPlatformDao, DictPlatform> {

	public DictPlatform get(String id) {
		return super.get(id);
	}
	
	public List<DictPlatform> findList(DictPlatform dictPlatform) {
		return super.findList(dictPlatform);
	}
	
	public Page<DictPlatform> findPage(Page<DictPlatform> page, DictPlatform dictPlatform) {
		return super.findPage(page, dictPlatform);
	}
	
	@Transactional(readOnly = false)
	public void save(DictPlatform dictPlatform) {
		super.save(dictPlatform);
	}
	
	@Transactional(readOnly = false)
	public void delete(DictPlatform dictPlatform) {
		super.delete(dictPlatform);
	}
	
	@Transactional(readOnly = false)
	public void updateMarkedValue(Map<String,Object> params) {
		dao.updateMarkedValue(params);
	}
}