/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Tags;
import com.pcitech.iLife.modules.mod.dao.TagsDao;

/**
 * 标签Service
 * @author chenci
 * @version 2017-09-27
 */
@Service
@Transactional(readOnly = true)
public class TagsService extends CrudService<TagsDao, Tags> {

	public Tags get(String id) {
		return super.get(id);
	}
	
	public List<Tags> findList(Tags tags) {
		return super.findList(tags);
	}
	
	public Page<Tags> findPage(Page<Tags> page, Tags tags) {
		return super.findPage(page, tags);
	}
	
	@Transactional(readOnly = false)
	public void save(Tags tags) {
		super.save(tags);
	}
	
	@Transactional(readOnly = false)
	public void delete(Tags tags) {
		super.delete(tags);
	}
	
}