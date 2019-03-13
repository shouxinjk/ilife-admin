/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.dao.UserDimensionDao;

/**
 * 用户客观评价Service
 * @author qchzhu
 * @version 2019-03-13
 */
@Service
@Transactional(readOnly = true)
public class UserDimensionService extends CrudService<UserDimensionDao, UserDimension> {

	public UserDimension get(String id) {
		return super.get(id);
	}
	
	public List<UserDimension> findList(UserDimension userDimension) {
		return super.findList(userDimension);
	}
	
	public Page<UserDimension> findPage(Page<UserDimension> page, UserDimension userDimension) {
		return super.findPage(page, userDimension);
	}
	
	@Transactional(readOnly = false)
	public void save(UserDimension userDimension) {
		super.save(userDimension);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserDimension userDimension) {
		super.delete(userDimension);
	}
	
}