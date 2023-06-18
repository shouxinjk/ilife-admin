/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.IntPackagePricePlan;
import com.pcitech.iLife.modules.mod.dao.IntPackagePricePlanDao;

/**
 * 订阅套餐明细Service
 * @author ilife
 * @version 2023-06-18
 */
@Service
@Transactional(readOnly = true)
public class IntPackagePricePlanService extends CrudService<IntPackagePricePlanDao, IntPackagePricePlan> {

	public IntPackagePricePlan get(String id) {
		return super.get(id);
	}
	
	public List<IntPackagePricePlan> findList(IntPackagePricePlan intPackagePricePlan) {
		return super.findList(intPackagePricePlan);
	}
	
	public Page<IntPackagePricePlan> findPage(Page<IntPackagePricePlan> page, IntPackagePricePlan intPackagePricePlan) {
		return super.findPage(page, intPackagePricePlan);
	}
	
	@Transactional(readOnly = false)
	public void save(IntPackagePricePlan intPackagePricePlan) {
		super.save(intPackagePricePlan);
	}
	
	@Transactional(readOnly = false)
	public void delete(IntPackagePricePlan intPackagePricePlan) {
		super.delete(intPackagePricePlan);
	}
	
}