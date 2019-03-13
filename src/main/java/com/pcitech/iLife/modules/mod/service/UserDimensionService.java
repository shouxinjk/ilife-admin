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
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.UserDimensionDao;

/**
 * 用户客观评价Service
 * @author qchzhu
 * @version 2019-03-13
 */
@Service
@Transactional(readOnly = true)
public class UserDimensionService extends TreeService<UserDimensionDao, UserDimension> {

	public UserDimension get(String id) {
		return super.get(id);
	}
	
	public List<UserDimension> findList(UserDimension userDimension) {
		if (StringUtils.isNotBlank(userDimension.getParentIds())){
			userDimension.setParentIds(","+userDimension.getParentIds()+",");
		}
		return super.findList(userDimension);
	}
	
	@Transactional(readOnly = false)
	public void save(UserDimension userDimension) {
		super.save(userDimension);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserDimension userDimension) {
		super.delete(userDimension);
	}


	@SuppressWarnings("unchecked")
	public List<UserDimension> findTree(){
		
		List<UserDimension> list;
		User user = UserUtils.getUser();
		UserDimension userDimension = new UserDimension();
		userDimension.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		userDimension.setParent(new UserDimension());
		list = dao.findList(userDimension);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (UserDimension e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (UserDimension e2 : list){
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
	
	public String getUserDimensionNames(String userDimensionIds) {
		return dao.getUserDimensionNames(userDimensionIds);
	}	
}