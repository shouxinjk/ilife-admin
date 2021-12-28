/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.PosterTemplate;

/**
 * 海报模板管理DAO接口
 * @author ilife
 * @version 2021-12-28
 */
@MyBatisDao
public interface PosterTemplateDao extends CrudDao<PosterTemplate> {
	public List<PosterTemplate> findItemList(String categoryId);
	public List<PosterTemplate> findBoardList();
}