/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.persistence.TreeNode;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.ope.dao.PerformanceDao;

/**
 * 标注Service
 * @author chenci
 * @version 2017-09-28
 */
@Service
@Transactional(readOnly = true)
public class PerformanceService extends CrudService<PerformanceDao, Performance> {

	@Autowired
	MeasureService measureService;
	
	public Performance get(String id) {
		return super.get(id);
	}
	
	public List<Performance> findList(Performance performance) {
		return super.findList(performance);
	}
	
	public Page<Performance> findPage(Page<Performance> page, Performance performance) {
		return super.findPage(page, performance);
	}
	
	@Transactional(readOnly = false)
	public void save(Performance performance) {
		super.save(performance);
	}
	
	@Transactional(readOnly = false)
	public void delete(Performance performance) {
		super.delete(performance);
	}
	
	public List<TreeNode> getTree(List<ItemCategory> itemCategoryTree) {
		List<TreeNode> list=new ArrayList<TreeNode>();
		Measure query=new Measure();
		for(ItemCategory category:itemCategoryTree){
			TreeNode treeNode=new TreeNode();
			treeNode.setId(category.getId());
			treeNode.setBusinessId(category.getId());
			treeNode.setName(category.getName());
			treeNode.setParent(new TreeNode(category.getParentId()));
			treeNode.setParentIds(category.getParentIds());
			treeNode.setSort(category.getSort());
			treeNode.setModule("itemCategory");
			treeNode.setTopType("itemCategory");
			list.add(treeNode);
			//查询分类下的Measure，作为叶子节点
			query.setCategory(category);
			List<Measure> items = measureService.findList(query);
			for(Measure item:items) {
				TreeNode leaf=new TreeNode();
				leaf.setId(item.getId());
				leaf.setBusinessId(item.getId());
				leaf.setName(item.getName());
				leaf.setParent(new TreeNode(category.getId()));
				//leaf.setParentIds(category.getParentIds());
				//leaf.setSort(item.getSort());
				leaf.setModule("measure");
				leaf.setTopType("itemCategory");
				list.add(leaf);
			}
		}
		return list;
	}
	
}