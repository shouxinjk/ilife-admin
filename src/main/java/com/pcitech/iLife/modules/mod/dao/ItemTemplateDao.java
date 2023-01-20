/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.ItemTemplate;

/**
 * 类目推广文案DAO接口
 * @author qchzhu
 * @version 2021-07-21
 */
@MyBatisDao
public interface ItemTemplateDao extends CrudDao<ItemTemplate> {
	public List<ItemTemplate> findByCategoryId(String category);
	public List<ItemTemplate> findItemList(Map<String,Object> params);
	public List<ItemTemplate> findBoardList();
}