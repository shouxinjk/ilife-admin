/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.UserEvaluationDimension;
import com.pcitech.iLife.modules.mod.dao.UserEvaluationDimensionDao;

/**
 * 用户主观评价-维度Service
 * @author qchzhu
 * @version 2019-03-13
 */
@Service
@Transactional(readOnly = true)
public class UserEvaluationDimensionService extends CrudService<UserEvaluationDimensionDao, UserEvaluationDimension> {

	public UserEvaluationDimension get(String id) {
		return super.get(id);
	}
	
	public List<UserEvaluationDimension> findList(UserEvaluationDimension userEvaluationDimension) {
		return super.findList(userEvaluationDimension);
	}
	
	public Page<UserEvaluationDimension> findPage(Page<UserEvaluationDimension> page, UserEvaluationDimension userEvaluationDimension) {
		return super.findPage(page, userEvaluationDimension);
	}
	
	@Transactional(readOnly = false)
	public void save(UserEvaluationDimension userEvaluationDimension) {
		super.save(userEvaluationDimension);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserEvaluationDimension userEvaluationDimension) {
		super.delete(userEvaluationDimension);
	}
	
}