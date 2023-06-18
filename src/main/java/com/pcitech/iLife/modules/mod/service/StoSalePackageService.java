/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.StoSalePackage;
import com.pcitech.iLife.modules.mod.dao.StoSalePackageDao;

/**
 * SaaS套餐Service
 * @author ilife
 * @version 2023-06-18
 */
@Service
@Transactional(readOnly = true)
public class StoSalePackageService extends CrudService<StoSalePackageDao, StoSalePackage> {

	public StoSalePackage get(String id) {
		return super.get(id);
	}
	
	public List<StoSalePackage> findList(StoSalePackage stoSalePackage) {
		return super.findList(stoSalePackage);
	}
	
	public Page<StoSalePackage> findPage(Page<StoSalePackage> page, StoSalePackage stoSalePackage) {
		return super.findPage(page, stoSalePackage);
	}
	
	@Transactional(readOnly = false)
	public void save(StoSalePackage stoSalePackage) {
		super.save(stoSalePackage);
	}
	
	@Transactional(readOnly = false)
	public void delete(StoSalePackage stoSalePackage) {
		super.delete(stoSalePackage);
	}
	
}