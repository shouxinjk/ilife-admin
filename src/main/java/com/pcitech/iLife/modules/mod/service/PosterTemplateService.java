/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.PosterTemplate;
import com.pcitech.iLife.modules.mod.dao.PosterTemplateDao;

/**
 * 海报模板管理Service
 * @author ilife
 * @version 2021-12-28
 */
@Service
@Transactional(readOnly = true)
public class PosterTemplateService extends CrudService<PosterTemplateDao, PosterTemplate> {

	public PosterTemplate get(String id) {
		return super.get(id);
	}
	
	public List<PosterTemplate> findItemList(String categoryId) {
		return dao.findItemList(categoryId);
	}
	
	public List<PosterTemplate> findBoardList() {
		return dao.findBoardList();
	}
	
	public List<PosterTemplate> findList(PosterTemplate posterTemplate) {
		return super.findList(posterTemplate);
	}
	
	public Page<PosterTemplate> findPage(Page<PosterTemplate> page, PosterTemplate posterTemplate) {
		return super.findPage(page, posterTemplate);
	}
	
	@Transactional(readOnly = false)
	public void save(PosterTemplate posterTemplate) {
		super.save(posterTemplate);
	}
	
	@Transactional(readOnly = false)
	public void delete(PosterTemplate posterTemplate) {
		super.delete(posterTemplate);
	}
	
}