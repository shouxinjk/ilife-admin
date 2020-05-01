/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.TreeService;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.UserTagCategory;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.UserTagCategoryDao;

/**
 * 用户标签分类Service
 * @author qchzhu
 * @version 2020-04-30
 */
@Service
@Transactional(readOnly = true)
public class UserTagCategoryService extends TreeService<UserTagCategoryDao, UserTagCategory> {

	@Autowired
	private UserTagCategoryDao userTagCategoryDao; 
	
	public UserTagCategory get(String id) {
		return super.get(id);
	}

	@SuppressWarnings("unchecked")
	public List<UserTagCategory> findTree(){
		
		List<UserTagCategory> list;
		User user = UserUtils.getUser();
		UserTagCategory tagCategory = new UserTagCategory();
		tagCategory.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		tagCategory.setParent(new UserTagCategory());
		list = dao.findList(tagCategory);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (UserTagCategory e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (UserTagCategory e2 : list){
					if (e.getParent().getId().equals(e2.getId())){
						isExistParent = true;
						break;
					}
				}
				if (!isExistParent){
					parentIdSet.add(e.getParent().getId());
				}
			}
		}
		if (parentIdSet.size() > 0){
			
		}
		
		return list;
	}
	
	
	public List<UserTagCategory> findList(UserTagCategory userTagCategory) {
		return super.findList(userTagCategory);
	}
	
	public Page<UserTagCategory> findPage(Page<UserTagCategory> page, UserTagCategory userTagCategory) {
		return super.findPage(page, userTagCategory);
	}
	
	@Transactional(readOnly = false)
	public void save(UserTagCategory userTagCategory) {
		super.save(userTagCategory);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserTagCategory userTagCategory) {
		super.delete(userTagCategory);
	}
	
	@Transactional(readOnly = false)
	public void updateChildrenType(UserTagCategory tagCategory){
		userTagCategoryDao.updateChildrenType(tagCategory);
	}	
}