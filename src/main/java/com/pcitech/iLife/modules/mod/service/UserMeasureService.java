/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.dao.UserMeasureDao;

/**
 * 用户属性定义Service
 * @author qchzhu
 * @version 2019-03-13
 */
@Service
@Transactional(readOnly = true)
public class UserMeasureService extends CrudService<UserMeasureDao, UserMeasure> {

	public UserMeasure get(String id) {
		return super.get(id);
	}
	
	public List<UserMeasure> findList(UserMeasure userMeasure) {
		return super.findList(userMeasure);
	}
	
	public Page<UserMeasure> findPage(Page<UserMeasure> page, UserMeasure userMeasure) {
		return super.findPage(page, userMeasure);
	}
	
	@Transactional(readOnly = false)
	public void save(UserMeasure userMeasure) {
		super.save(userMeasure);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserMeasure userMeasure) {
		super.delete(userMeasure);
	}
	
}