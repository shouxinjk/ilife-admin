/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.GuideTermItem;
import com.pcitech.iLife.modules.diy.dao.GuideTermItemDao;

/**
 * 指南规则条目关联Service
 * @author chenci
 * @version 2022-10-29
 */
@Service
@Transactional(readOnly = true)
public class GuideTermItemService extends CrudService<GuideTermItemDao, GuideTermItem> {

	public GuideTermItem get(String id) {
		return super.get(id);
	}
	
	public List<GuideTermItem> findList(GuideTermItem guideTermItem) {
		return super.findList(guideTermItem);
	}
	
	public Page<GuideTermItem> findPage(Page<GuideTermItem> page, GuideTermItem guideTermItem) {
		return super.findPage(page, guideTermItem);
	}
	
	@Transactional(readOnly = false)
	public void save(GuideTermItem guideTermItem) {
		super.save(guideTermItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(GuideTermItem guideTermItem) {
		super.delete(guideTermItem);
	}
	
}