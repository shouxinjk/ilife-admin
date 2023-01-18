/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Credit;

/**
 * 贡献度DAO接口
 * @author ilife
 * @version 2023-01-18
 */
@MyBatisDao
public interface CreditDao extends CrudDao<Credit> {
	
}