/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.IntPricePlanResource;
import com.pcitech.iLife.modules.mod.dao.IntPricePlanResourceDao;

/**
 * 订阅计划资源Service
 * @author ilife
 * @version 2023-06-30
 */
@Service
@Transactional(readOnly = true)
public class IntPricePlanResourceService extends CrudService<IntPricePlanResourceDao, IntPricePlanResource> {

	public IntPricePlanResource get(String id) {
		return super.get(id);
	}
	
	public List<IntPricePlanResource> findList(IntPricePlanResource intPricePlanResource) {
		return super.findList(intPricePlanResource);
	}
	
	public Page<IntPricePlanResource> findPage(Page<IntPricePlanResource> page, IntPricePlanResource intPricePlanResource) {
		return super.findPage(page, intPricePlanResource);
	}
	
	@Transactional(readOnly = false)
	public void save(IntPricePlanResource intPricePlanResource) {
		super.save(intPricePlanResource);
	}
	
	@Transactional(readOnly = false)
	public void delete(IntPricePlanResource intPricePlanResource) {
		super.delete(intPricePlanResource);
	}
	
}