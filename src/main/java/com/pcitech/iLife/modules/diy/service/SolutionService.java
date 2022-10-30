/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.Solution;
import com.pcitech.iLife.modules.diy.dao.SolutionDao;

/**
 * 个性定制方案Service
 * @author chenci
 * @version 2022-10-29
 */
@Service
@Transactional(readOnly = true)
public class SolutionService extends CrudService<SolutionDao, Solution> {

	public Solution get(String id) {
		return super.get(id);
	}
	
	public List<Solution> findList(Solution solution) {
		return super.findList(solution);
	}
	
	public Page<Solution> findPage(Page<Solution> page, Solution solution) {
		return super.findPage(page, solution);
	}
	
	@Transactional(readOnly = false)
	public void save(Solution solution) {
		super.save(solution);
	}
	
	@Transactional(readOnly = false)
	public void delete(Solution solution) {
		super.delete(solution);
	}
	
}