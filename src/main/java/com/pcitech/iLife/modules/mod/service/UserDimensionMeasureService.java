/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.UserDimensionMeasure;
import com.pcitech.iLife.modules.mod.dao.UserDimensionMeasureDao;

/**
 * 用户客观评价-属性Service
 * @author qchzhu
 * @version 2019-03-13
 */
@Service
@Transactional(readOnly = true)
public class UserDimensionMeasureService extends CrudService<UserDimensionMeasureDao, UserDimensionMeasure> {

	public UserDimensionMeasure get(String id) {
		return super.get(id);
	}
	
	public List<UserDimensionMeasure> findList(UserDimensionMeasure userDimensionMeasure) {
		return super.findList(userDimensionMeasure);
	}
	
	public Page<UserDimensionMeasure> findPage(Page<UserDimensionMeasure> page, UserDimensionMeasure userDimensionMeasure) {
		return super.findPage(page, userDimensionMeasure);
	}
	
	@Transactional(readOnly = false)
	public void save(UserDimensionMeasure userDimensionMeasure) {
		super.save(userDimensionMeasure);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserDimensionMeasure userDimensionMeasure) {
		super.delete(userDimensionMeasure);
	}
	
}