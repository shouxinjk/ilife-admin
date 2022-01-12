/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.CategoryNeed;

/**
 * 品类需要满足DAO接口
 * @author qchzhu
 * @version 2020-04-30
 */
@MyBatisDao
public interface CategoryNeedDao extends CrudDao<CategoryNeed> {
	//更新weight
	public void updateWeight(Map<String,Object> params);
}