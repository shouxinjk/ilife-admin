/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.service.TreeService;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.LifeStyleCategory;
import com.pcitech.iLife.modules.mod.dao.LifeStyleCategoryDao;

/**
 * vals分类Service
 * @author chenci
 * @version 2018-08-05
 */
@Service
@Transactional(readOnly = true)
public class LifeStyleCategoryService extends TreeService<LifeStyleCategoryDao, LifeStyleCategory> {

	public LifeStyleCategory get(String id) {
		return super.get(id);
	}
	
	public List<LifeStyleCategory> findList(LifeStyleCategory lifeStyleCategory) {
		if (StringUtils.isNotBlank(lifeStyleCategory.getParentIds())){
			lifeStyleCategory.setParentIds(","+lifeStyleCategory.getParentIds()+",");
		}
		return super.findList(lifeStyleCategory);
	}
	
	@Transactional(readOnly = false)
	public void save(LifeStyleCategory lifeStyleCategory) {
		super.save(lifeStyleCategory);
	}
	
	@Transactional(readOnly = false)
	public void delete(LifeStyleCategory lifeStyleCategory) {
		super.delete(lifeStyleCategory);
	}

	public List<LifeStyleCategory> findTree(){

		List<LifeStyleCategory> list;
		User user = UserUtils.getUser();
		LifeStyleCategory motivationCategory = new LifeStyleCategory();
		motivationCategory.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		motivationCategory.setParent(new LifeStyleCategory());
		list = dao.findList(motivationCategory);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (LifeStyleCategory e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (LifeStyleCategory e2 : list){
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
}