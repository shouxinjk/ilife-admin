/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Hierarchy;

/**
 * 社会分层DAO接口
 * @author chenci
 * @version 2017-09-15
 */
@MyBatisDao
public interface HierarchyDao extends CrudDao<Hierarchy> {
	public Map<String,Double> getCapabilityMap(String hierarchyId);
}