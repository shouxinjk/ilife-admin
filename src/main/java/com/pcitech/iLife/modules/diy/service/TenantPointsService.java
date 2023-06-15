/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.TenantPoints;
import com.pcitech.iLife.modules.diy.dao.TenantPointsDao;

/**
 * 租户余额信息Service
 * @author ilife
 * @version 2023-06-15
 */
@Service
@Transactional(readOnly = true)
public class TenantPointsService extends CrudService<TenantPointsDao, TenantPoints> {

	public boolean updatePoints(TenantPoints tenantPoints) {
		try {
			dao.updatePoints(tenantPoints);
			return true;
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public TenantPoints get(String id) {
		return super.get(id);
	}
	
	public List<TenantPoints> findList(TenantPoints tenantPoints) {
		return super.findList(tenantPoints);
	}
	
	public Page<TenantPoints> findPage(Page<TenantPoints> page, TenantPoints tenantPoints) {
		return super.findPage(page, tenantPoints);
	}
	
	@Transactional(readOnly = false)
	public void save(TenantPoints tenantPoints) {
		super.save(tenantPoints);
	}
	
	@Transactional(readOnly = false)
	public void delete(TenantPoints tenantPoints) {
		super.delete(tenantPoints);
	}
	
}