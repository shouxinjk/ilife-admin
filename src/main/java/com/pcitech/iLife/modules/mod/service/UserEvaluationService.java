/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.mod.dao.UserEvaluationDao;

/**
 * 用户主观评价Service
 * @author qchzhu
 * @version 2019-03-13
 */
@Service
@Transactional(readOnly = true)
public class UserEvaluationService extends CrudService<UserEvaluationDao, UserEvaluation> {

	public UserEvaluation get(String id) {
		return super.get(id);
	}
	
	public List<UserEvaluation> findList(UserEvaluation userEvaluation) {
		return super.findList(userEvaluation);
	}
	
	public Page<UserEvaluation> findPage(Page<UserEvaluation> page, UserEvaluation userEvaluation) {
		return super.findPage(page, userEvaluation);
	}
	
	@Transactional(readOnly = false)
	public void save(UserEvaluation userEvaluation) {
		super.save(userEvaluation);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserEvaluation userEvaluation) {
		super.delete(userEvaluation);
	}
	
}