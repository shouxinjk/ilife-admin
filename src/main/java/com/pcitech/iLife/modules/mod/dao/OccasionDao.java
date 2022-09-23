/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;

/**
 * 外部诱因DAO接口
 * @author chenci
 * @version 2017-09-15
 */
@MyBatisDao
public interface OccasionDao extends CrudDao<Occasion> {
	
	public String getOccasionNames(String occasionIds);

	public int updateChildrenType(OccasionCategory occasionCategory);
	
	//参数NeedId, name
	public List<Occasion> findPendingListForNeed(Map<String,String> params);
}