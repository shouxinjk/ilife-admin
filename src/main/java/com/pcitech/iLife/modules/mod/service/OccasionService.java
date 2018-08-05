/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.dao.OccasionDao;

/**
 * 外部诱因Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class OccasionService extends CrudService<OccasionDao, Occasion> {

	@Autowired
	private OccasionDao occasionDao;

	public Occasion get(String id) {
		return super.get(id);
	}
	
	public List<Occasion> findList(Occasion occasion) {
		return super.findList(occasion);
	}
	
	public Page<Occasion> findPage(Page<Occasion> page, Occasion occasion) {
		return super.findPage(page, occasion);
	}
	
	@Transactional(readOnly = false)
	public void save(Occasion occasion) {
		super.save(occasion);
	}
	
	@Transactional(readOnly = false)
	public void delete(Occasion occasion) {
		super.delete(occasion);
	}
	
	public String getOccasionNames(String occasionIds) {
		return dao.getOccasionNames(occasionIds);
	}

	@Transactional(readOnly = false)
	public void updateChildrenType(OccasionCategory occasionCategory){
		occasionDao.updateChildrenType(occasionCategory);
	}
}