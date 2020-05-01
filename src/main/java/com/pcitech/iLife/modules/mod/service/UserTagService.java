/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.UserTag;
import com.pcitech.iLife.modules.mod.dao.UserTagDao;

/**
 * 用户标签Service
 * @author qchzhu
 * @version 2020-04-30
 */
@Service
@Transactional(readOnly = true)
public class UserTagService extends CrudService<UserTagDao, UserTag> {

	public UserTag get(String id) {
		return super.get(id);
	}
	
	public List<UserTag> findList(UserTag userTag) {
		return super.findList(userTag);
	}
	
	public Page<UserTag> findPage(Page<UserTag> page, UserTag userTag) {
		return super.findPage(page, userTag);
	}
	
	@Transactional(readOnly = false)
	public void save(UserTag userTag) {
		super.save(userTag);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserTag userTag) {
		super.delete(userTag);
	}
	
}