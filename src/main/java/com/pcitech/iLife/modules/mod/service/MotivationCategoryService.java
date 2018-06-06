/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.TreeService;
import com.pcitech.iLife.modules.mod.entity.MotivationCategory;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.MotivationCategoryDao;

/**
 * 内部动机类别Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class MotivationCategoryService extends TreeService<MotivationCategoryDao, MotivationCategory> {

	public MotivationCategory get(String id) {
		return super.get(id);
	}
	
	public List<MotivationCategory> findList(MotivationCategory motivationCategory) {
		return super.findList(motivationCategory);
	}
	
	public Page<MotivationCategory> findPage(Page<MotivationCategory> page, MotivationCategory motivationCategory) {
		return super.findPage(page, motivationCategory);
	}
	
	@Transactional(readOnly = false)
	public void save(MotivationCategory motivationCategory) {
		super.save(motivationCategory);
	}
	
	@Transactional(readOnly = false)
	public void delete(MotivationCategory motivationCategory) {
		super.delete(motivationCategory);
	}
	
	@SuppressWarnings("unchecked")
	public List<MotivationCategory> findTree(){
		
		List<MotivationCategory> list;
		User user = UserUtils.getUser();
		MotivationCategory motivationCategory = new MotivationCategory();
		motivationCategory.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		motivationCategory.setParent(new MotivationCategory());
		list = dao.findList(motivationCategory);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (MotivationCategory e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (MotivationCategory e2 : list){
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