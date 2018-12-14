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
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.entity.ItemEvaluation;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.ItemEvaluationDao;

/**
 * 主观评价Service
 * @author qchzhu
 * @version 2018-12-14
 */
@Service
@Transactional(readOnly = true)
public class ItemEvaluationService extends TreeService<ItemEvaluationDao, ItemEvaluation> {

	public ItemEvaluation get(String id) {
		return super.get(id);
	}
	
	public List<ItemEvaluation> findList(ItemEvaluation itemEvaluation) {
		if (StringUtils.isNotBlank(itemEvaluation.getParentIds())){
			itemEvaluation.setParentIds(","+itemEvaluation.getParentIds()+",");
		}
		return super.findList(itemEvaluation);
	}
	
	@Transactional(readOnly = false)
	public void save(ItemEvaluation itemEvaluation) {
		super.save(itemEvaluation);
	}
	
	@Transactional(readOnly = false)
	public void delete(ItemEvaluation itemEvaluation) {
		super.delete(itemEvaluation);
	}

	public List<ItemEvaluation> findTree(ItemCategory category){
		List<ItemEvaluation> list;
		User user = UserUtils.getUser();
		ItemEvaluation itemEvaluation = new ItemEvaluation();
		itemEvaluation.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		//start query dimensions by category qchzhu
//		itemDimension.setParent(new ItemDimension());
//		itemDimension.setParent(new ItemDimension());
		if(category != null)
			itemEvaluation.setCategory(category);
		/*
		ItemDimension parentItemDimension = new ItemDimension();
		if(categoryId.trim().length()>0) {
			parentItemDimension.setId(categoryId);//父节点ID和对应目录ID相同
		}
		itemDimension.setParent(parentItemDimension);
		//*/
		//end qchzhu
		list = dao.findList(itemEvaluation);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (ItemEvaluation e : list){
			if (e.getParent()!=null && org.apache.commons.lang3.StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (ItemEvaluation e2 : list){
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