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
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;
import com.pcitech.iLife.modules.mod.dao.PersonaNeedDao;

/**
 * 画像需要构成Service
 * @author qchzhu
 * @version 2020-04-30
 */
@Service
@Transactional(readOnly = true)
public class PersonaNeedService extends CrudService<PersonaNeedDao, PersonaNeed> {

	@Transactional(readOnly = false)
	public void updateWeight(Map<String,Object> params) {
		dao.updateWeight(params);
	}
	
	public PersonaNeed get(String id) {
		return super.get(id);
	}
	
	public List<PersonaNeed> findList(PersonaNeed personaNeed) {
		return super.findList(personaNeed);
	}
	
	public Page<PersonaNeed> findPage(Page<PersonaNeed> page, PersonaNeed personaNeed) {
		return super.findPage(page, personaNeed);
	}
	
	@Transactional(readOnly = false)
	public void save(PersonaNeed personaNeed) {
		super.save(personaNeed);
	}
	
	@Transactional(readOnly = false)
	public void delete(PersonaNeed personaNeed) {
		super.delete(personaNeed);
	}
	
}