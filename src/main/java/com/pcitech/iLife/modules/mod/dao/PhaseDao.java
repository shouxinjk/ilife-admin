/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;

import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Phase;

/**
 * 人生阶段DAO接口
 * @author chenci
 * @version 2017-09-15
 */
@MyBatisDao
public interface PhaseDao extends TreeDao<Phase> {
	public List<Phase> findByParentId(String parentId);
}