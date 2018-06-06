/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.ope.entity.Behavior;
import com.pcitech.iLife.modules.ope.dao.BehaviorDao;

/**
 * 用户行为Service
 * @author chenci
 * @version 2017-09-28
 */
@Service
@Transactional(readOnly = true)
public class BehaviorService extends CrudService<BehaviorDao, Behavior> {

	public Behavior get(String id) {
		return super.get(id);
	}
	
	public List<Behavior> findList(Behavior behavior) {
		return super.findList(behavior);
	}
	
	public Page<Behavior> findPage(Page<Behavior> page, Behavior behavior) {
		return super.findPage(page, behavior);
	}
	
	@Transactional(readOnly = false)
	public void save(Behavior behavior) {
		super.save(behavior);
	}
	
	@Transactional(readOnly = false)
	public void delete(Behavior behavior) {
		super.delete(behavior);
	}
	
}