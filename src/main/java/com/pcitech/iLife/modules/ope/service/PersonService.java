/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.ope.entity.Person;
import com.pcitech.iLife.modules.ope.dao.PersonDao;

/**
 * 用户Service
 * @author chenci
 * @version 2017-09-27
 */
@Service
@Transactional(readOnly = true)
public class PersonService extends CrudService<PersonDao, Person> {

	public Person get(String id) {
		return super.get(id);
	}
	
	public List<Person> findList(Person person) {
		return super.findList(person);
	}
	
	public Page<Person> findPage(Page<Person> page, Person person) {
		return super.findPage(page, person);
	}
	
	@Transactional(readOnly = false)
	public void save(Person person) {
		super.save(person);
	}
	
	@Transactional(readOnly = false)
	public void delete(Person person) {
		super.delete(person);
	}
	
}