/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.common.service.TreeService;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.sys.entity.Office;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.cms.entity.Category;
import com.pcitech.iLife.modules.cms.entity.Site;
import com.pcitech.iLife.modules.mod.dao.PhaseDao;

/**
 * 人生阶段Service
 * @author chenci
 * @version 2017-09-15
 */
@Service
@Transactional(readOnly = true)
public class PhaseService extends TreeService<PhaseDao, Phase> {

	
	public Phase get(String id) {
		return super.get(id);
	}
	
	public List<Phase> findList(Phase phase) {
		return super.findList(phase);
	}
	
	public Page<Phase> findPage(Page<Phase> page, Phase phase) {
		return super.findPage(page, phase);
	}
	
	@Transactional(readOnly = false)
	public void save(Phase phase) {
		super.save(phase);
	}
	
	@Transactional(readOnly = false)
	public void delete(Phase phase) {
		super.delete(phase);
	}
	
	@SuppressWarnings("unchecked")
	public List<Phase> findTree(){
		
		List<Phase> list;
		User user = UserUtils.getUser();
		Phase phase = new Phase();
		phase.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		phase.setParent(new Phase());
		list = dao.findList(phase);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (Phase e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (Phase e2 : list){
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