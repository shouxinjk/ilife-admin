/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.dao;

import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.ope.entity.UserPerformance;

/**
 * 用户属性标注DAO接口
 * @author qchzhu
 * @version 2020-05-07
 */
@MyBatisDao
public interface UserPerformanceDao extends CrudDao<UserPerformance> {
	//更新属性值的markedValue及level
	public void updateMarkedValue(Map<String,Object> params); 
	//更新属性值的controlValue及level
	public void updateControlValue(Map<String,Object> params); 
}