/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Rank;

/**
 * 排行榜DAO接口
 * @author ilife
 * @version 2023-01-11
 */
@MyBatisDao
public interface RankDao extends CrudDao<Rank> {
	
}