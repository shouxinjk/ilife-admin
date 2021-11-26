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
import com.pcitech.iLife.modules.dict.entity.DictMaterial;
import com.pcitech.iLife.modules.dict.dao.DictMaterialDao;

/**
 * 材质字典管理Service
 * @author iLife
 * @version 2021-11-26
 */
@Service
@Transactional(readOnly = true)
public class DictMaterialService extends CrudService<DictMaterialDao, DictMaterial> {

	public DictMaterial get(String id) {
		return super.get(id);
	}
	
	public List<DictMaterial> findList(DictMaterial dictMaterial) {
		return super.findList(dictMaterial);
	}
	
	public Page<DictMaterial> findPage(Page<DictMaterial> page, DictMaterial dictMaterial) {
		return super.findPage(page, dictMaterial);
	}
	
	@Transactional(readOnly = false)
	public void save(DictMaterial dictMaterial) {
		super.save(dictMaterial);
	}
	
	@Transactional(readOnly = false)
	public void delete(DictMaterial dictMaterial) {
		super.delete(dictMaterial);
	}
	
	@Transactional(readOnly = false)
	public void updateMarkedValue(Map<String,Object> params) {
		dao.updateMarkedValue(params);
	}
}