/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.google.common.net.InetAddresses.TeredoInfo;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.persistence.TreeNode;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.common.service.TreeService;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.PersonaDao;

/**
 * 用户分群Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class PersonaService extends TreeService<PersonaDao, Persona> {

	public Persona get(String id) {
		return super.get(id);
	}
	
	public List<Persona> findList(Persona persona) {
		return super.findList(persona);
	}
	
	public Page<Persona> findPage(Page<Persona> page, Persona persona) {
		return super.findPage(page, persona);
	}
	
	@Transactional(readOnly = false)
	public void save(Persona persona) {
		super.save(persona);
	}
	
	@Transactional(readOnly = false)
	public void delete(Persona persona) {
		super.delete(persona);
	}
	
	public List<Persona> findByParentId(String parentId) {
		return dao.findByParentId(parentId);
	}
	public List<Persona> findByPhaseId(String phaseId) {
		return dao.findByPhaseId(phaseId);
	}
	@SuppressWarnings("unchecked")
	public List<Persona> findTree(){
		
		List<Persona> list;
		User user = UserUtils.getUser();
		Persona persona = new Persona();
		persona.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
//		persona.setParent(new Persona());
		list = dao.findList(persona);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (Persona e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (Persona e2 : list){
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
	
	public List<TreeNode> getTree(List<Phase> phaseTree) {
		List<TreeNode> list=new ArrayList<TreeNode>();
		Persona queryPersona=new Persona();
//		queryPersona.setParent(new Persona("0"));
		List<Persona> personas = findList(queryPersona);
		for(Persona persona:personas){
			TreeNode treeNode=new TreeNode();
			treeNode.setId(persona.getId());
			treeNode.setBusinessId(persona.getId());
			if(persona.getParent()!=null && persona.getParent().getId()!=null && persona.getParent().getName()!=null) {
				treeNode.setName(persona.getName()+" ("+persona.getParent().getName()+")");
			}else {
				treeNode.setName(persona.getName());
			}
			treeNode.setParent(new TreeNode(persona.getPhase().getId()));
//			treeNode.setParentIds(persona.getParentIds());
			treeNode.setSort(persona.getSort());
			treeNode.setModule("persona");
			treeNode.setTopType("phase");
			list.add(treeNode);
		}
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
		}
		return list;
	}
	
}