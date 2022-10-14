/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Map;

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

	//批量忽略/启用类目相关
	@Transactional(readOnly = false)
	public void batchUpdateCategoryInfo(Map<String,Object> params) {
		dao.batchUpdateCategoryInfo(params);
	}
	
	//根据字典获取标注值列表，已排重
	public List<Map<String,Object>> findDistinctValuesByDict(String dictId){
		return dao.findDistinctValuesByDict(dictId);
	}
	
	//严格类目检查：根据类目（支持null检查）获取指定字典、指定标注值记录是否存在
	public List<DictValue> getByCategoryCheck(DictValue dictValue){
		return dao.getByCategoryCheck(dictValue);
	}
	
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