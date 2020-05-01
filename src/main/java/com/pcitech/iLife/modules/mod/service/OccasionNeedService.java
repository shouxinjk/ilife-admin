/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.OccasionNeed;
import com.pcitech.iLife.modules.mod.dao.OccasionNeedDao;

/**
 * 诱因对需要的影响Service
 * @author qchzhu
 * @version 2020-04-30
 */
@Service
@Transactional(readOnly = true)
public class OccasionNeedService extends CrudService<OccasionNeedDao, OccasionNeed> {

	public OccasionNeed get(String id) {
		return super.get(id);
	}
	
	public List<OccasionNeed> findList(OccasionNeed occasionNeed) {
		return super.findList(occasionNeed);
	}
	
	public Page<OccasionNeed> findPage(Page<OccasionNeed> page, OccasionNeed occasionNeed) {
		return super.findPage(page, occasionNeed);
	}
	
	@Transactional(readOnly = false)
	public void save(OccasionNeed occasionNeed) {
		super.save(occasionNeed);
	}
	
	@Transactional(readOnly = false)
	public void delete(OccasionNeed occasionNeed) {
		super.delete(occasionNeed);
	}
	
}