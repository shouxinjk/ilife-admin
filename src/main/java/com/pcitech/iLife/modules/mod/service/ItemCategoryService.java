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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.ItemCategoryDao;

/**
 * 商品分类Service
 * @author chenci
 * @version 2017-09-22
 */
@Service
@Transactional(readOnly = true)
public class ItemCategoryService extends TreeService<ItemCategoryDao, ItemCategory> {

	public ItemCategory get(String id) {
		return super.get(id);
	}
	
	public List<ItemCategory> findListWithRank(){
		return dao.findListWithRank();
	}
	
	public List<ItemCategory> findList(ItemCategory itemCategory) {
		return super.findList(itemCategory);
	}
	
	public List<ItemCategory> findByParentId(String parentId) {
		return dao.findByParentId(parentId);
	}
	
	public Page<ItemCategory> findPage(Page<ItemCategory> page, ItemCategory itemCategory) {
		return super.findPage(page, itemCategory);
	}
	
	@Transactional(readOnly = false)
	public void save(ItemCategory itemCategory) {
		super.save(itemCategory);
	}
	
	@Transactional(readOnly = false)
	public void delete(ItemCategory itemCategory) {
		super.delete(itemCategory);
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemCategory> findTree(){
		
		List<ItemCategory> list;
		User user = UserUtils.getUser();
		ItemCategory itemCategory = new ItemCategory();
		itemCategory.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		itemCategory.setParent(new ItemCategory());
		list = dao.findList(itemCategory);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (ItemCategory e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (ItemCategory e2 : list){
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
	
	public String getItemCategoryNames(String itemCategoryIds) {
		return dao.getItemCategoryNames(itemCategoryIds);
	}
	
}