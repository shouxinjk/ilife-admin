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
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.mod.dao.UserEvaluationDao;

/**
 * 用户主观评价Service
 * @author qchzhu
 * @version 2019-03-13
 */
@Service
@Transactional(readOnly = true)
public class UserEvaluationService extends TreeService<UserEvaluationDao, UserEvaluation> {

	public UserEvaluation get(String id) {
		return super.get(id);
	}
	
	public List<UserEvaluation> findList(UserEvaluation userEvaluation) {
		if (StringUtils.isNotBlank(userEvaluation.getParentIds())){
			userEvaluation.setParentIds(","+userEvaluation.getParentIds()+",");
		}
		return super.findList(userEvaluation);
	}
	
	@Transactional(readOnly = false)
	public void save(UserEvaluation userEvaluation) {
		super.save(userEvaluation);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserEvaluation userEvaluation) {
		super.delete(userEvaluation);
	}


	@SuppressWarnings("unchecked")
	public List<UserEvaluation> findTree(){
		
		List<UserEvaluation> list;
		User user = UserUtils.getUser();
		UserEvaluation userEvaluation = new UserEvaluation();
		userEvaluation.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
		userEvaluation.setParent(new UserEvaluation());
		list = dao.findList(userEvaluation);
		// 将没有父节点的节点，找到父节点
		Set<String> parentIdSet = Sets.newHashSet();
		for (UserEvaluation e : list){
			if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
				boolean isExistParent = false;
				for (UserEvaluation e2 : list){
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
	
	public String getUserEvaluationNames(String userEvaluationIds) {
		return dao.getUserEvaluationNames(userEvaluationIds);
	}	
}