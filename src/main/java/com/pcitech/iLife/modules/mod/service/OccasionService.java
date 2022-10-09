/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.Phase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.persistence.TreeNode;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.dao.OccasionDao;

/**
 * 外部诱因Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class OccasionService extends CrudService<OccasionDao, Occasion> {

	@Autowired
	private OccasionDao occasionDao;

	//参数NeedId, name
	public List<Occasion> findPendingListForNeed(Map<String,String> params){
		return dao.findPendingListForNeed(params);
	}
	
	public Occasion get(String id) {
		return super.get(id);
	}
	
	public List<Occasion> findList(Occasion occasion) {
		return super.findList(occasion);
	}
	
	public Page<Occasion> findPage(Page<Occasion> page, Occasion occasion) {
		return super.findPage(page, occasion);
	}
	
	@Transactional(readOnly = false)
	public void save(Occasion occasion) {
		super.save(occasion);
	}
	
	@Transactional(readOnly = false)
	public void delete(Occasion occasion) {
		super.delete(occasion);
	}
	
	public String getOccasionNames(String occasionIds) {
		return dao.getOccasionNames(occasionIds);
	}

	@Transactional(readOnly = false)
	public void updateChildrenType(OccasionCategory occasionCategory){
		occasionDao.updateChildrenType(occasionCategory);
	}
	

	public List<TreeNode> getTree(List<OccasionCategory> occasionCategoryTree) {
		List<TreeNode> list=new ArrayList<TreeNode>();
		for(OccasionCategory category:occasionCategoryTree){
			//添加诱因分类节点
			TreeNode treeNode=new TreeNode();
			treeNode.setId(category.getId());
			treeNode.setBusinessId(category.getId());
			treeNode.setName(category.getName());
			treeNode.setParent(new TreeNode(category.getParentId()));
			treeNode.setParentIds(category.getParentIds());
			treeNode.setSort(category.getSort());
			treeNode.setModule("occasionCategory");
			treeNode.setTopType("occasionCategory");
			list.add(treeNode);
			//查询添加该分类下的诱因作为叶子节点
			Occasion query = new Occasion();
			query.setOccasionCategory(category);
			List<Occasion> occasions = findList(query);
			for(Occasion occasion:occasions){
				TreeNode leafNode=new TreeNode();
				leafNode.setId(occasion.getId());
				leafNode.setBusinessId(occasion.getId());
				leafNode.setName("๏"+occasion.getName());
				leafNode.setParent(new TreeNode(category.getId()));//直接放在当前OccasionCategory下
//				leafNode.setParentIds(persona.getParentIds());
//				leafNode.setSort(occasion.getSort());
				leafNode.setModule("occasion");
				leafNode.setTopType("occasionCategory");
				list.add(leafNode);
			}
		}
		return list;
	}	
}