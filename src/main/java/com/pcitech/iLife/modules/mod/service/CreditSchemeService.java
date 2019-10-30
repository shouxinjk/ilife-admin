/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.CreditScheme;
import com.pcitech.iLife.modules.mod.dao.CreditSchemeDao;

/**
 * 积分规则Service
 * @author qchzhu
 * @version 2019-10-30
 */
@Service
@Transactional(readOnly = true)
public class CreditSchemeService extends CrudService<CreditSchemeDao, CreditScheme> {

	@Autowired
	CreditSchemeDao creditSchemeDao;
	
	public CreditScheme getByQuery(CreditScheme query) {
		return creditSchemeDao.getByQuery(query);
	}
	
	public CreditScheme get(String id) {
		return super.get(id);
	}
	
	public List<CreditScheme> findList(CreditScheme creditScheme) {
		return super.findList(creditScheme);
	}
	
	public Page<CreditScheme> findPage(Page<CreditScheme> page, CreditScheme creditScheme) {
		return super.findPage(page, creditScheme);
	}
	
	@Transactional(readOnly = false)
	public void save(CreditScheme creditScheme) {
		super.save(creditScheme);
	}
	
	@Transactional(readOnly = false)
	public void delete(CreditScheme creditScheme) {
		super.delete(creditScheme);
	}
	
}