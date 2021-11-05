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
	//根据categoryId查找继承属性列表
	public List<Map<String,String>> findInheritMeasures(String categoryId);
	
	//根据measureId查询属性值列表
	public List<Performance> findListByMeasureAndCategory(Map<String,String> params);
	//根据measureId查询属性值列表
	public List<Performance> findListByMeasureId(String measureId);
	//更新属性值的markedValue及level
	public void updateMarkedValue(Map<String,Object> params); 
	//更新属性值的controlValue及level
	public void updateControlValue(Map<String,Object> params); 	
}