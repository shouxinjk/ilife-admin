/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.PlatformProperty;

/**
 * 电商平台属性映射DAO接口
 * @author ilife
 * @version 2022-01-07
 */
@MyBatisDao
public interface PlatformPropertyDao extends CrudDao<PlatformProperty> {
	
}