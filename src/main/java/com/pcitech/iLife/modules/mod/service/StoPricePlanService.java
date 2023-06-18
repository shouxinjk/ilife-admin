/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.StoPricePlan;
import com.pcitech.iLife.modules.mod.dao.StoPricePlanDao;

/**
 * 产品订阅计划Service
 * @author ilife
 * @version 2023-06-18
 */
@Service
@Transactional(readOnly = true)
public class StoPricePlanService extends CrudService<StoPricePlanDao, StoPricePlan> {

	public StoPricePlan get(String id) {
		return super.get(id);
	}
	
	public List<StoPricePlan> findList(StoPricePlan stoPricePlan) {
		return super.findList(stoPricePlan);
	}
	
	public Page<StoPricePlan> findPage(Page<StoPricePlan> page, StoPricePlan stoPricePlan) {
		return super.findPage(page, stoPricePlan);
	}
	
	@Transactional(readOnly = false)
	public void save(StoPricePlan stoPricePlan) {
		super.save(stoPricePlan);
	}
	
	@Transactional(readOnly = false)
	public void delete(StoPricePlan stoPricePlan) {
		super.delete(stoPricePlan);
	}
	
}