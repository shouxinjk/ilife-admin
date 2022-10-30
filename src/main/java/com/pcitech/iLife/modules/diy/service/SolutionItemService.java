/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.SolutionItem;
import com.pcitech.iLife.modules.diy.dao.SolutionItemDao;

/**
 * 个性定制方案条目Service
 * @author chenci
 * @version 2022-10-29
 */
@Service
@Transactional(readOnly = true)
public class SolutionItemService extends CrudService<SolutionItemDao, SolutionItem> {

	public SolutionItem get(String id) {
		return super.get(id);
	}
	
	public List<SolutionItem> findList(SolutionItem solutionItem) {
		return super.findList(solutionItem);
	}
	
	public Page<SolutionItem> findPage(Page<SolutionItem> page, SolutionItem solutionItem) {
		return super.findPage(page, solutionItem);
	}
	
	@Transactional(readOnly = false)
	public void save(SolutionItem solutionItem) {
		super.save(solutionItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(SolutionItem solutionItem) {
		super.delete(solutionItem);
	}
	
}