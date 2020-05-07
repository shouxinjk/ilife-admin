/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.dao;

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
	
}