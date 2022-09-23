/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Motivation;

/**
 * 内部动机DAO接口
 * @author chenci
 * @version 2017-09-15
 */
@MyBatisDao
public interface MotivationDao extends CrudDao<Motivation> {
	
	public String getMotivationNames(String motivationIds);
	
	public List<Motivation> findByOccasionId(String id);
	
	//参数phaseId, name
	public List<Motivation> findPendingListForPhase(Map<String,String> params);
	
	//参数personaId, name
	public List<Motivation> findPendingListForPersona(Map<String,String> params);
	
	//参数OccasionId, name
	public List<Motivation> findPendingListForOccasion(Map<String,String> params);
	
	//参数categoryId, name
	public List<Motivation> findPendingListForItemCategory(Map<String,String> params);
	
}