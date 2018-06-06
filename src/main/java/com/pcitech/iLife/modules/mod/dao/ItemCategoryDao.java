/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;

/**
 * 商品分类DAO接口
 * @author chenci
 * @version 2017-09-22
 */
@MyBatisDao
public interface ItemCategoryDao extends TreeDao<ItemCategory> {
	
	public String getItemCategoryNames(String itemCategoryIds);
	
}