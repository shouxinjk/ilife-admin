/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.LinkTemplate;

/**
 * 链接模板DAO接口
 * @author qchzhu
 * @version 2021-09-01
 */
@MyBatisDao
public interface LinkTemplateDao extends CrudDao<LinkTemplate> {
	
}