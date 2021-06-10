/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.ope.entity.Performance;

/**
 * 标注DAO接口
 * @author chenci
 * @version 2017-09-28
 */
@MyBatisDao
public interface PerformanceDao extends CrudDao<Performance> {
	//根据measureId查询属性值列表
	public List<Performance> findListByMeasureId(String measureId);
	//更新属性值的markedValue及level
	public void updateMarkedValue(Map<String,Object> params); 
}