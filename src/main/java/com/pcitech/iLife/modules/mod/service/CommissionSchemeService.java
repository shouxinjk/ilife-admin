/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.CommissionScheme;
import com.pcitech.iLife.modules.mod.dao.CommissionSchemeDao;

/**
 * 佣金规则设置Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class CommissionSchemeService extends CrudService<CommissionSchemeDao, CommissionScheme> {

	public CommissionScheme get(String id) {
		return super.get(id);
	}
	
	public List<CommissionScheme> findList(CommissionScheme commissionScheme) {
		return super.findList(commissionScheme);
	}
	
	public Page<CommissionScheme> findPage(Page<CommissionScheme> page, CommissionScheme commissionScheme) {
		return super.findPage(page, commissionScheme);
	}
	
	@Transactional(readOnly = false)
	public void save(CommissionScheme commissionScheme) {
		super.save(commissionScheme);
	}
	
	@Transactional(readOnly = false)
	public void delete(CommissionScheme commissionScheme) {
		super.delete(commissionScheme);
	}
	
}