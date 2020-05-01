/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.PersonaNeed;

/**
 * 画像需要构成DAO接口
 * @author qchzhu
 * @version 2020-04-30
 */
@MyBatisDao
public interface PersonaNeedDao extends CrudDao<PersonaNeed> {
	
}