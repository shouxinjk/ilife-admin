/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;

import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;

/**
 * 电商平台类目映射DAO接口
 * @author ilife
 * @version 2022-01-07
 */
@MyBatisDao
public interface PlatformCategoryDao extends TreeDao<PlatformCategory> {
	public void upsertMapping(PlatformCategory platformCategory);
	public List<PlatformCategory> findMapping(PlatformCategory platformCategory);
}