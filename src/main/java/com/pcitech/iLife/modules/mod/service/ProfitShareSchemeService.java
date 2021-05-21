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
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.dao.ProfitShareSchemeDao;

/**
 * 分润规则Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class ProfitShareSchemeService extends CrudService<ProfitShareSchemeDao, ProfitShareScheme> {
	@Autowired
	ProfitShareSchemeDao profitShareSchemeDao;
	
	public ProfitShareScheme getByQuery(ProfitShareScheme profitShareScheme) {
		return profitShareSchemeDao.getByQuery(profitShareScheme);
	}
	
	//查询所有支持的电商平台
	public List<String> getProfitSources(){
		return profitShareSchemeDao.getProfitSources();
	}
	
	public ProfitShareScheme get(String id) {
		return super.get(id);
	}
	
	public List<ProfitShareScheme> findList(ProfitShareScheme profitShareScheme) {
		return super.findList(profitShareScheme);
	}
	
	public Page<ProfitShareScheme> findPage(Page<ProfitShareScheme> page, ProfitShareScheme profitShareScheme) {
		return super.findPage(page, profitShareScheme);
	}
	
	@Transactional(readOnly = false)
	public void save(ProfitShareScheme profitShareScheme) {
		super.save(profitShareScheme);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProfitShareScheme profitShareScheme) {
		super.delete(profitShareScheme);
	}
	
}