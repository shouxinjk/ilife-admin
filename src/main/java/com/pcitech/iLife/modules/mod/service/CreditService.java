/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Credit;
import com.pcitech.iLife.modules.mod.dao.CreditDao;

/**
 * 贡献度Service
 * @author ilife
 * @version 2023-01-18
 */
@Service
@Transactional(readOnly = true)
public class CreditService extends CrudService<CreditDao, Credit> {

	public Credit get(String id) {
		return super.get(id);
	}
	
	public List<Credit> findList(Credit credit) {
		return super.findList(credit);
	}
	
	public Page<Credit> findPage(Page<Credit> page, Credit credit) {
		return super.findPage(page, credit);
	}
	
	@Transactional(readOnly = false)
	public void save(Credit credit) {
		super.save(credit);
	}
	
	@Transactional(readOnly = false)
	public void delete(Credit credit) {
		super.delete(credit);
	}
	
}