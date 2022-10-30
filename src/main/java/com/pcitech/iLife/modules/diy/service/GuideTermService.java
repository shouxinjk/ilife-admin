/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.GuideTerm;
import com.pcitech.iLife.modules.diy.dao.GuideTermDao;

/**
 * 个性化定制指南条目Service
 * @author chenci
 * @version 2022-10-29
 */
@Service
@Transactional(readOnly = true)
public class GuideTermService extends CrudService<GuideTermDao, GuideTerm> {

	public GuideTerm get(String id) {
		return super.get(id);
	}
	
	public List<GuideTerm> findList(GuideTerm guideTerm) {
		return super.findList(guideTerm);
	}
	
	public Page<GuideTerm> findPage(Page<GuideTerm> page, GuideTerm guideTerm) {
		return super.findPage(page, guideTerm);
	}
	
	@Transactional(readOnly = false)
	public void save(GuideTerm guideTerm) {
		super.save(guideTerm);
	}
	
	@Transactional(readOnly = false)
	public void delete(GuideTerm guideTerm) {
		super.delete(guideTerm);
	}
	
}