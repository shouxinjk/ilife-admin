/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.CategoryBroker;
import com.pcitech.iLife.modules.mod.dao.CategoryBrokerDao;

/**
 * 类目专家授权Service
 * @author ilife
 * @version 2022-12-27
 */
@Service
@Transactional(readOnly = true)
public class CategoryBrokerService extends CrudService<CategoryBrokerDao, CategoryBroker> {

	public CategoryBroker get(String id) {
		return super.get(id);
	}
	
	public List<CategoryBroker> findList(CategoryBroker categoryBroker) {
		return super.findList(categoryBroker);
	}
	
	public Page<CategoryBroker> findPage(Page<CategoryBroker> page, CategoryBroker categoryBroker) {
		return super.findPage(page, categoryBroker);
	}
	
	@Transactional(readOnly = false)
	public void save(CategoryBroker categoryBroker) {
		super.save(categoryBroker);
	}
	
	@Transactional(readOnly = false)
	public void delete(CategoryBroker categoryBroker) {
		super.delete(categoryBroker);
	}
	
}