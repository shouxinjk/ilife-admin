/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.LinkTemplate;
import com.pcitech.iLife.modules.mod.dao.LinkTemplateDao;

/**
 * 链接模板Service
 * @author qchzhu
 * @version 2021-09-01
 */
@Service
@Transactional(readOnly = true)
public class LinkTemplateService extends CrudService<LinkTemplateDao, LinkTemplate> {

	public LinkTemplate get(String id) {
		return super.get(id);
	}
	
	public List<LinkTemplate> findList(LinkTemplate linkTemplate) {
		return super.findList(linkTemplate);
	}
	
	public Page<LinkTemplate> findPage(Page<LinkTemplate> page, LinkTemplate linkTemplate) {
		return super.findPage(page, linkTemplate);
	}
	
	@Transactional(readOnly = false)
	public void save(LinkTemplate linkTemplate) {
		super.save(linkTemplate);
	}
	
	@Transactional(readOnly = false)
	public void delete(LinkTemplate linkTemplate) {
		super.delete(linkTemplate);
	}
	
}