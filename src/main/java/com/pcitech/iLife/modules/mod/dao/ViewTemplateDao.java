/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.ViewTemplate;

/**
 * 模型展示模板DAO接口
 * @author qchzhu
 * @version 2021-08-15
 */
@MyBatisDao
public interface ViewTemplateDao extends CrudDao<ViewTemplate> {
	
}