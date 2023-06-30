/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.IntPricePlanPermission;
import com.pcitech.iLife.modules.mod.dao.IntPricePlanPermissionDao;

/**
 * 订阅计划授权Service
 * @author ilife
 * @version 2023-06-30
 */
@Service
@Transactional(readOnly = true)
public class IntPricePlanPermissionService extends CrudService<IntPricePlanPermissionDao, IntPricePlanPermission> {

	public IntPricePlanPermission get(String id) {
		return super.get(id);
	}
	
	public List<IntPricePlanPermission> findList(IntPricePlanPermission intPricePlanPermission) {
		return super.findList(intPricePlanPermission);
	}
	
	public Page<IntPricePlanPermission> findPage(Page<IntPricePlanPermission> page, IntPricePlanPermission intPricePlanPermission) {
		return super.findPage(page, intPricePlanPermission);
	}
	
	@Transactional(readOnly = false)
	public void save(IntPricePlanPermission intPricePlanPermission) {
		super.save(intPricePlanPermission);
	}
	
	@Transactional(readOnly = false)
	public void delete(IntPricePlanPermission intPricePlanPermission) {
		super.delete(intPricePlanPermission);
	}
	
}