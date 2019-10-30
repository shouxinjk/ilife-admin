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
import com.pcitech.iLife.modules.mod.entity.CpsLinkScheme;
import com.pcitech.iLife.modules.mod.dao.CpsLinkSchemeDao;

/**
 * CPS链接规则Service
 * @author qchzhu
 * @version 2019-10-30
 */
@Service
@Transactional(readOnly = true)
public class CpsLinkSchemeService extends CrudService<CpsLinkSchemeDao, CpsLinkScheme> {

	@Autowired
	private CpsLinkSchemeDao cpsLinkSchemeDao;
	public CpsLinkScheme getByQuery(CpsLinkScheme query) {
		return cpsLinkSchemeDao.getByQuery(query);
	}
	
	public CpsLinkScheme get(String id) {
		return super.get(id);
	}
	
	public List<CpsLinkScheme> findList(CpsLinkScheme cpsLinkScheme) {
		return super.findList(cpsLinkScheme);
	}
	
	public Page<CpsLinkScheme> findPage(Page<CpsLinkScheme> page, CpsLinkScheme cpsLinkScheme) {
		return super.findPage(page, cpsLinkScheme);
	}
	
	@Transactional(readOnly = false)
	public void save(CpsLinkScheme cpsLinkScheme) {
		super.save(cpsLinkScheme);
	}
	
	@Transactional(readOnly = false)
	public void delete(CpsLinkScheme cpsLinkScheme) {
		super.delete(cpsLinkScheme);
	}
	
}