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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.PlatformCategoryDao;

/**
 * 电商平台类目映射Service
 * @author ilife
 * @version 2022-01-07
 */
@Service
@Transactional(readOnly = true)
public class PlatformCategoryService extends TreeService<PlatformCategoryDao, PlatformCategory> {

	public boolean upsertMapping(PlatformCategory platformCategory) {
		try {
			dao.upsertMapping(platformCategory);
			return true;
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public PlatformCategory get(String id) {
		return super.get(id);
	}
	
	public List<PlatformCategory> findList(PlatformCategory platformCategory) {
		if (StringUtils.isNotBlank(platformCategory.getParentIds())){
			platformCategory.setParentIds(","+platformCategory.getParentIds()+",");
		}
		return super.findList(platformCategory);
	}
	
	@Transactional(readOnly = false)
	public void save(PlatformCategory platformCategory) {
		super.save(platformCategory);
	}
	
	@Transactional(readOnly = false)
	public void delete(PlatformCategory platformCategory) {
		super.delete(platformCategory);
	}
	
	@SuppressWarnings("unchecked")
	public List<PlatformCategory> findTree(){
		
		List<PlatformCategory> list;
		User user = UserUtils.getUser();
		PlatformCategory itemCategory = new PlatformCategory();
		itemCategory.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		itemCategory.setParent(new PlatformCategory());
		list = dao.findList(itemCategory);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (PlatformCategory e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (PlatformCategory e2 : list){
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