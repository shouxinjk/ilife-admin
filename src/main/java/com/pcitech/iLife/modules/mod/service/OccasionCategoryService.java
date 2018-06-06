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
import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.common.service.TreeService;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.OccasionCategoryDao;

/**
 * 外部诱因类别Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class OccasionCategoryService extends TreeService<OccasionCategoryDao, OccasionCategory> {

	public OccasionCategory get(String id) {
		return super.get(id);
	}
	
	public List<OccasionCategory> findList(OccasionCategory occasionCategory) {
		return super.findList(occasionCategory);
	}
	
	public Page<OccasionCategory> findPage(Page<OccasionCategory> page, OccasionCategory occasionCategory) {
		return super.findPage(page, occasionCategory);
	}
	
	@Transactional(readOnly = false)
	public void save(OccasionCategory occasionCategory) {
		super.save(occasionCategory);
	}
	
	@Transactional(readOnly = false)
	public void delete(OccasionCategory occasionCategory) {
		super.delete(occasionCategory);
	}
	
	@SuppressWarnings("unchecked")
	public List<OccasionCategory> findTree(){
		
		List<OccasionCategory> list;
		User user = UserUtils.getUser();
		OccasionCategory occasionCategory = new OccasionCategory();
		occasionCategory.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		occasionCategory.setParent(new OccasionCategory());
		list = dao.findList(occasionCategory);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (OccasionCategory e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (OccasionCategory e2 : list){
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