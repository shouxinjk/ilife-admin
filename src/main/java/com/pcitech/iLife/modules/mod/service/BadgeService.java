/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Badge;
import com.pcitech.iLife.modules.mod.dao.BadgeDao;

/**
 * 勋章Service
 * @author ilife
 * @version 2022-12-27
 */
@Service
@Transactional(readOnly = true)
public class BadgeService extends CrudService<BadgeDao, Badge> {

	public Badge get(String id) {
		return super.get(id);
	}
	
	public List<Badge> findList(Badge badge) {
		return super.findList(badge);
	}
	
	public Page<Badge> findPage(Page<Badge> page, Badge badge) {
		return super.findPage(page, badge);
	}
	
	@Transactional(readOnly = false)
	public void save(Badge badge) {
		super.save(badge);
	}
	
	@Transactional(readOnly = false)
	public void delete(Badge badge) {
		super.delete(badge);
	}
	
}