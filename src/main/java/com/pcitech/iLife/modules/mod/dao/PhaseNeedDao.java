/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.PhaseNeed;

/**
 * 阶段需要构成DAO接口
 * @author ilife
 * @version 2022-02-09
 */
@MyBatisDao
public interface PhaseNeedDao extends CrudDao<PhaseNeed> {
	//更新weight
	public void updateWeight(Map<String,Object> params);
}