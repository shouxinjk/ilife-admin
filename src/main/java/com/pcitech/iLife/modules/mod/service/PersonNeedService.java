/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.PersonNeed;
import com.pcitech.iLife.modules.mod.dao.PersonNeedDao;

/**
 * 用户需要构成Service
 * @author ilife
 * @version 2022-02-17
 */
@Service
@Transactional(readOnly = true)
public class PersonNeedService extends CrudService<PersonNeedDao, PersonNeed> {

	@Transactional(readOnly = false)
	public void updateWeight(Map<String,Object> params) {
		dao.updateWeight(params);
	}
	
	public PersonNeed get(String id) {
		return super.get(id);
	}
	
	public List<PersonNeed> findList(PersonNeed personNeed) {
		return super.findList(personNeed);
	}
	
	public Page<PersonNeed> findPage(Page<PersonNeed> page, PersonNeed personNeed) {
		return super.findPage(page, personNeed);
	}
	
	@Transactional(readOnly = false)
	public void save(PersonNeed personNeed) {
		super.save(personNeed);
	}
	
	@Transactional(readOnly = false)
	public void delete(PersonNeed personNeed) {
		super.delete(personNeed);
	}
	
}