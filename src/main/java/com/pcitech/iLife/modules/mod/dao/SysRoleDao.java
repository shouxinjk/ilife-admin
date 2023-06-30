/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.SysRole;

/**
 * 系统角色DAO接口
 * @author ilife
 * @version 2023-06-30
 */
@MyBatisDao
public interface SysRoleDao extends CrudDao<SysRole> {
	
}