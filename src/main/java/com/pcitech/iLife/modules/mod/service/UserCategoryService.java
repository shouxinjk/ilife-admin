/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.pcitech.iLife.common.service.TreeService;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.UserCategory;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.UserCategoryDao;

/**
 * 用户属性分类Service
 * @author qchzhu
 * @version 2019-03-13
 */
@Service
@Transactional(readOnly = true)
public class UserCategoryService extends TreeService<UserCategoryDao, UserCategory> {

	public UserCategory get(String id) {
		return super.get(id);
	}
	
	public List<UserCategory> findList(UserCategory userCategory) {
		if (StringUtils.isNotBlank(userCategory.getParentIds())){
			userCategory.setParentIds(","+userCategory.getParentIds()+",");
		}
		return super.findList(userCategory);
	}
	
	@Transactional(readOnly = false)
	public void save(UserCategory userCategory) {
		super.save(userCategory);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserCategory userCategory) {
		super.delete(userCategory);
	}
	

	@SuppressWarnings("unchecked")
	public List<UserCategory> findTree(){
		
		List<UserCategory> list;
		User user = UserUtils.getUser();
		UserCategory userCategory = new UserCategory();
		userCategory.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		userCategory.setParent(new UserCategory());
		list = dao.findList(userCategory);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (UserCategory e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (UserCategory e2 : list){
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
	
	public String getUserCategoryNames(String userCategoryIds) {
		return dao.getUserCategoryNames(userCategoryIds);
	}
	
}