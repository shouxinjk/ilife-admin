/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.dict.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.persistence.TreeNode;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.dict.entity.DictBrand;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.dict.dao.DictBrandDao;

/**
 * 品牌字典管理Service
 * @author ilife
 * @version 2021-11-26
 */
@Service
@Transactional(readOnly = true)
public class DictBrandService extends CrudService<DictBrandDao, DictBrand> {

	public DictBrand get(String id) {
		return super.get(id);
	}
	
	public List<DictBrand> findList(DictBrand dictBrand) {
		return super.findList(dictBrand);
	}
	
	public Page<DictBrand> findPage(Page<DictBrand> page, DictBrand dictBrand) {
		return super.findPage(page, dictBrand);
	}
	
	@Transactional(readOnly = false)
	public void save(DictBrand dictBrand) {
		super.save(dictBrand);
	}
	
	@Transactional(readOnly = false)
	public void delete(DictBrand dictBrand) {
		super.delete(dictBrand);
	}
	
	@Transactional(readOnly = false)
	public void updateMarkedValue(Map<String,Object> params) {
		dao.updateMarkedValue(params);
	}
	
	public List<TreeNode> getTree(List<ItemCategory> itemCategoryTree) {
		List<TreeNode> list=new ArrayList<TreeNode>();
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
		}
		return list;
	}
	
}