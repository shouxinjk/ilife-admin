/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.dict.dao;

import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.dict.entity.DictMaterial;

/**
 * 材质字典管理DAO接口
 * @author ilife
 * @version 2021-11-26
 */
@MyBatisDao
public interface DictMaterialDao extends CrudDao<DictMaterial> {
	//更新属性值的markedValue及level
	public void updateMarkedValue(Map<String,Object> params); 
}