/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.DictValue;
import com.pcitech.iLife.modules.mod.dao.DictValueDao;

/**
 * 业务字典Service
 * @author chenci
 * @version 2022-10-13
 */
@Service
@Transactional(readOnly = true)
public class DictValueService extends CrudService<DictValueDao, DictValue> {

	public DictValue get(String id) {
		return super.get(id);
	}
	
	public List<DictValue> findList(DictValue dictValue) {
		return super.findList(dictValue);
	}
	
	public Page<DictValue> findPage(Page<DictValue> page, DictValue dictValue) {
		return super.findPage(page, dictValue);
	}
	
	@Transactional(readOnly = false)
	public void save(DictValue dictValue) {
		super.save(dictValue);
	}
	
	@Transactional(readOnly = false)
	public void delete(DictValue dictValue) {
		super.delete(dictValue);
	}
	
}