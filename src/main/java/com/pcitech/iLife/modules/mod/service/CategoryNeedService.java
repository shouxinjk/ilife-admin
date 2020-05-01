/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.CategoryNeed;
import com.pcitech.iLife.modules.mod.dao.CategoryNeedDao;

/**
 * 品类需要满足Service
 * @author qchzhu
 * @version 2020-04-30
 */
@Service
@Transactional(readOnly = true)
public class CategoryNeedService extends CrudService<CategoryNeedDao, CategoryNeed> {

	public CategoryNeed get(String id) {
		return super.get(id);
	}
	
	public List<CategoryNeed> findList(CategoryNeed categoryNeed) {
		return super.findList(categoryNeed);
	}
	
	public Page<CategoryNeed> findPage(Page<CategoryNeed> page, CategoryNeed categoryNeed) {
		return super.findPage(page, categoryNeed);
	}
	
	@Transactional(readOnly = false)
	public void save(CategoryNeed categoryNeed) {
		super.save(categoryNeed);
	}
	
	@Transactional(readOnly = false)
	public void delete(CategoryNeed categoryNeed) {
		super.delete(categoryNeed);
	}
	
}