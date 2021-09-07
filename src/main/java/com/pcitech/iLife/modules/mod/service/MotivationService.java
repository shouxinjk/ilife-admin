/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.persistence.TreeNode;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.MotivationCategory;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.dao.MotivationDao;
import com.pcitech.iLife.modules.mod.dao.PhaseDao;

/**
 * 内部动机Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class MotivationService extends CrudService<MotivationDao, Motivation> {
	
	@Autowired
	private PhaseDao phaseDao;
	
	public List<Motivation> findByOccasionId(String id){
		return dao.findByOccasionId(id);
	}
	
	public Motivation get(String id) {
		return super.get(id);
	}
	
	public List<Motivation> findList(Motivation motivation) {
		return super.findList(motivation);
	}
	
	public Page<Motivation> findPage(Page<Motivation> page, Motivation motivation) {
		return super.findPage(page, motivation);
	}
	
	@Transactional(readOnly = false)
	public void save(Motivation motivation) {
		super.save(motivation);
	}
	
	@Transactional(readOnly = false)
	public void delete(Motivation motivation) {
		super.delete(motivation);
	}

	public List<TreeNode> getTree(List<Phase> phaseTree, List<MotivationCategory> motivationCategoryTree) {
		List<TreeNode> list=new ArrayList<TreeNode>();
		for(Phase phase:phaseTree){
			TreeNode treeNode=new TreeNode();
			treeNode.setId(phase.getId());
			treeNode.setBusinessId(phase.getId());
			treeNode.setName(phase.getName());
			treeNode.setParent(new TreeNode(phase.getParentId()));
			treeNode.setParentIds(phase.getParentIds());
			treeNode.setSort(phase.getSort());
			treeNode.setModule("phase");
			treeNode.setTopType("phase");
			list.add(treeNode);
			Phase queryEntity=new Phase();
			queryEntity.setParentIds("%,"+phase.getId()+",%");
			List<Phase> phases = phaseDao.findByParentIdsLike(queryEntity);
			if(phases==null||phases.size()==0){
				for(MotivationCategory motivationCategory:motivationCategoryTree){
					if(!motivationCategory.getId().equals("1")){
						TreeNode  motivationCategoryTreeNode=new TreeNode();
						motivationCategoryTreeNode.setId(motivationCategory.getId()+phase.getId());
						motivationCategoryTreeNode.setBusinessId(motivationCategory.getId());
						motivationCategoryTreeNode.setName(motivationCategory.getName());
						if(motivationCategory.getParentId().equals("1")){
							motivationCategoryTreeNode.setParent(new TreeNode(phase.getId()));
						}else{
							motivationCategoryTreeNode.setParent(new TreeNode(motivationCategory.getParentId()+phase.getId()));
						}
						motivationCategoryTreeNode.setTopId(phase.getId());
						motivationCategoryTreeNode.setSort(motivationCategory.getSort());
						motivationCategoryTreeNode.setModule("motivationCategory");
						motivationCategoryTreeNode.setTopType("phase");
						list.add(motivationCategoryTreeNode);
					}
					
				}
			}
		}
		
		return list;
	}
	

	public List<TreeNode> getTreeWithLeaf(List<Phase> phaseTree, List<MotivationCategory> motivationCategoryTree) {
		List<TreeNode> list=new ArrayList<TreeNode>();
		for(Phase phase:phaseTree){
			TreeNode treeNode=new TreeNode();
			treeNode.setId(phase.getId());
			treeNode.setBusinessId(phase.getId());
			treeNode.setName(phase.getName());
			treeNode.setParent(new TreeNode(phase.getParentId()));
			treeNode.setParentIds(phase.getParentIds());
			treeNode.setSort(phase.getSort());
			treeNode.setModule("phase");
			treeNode.setTopType("phase");
			list.add(treeNode);
			//Phase queryEntity=new Phase();
			//queryEntity.setParentIds("%,"+phase.getId()+",%");
			//List<Phase> phases = phaseDao.findByParentIdsLike(queryEntity);
			//if(phases==null||phases.size()==0){
				for(MotivationCategory motivationCategory:motivationCategoryTree){
					if(!motivationCategory.getId().equals("1")){
						TreeNode  motivationCategoryTreeNode=new TreeNode();
						motivationCategoryTreeNode.setId(motivationCategory.getId()+phase.getId());
						motivationCategoryTreeNode.setBusinessId(motivationCategory.getId());
						motivationCategoryTreeNode.setName(motivationCategory.getName());
						if(motivationCategory.getParentId().equals("1")){
							motivationCategoryTreeNode.setParent(new TreeNode(phase.getId()));
						}else{
							motivationCategoryTreeNode.setParent(new TreeNode(motivationCategory.getParentId()+phase.getId()));
						}
						motivationCategoryTreeNode.setTopId(phase.getId());
						motivationCategoryTreeNode.setSort(motivationCategory.getSort());
						motivationCategoryTreeNode.setModule("motivationCategory");
						motivationCategoryTreeNode.setTopType("phase");
						list.add(motivationCategoryTreeNode);
						//获取对应phase及category下的具体动机，作为叶子节点
						Motivation query = new Motivation();
						query.setPhase(phase);
						query.setMotivationCategory(motivationCategory);
						List<Motivation> motivations = findList(query);
						for(Motivation motivation:motivations) {
							TreeNode leafNode=new TreeNode();
							leafNode.setId(motivation.getId());
							leafNode.setBusinessId(motivation.getId());
							leafNode.setName(motivation.getName());
							leafNode.setParent(new TreeNode(motivationCategory.getId()+phase.getId()));//放到当前category下，注意需要结合phase进行差异化
//							leafNode.setParentIds(persona.getParentIds());
//							leafNode.setSort(occasion.getSort());
							motivationCategoryTreeNode.setTopId(phase.getId());
							leafNode.setModule("motivation");
							leafNode.setTopType("phase");
							list.add(leafNode);
						}
					}
				}
			//}
		}
		
		return list;
	}
	
	public String getMotivationNames(String motivationIds) {
		return dao.getMotivationNames(motivationIds);
	}
}