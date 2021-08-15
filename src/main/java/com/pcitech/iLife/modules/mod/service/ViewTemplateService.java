/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.ViewTemplate;
import com.pcitech.iLife.modules.mod.dao.ViewTemplateDao;

/**
 * 模型展示模板Service
 * @author qchzhu
 * @version 2021-08-15
 */
@Service
@Transactional(readOnly = true)
public class ViewTemplateService extends CrudService<ViewTemplateDao, ViewTemplate> {

	@Autowired
	ViewTemplateDao viewTemplateDao;
	
	public ViewTemplate get(String id) {
		return super.get(id);
	}
	
	public ViewTemplate getByType(String type) {
		return viewTemplateDao.getByType(type);
	}
	
	public List<ViewTemplate> findList(ViewTemplate viewTemplate) {
		return super.findList(viewTemplate);
	}
	
	public Page<ViewTemplate> findPage(Page<ViewTemplate> page, ViewTemplate viewTemplate) {
		return super.findPage(page, viewTemplate);
	}
	
	@Transactional(readOnly = false)
	public void save(ViewTemplate viewTemplate) {
		super.save(viewTemplate);
	}
	
	@Transactional(readOnly = false)
	public void delete(ViewTemplate viewTemplate) {
		super.delete(viewTemplate);
	}
	
}