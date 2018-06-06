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
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.entity.TagCategory;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.TagCategoryDao;

/**
 * 标签分类Service
 * @author chenci
 * @version 2017-09-26
 */
@Service
@Transactional(readOnly = true)
public class TagCategoryService extends TreeService<TagCategoryDao, TagCategory> {

	public TagCategory get(String id) {
		return super.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public List<TagCategory> findTree(){
		
		List<TagCategory> list;
		User user = UserUtils.getUser();
		TagCategory tagCategory = new TagCategory();
		tagCategory.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		tagCategory.setParent(new TagCategory());
		list = dao.findList(tagCategory);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (TagCategory e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (TagCategory e2 : list){
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
	
	@Transactional(readOnly = false)
	public void save(TagCategory tagCategory) {
		super.save(tagCategory);
	}
	
	@Transactional(readOnly = false)
	public void delete(TagCategory tagCategory) {
		super.delete(tagCategory);
	}
	
}