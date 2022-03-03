/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.PlatformSource;

/**
 * 商品数据来源DAO接口
 * @author ilife
 * @version 2022-03-03
 */
@MyBatisDao
public interface PlatformSourceDao extends CrudDao<PlatformSource> {
	//查询所有可用source
	public List<PlatformSource> findActiveSources(PlatformSource platformSource);
}