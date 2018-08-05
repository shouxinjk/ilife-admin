/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.service.TreeService;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.dao.ItemDimensionDao;

/**
 * 商品维度Service
 * @author chenci
 * @version 2018-06-22
 */
@Service
@Transactional(readOnly = true)
public class ItemDimensionService extends TreeService<ItemDimensionDao, ItemDimension> {

	public ItemDimension get(String id) {
		return super.get(id);
	}
	
	public List<ItemDimension> findList(ItemDimension itemDimension) {
		if (StringUtils.isNotBlank(itemDimension.getParentIds())){
			itemDimension.setParentIds(","+itemDimension.getParentIds()+",");
		}
		return super.findList(itemDimension);
	}
	
	@Transactional(readOnly = false)
	public void save(ItemDimension itemDimension) {
		super.save(itemDimension);
	}
	
	@Transactional(readOnly = false)
	public void delete(ItemDimension itemDimension) {
		super.delete(itemDimension);
	}

	public List<ItemDimension> findTree(){

		List<ItemDimension> list;
		User user = UserUtils.getUser();
		ItemDimension itemDimension = new ItemDimension();
		itemDimension.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		itemDimension.setParent(new ItemDimension());
		list = dao.findList(itemDimension);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (ItemDimension e : list){
			if (e.getParent()!=null && org.apache.commons.lang3.StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (ItemDimension e2 : list){
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