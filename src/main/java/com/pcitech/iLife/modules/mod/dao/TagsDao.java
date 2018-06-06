/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Tags;

/**
 * 标签DAO接口
 * @author chenci
 * @version 2017-09-27
 */
@MyBatisDao
public interface TagsDao extends CrudDao<Tags> {
	
}