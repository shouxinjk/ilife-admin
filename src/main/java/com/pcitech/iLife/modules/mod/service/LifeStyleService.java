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
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.common.service.TreeService;
import com.pcitech.iLife.modules.mod.entity.LifeStyle;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.LifeStyleDao;

/**
 * VALS模型Service
 * @author chenci
 * @version 2017-09-22
 */
@Service
@Transactional(readOnly = true)
public class LifeStyleService extends TreeService<LifeStyleDao, LifeStyle> {

	public LifeStyle get(String id) {
		return super.get(id);
	}
	
	public List<LifeStyle> findList(LifeStyle lifeStyle) {
		return super.findList(lifeStyle);
	}
	
	public Page<LifeStyle> findPage(Page<LifeStyle> page, LifeStyle lifeStyle) {
		return super.findPage(page, lifeStyle);
	}
	
	@Transactional(readOnly = false)
	public void save(LifeStyle lifeStyle) {
		super.save(lifeStyle);
	}
	
	@Transactional(readOnly = false)
	public void delete(LifeStyle lifeStyle) {
		super.delete(lifeStyle);
	}
	
	@SuppressWarnings("unchecked")
	public List<LifeStyle> findTree(LifeStyle lifeStyle){
		
		List<LifeStyle> list;
		User user = UserUtils.getUser();
		lifeStyle.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		lifeStyle.setParent(new LifeStyle());
		list = dao.findList(lifeStyle);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (LifeStyle e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (LifeStyle e2 : list){
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