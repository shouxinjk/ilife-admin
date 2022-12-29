/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.ope.entity.HumanMarkedDict;
import com.pcitech.iLife.modules.ope.dao.HumanMarkedDictDao;

/**
 * 用户标注字典Service
 * @author ilife
 * @version 2022-12-29
 */
@Service
@Transactional(readOnly = true)
public class HumanMarkedDictService extends CrudService<HumanMarkedDictDao, HumanMarkedDict> {

	public HumanMarkedDict get(String id) {
		return super.get(id);
	}
	
	public List<HumanMarkedDict> findList(HumanMarkedDict humanMarkedDict) {
		return super.findList(humanMarkedDict);
	}
	
	public Page<HumanMarkedDict> findPage(Page<HumanMarkedDict> page, HumanMarkedDict humanMarkedDict) {
		return super.findPage(page, humanMarkedDict);
	}
	
	@Transactional(readOnly = false)
	public void save(HumanMarkedDict humanMarkedDict) {
		super.save(humanMarkedDict);
	}
	
	@Transactional(readOnly = false)
	public void delete(HumanMarkedDict humanMarkedDict) {
		super.delete(humanMarkedDict);
	}
	
}