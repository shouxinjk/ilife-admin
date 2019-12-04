/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.dao.ClearingDao;

/**
 * 清算Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class ClearingService extends CrudService<ClearingDao, Clearing> {
	@Autowired
	private ClearingDao clearingDao;
	
	public List<Clearing> findListByBroker(Map<String,Object> param){
		return clearingDao.findListByBroker(param);
	}
	
	public Clearing get(String id) {
		return super.get(id);
	}
	
	public List<Clearing> findList(Clearing clearing) {
		return super.findList(clearing);
	}
	
	public Page<Clearing> findPage(Page<Clearing> page, Clearing clearing) {
		return super.findPage(page, clearing);
	}
	
	@Transactional(readOnly = false)
	public void save(Clearing clearing) {
		super.save(clearing);
	}
	
	@Transactional(readOnly = false)
	public void delete(Clearing clearing) {
		super.delete(clearing);
	}
	
}