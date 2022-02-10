/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.persistence.TreeNode;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.ope.entity.UserPerformance;
import com.pcitech.iLife.modules.mod.entity.UserCategory;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.ope.dao.UserPerformanceDao;

/**
 * 用户属性标注Service
 * @author qchzhu
 * @version 2020-05-07
 */
@Service
@Transactional(readOnly = true)
public class UserPerformanceService extends CrudService<UserPerformanceDao, UserPerformance> {

	@Autowired
	UserMeasureService userMeasureService;
	
	@Transactional(readOnly = false)
	public void updateMarkedValue(Map<String,Object> params) {
		dao.updateMarkedValue(params);
	}
	@Transactional(readOnly = false)
	public void updateControlValue(Map<String,Object> params) {
		dao.updateControlValue(params);
	}
	
	public UserPerformance get(String id) {
		return super.get(id);
	}
	
	public List<UserPerformance> findList(UserPerformance userPerformance) {
		return super.findList(userPerformance);
	}
	
	public Page<UserPerformance> findPage(Page<UserPerformance> page, UserPerformance userPerformance) {
		return super.findPage(page, userPerformance);
	}
	
	@Transactional(readOnly = false)
	public void save(UserPerformance userPerformance) {
		super.save(userPerformance);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserPerformance userPerformance) {
		super.delete(userPerformance);
	}
	

	public List<TreeNode> getTree(List<UserCategory> userCategoryTree) {
		List<TreeNode> list=new ArrayList<TreeNode>();
		UserMeasure query=new UserMeasure();
		for(UserCategory category:userCategoryTree){
			TreeNode treeNode=new TreeNode();
			treeNode.setId(category.getId());
			treeNode.setBusinessId(category.getId());
			treeNode.setName(category.getName());
			treeNode.setParent(new TreeNode(category.getParentId()));
			treeNode.setParentIds(category.getParentIds());
			treeNode.setSort(category.getSort());
			treeNode.setModule("userCategory");
			treeNode.setTopType("userCategory");
			list.add(treeNode);
			//查询分类下的Measure，作为叶子节点
			query.setCategory(category);
			List<UserMeasure> items = userMeasureService.findList(query);
			for(UserMeasure item:items) {
				TreeNode leaf=new TreeNode();
				leaf.setId(item.getId());
				leaf.setBusinessId(item.getId());
				leaf.setName(item.getName());
				leaf.setParent(new TreeNode(category.getId()));
				//leaf.setParentIds(category.getParentIds());
				//leaf.setSort(item.getSort());
				leaf.setModule("measure");
				leaf.setTopType("userCategory");
				list.add(leaf);
			}
		}
		return list;
	}
	
}