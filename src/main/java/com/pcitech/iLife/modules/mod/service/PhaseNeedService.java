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
import com.pcitech.iLife.modules.mod.entity.PhaseNeed;
import com.pcitech.iLife.modules.mod.dao.PhaseNeedDao;

/**
 * 阶段需要构成Service
 * @author ilife
 * @version 2022-02-09
 */
@Service
@Transactional(readOnly = true)
public class PhaseNeedService extends CrudService<PhaseNeedDao, PhaseNeed> {
	@Transactional(readOnly = false)
	public void updateWeight(Map<String,Object> params) {
		dao.updateWeight(params);
	}
	
	public PhaseNeed get(String id) {
		return super.get(id);
	}
	
	public List<PhaseNeed> findList(PhaseNeed phaseNeed) {
		return super.findList(phaseNeed);
	}
	
	public Page<PhaseNeed> findPage(Page<PhaseNeed> page, PhaseNeed phaseNeed) {
		return super.findPage(page, phaseNeed);
	}
	
	@Transactional(readOnly = false)
	public void save(PhaseNeed phaseNeed) {
		super.save(phaseNeed);
	}
	
	@Transactional(readOnly = false)
	public void delete(PhaseNeed phaseNeed) {
		super.delete(phaseNeed);
	}
	
}