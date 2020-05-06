/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.PersonaMeasure;
import com.pcitech.iLife.modules.mod.dao.PersonaMeasureDao;

/**
 * 用户属性VALS标注Service
 * @author qchzhu
 * @version 2020-05-06
 */
@Service
@Transactional(readOnly = true)
public class PersonaMeasureService extends CrudService<PersonaMeasureDao, PersonaMeasure> {

	public PersonaMeasure get(String id) {
		return super.get(id);
	}
	
	public List<PersonaMeasure> findList(PersonaMeasure personaMeasure) {
		return super.findList(personaMeasure);
	}
	
	public Page<PersonaMeasure> findPage(Page<PersonaMeasure> page, PersonaMeasure personaMeasure) {
		return super.findPage(page, personaMeasure);
	}
	
	@Transactional(readOnly = false)
	public void save(PersonaMeasure personaMeasure) {
		super.save(personaMeasure);
	}
	
	@Transactional(readOnly = false)
	public void delete(PersonaMeasure personaMeasure) {
		super.delete(personaMeasure);
	}
	
}