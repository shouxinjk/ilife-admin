/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.ProfitShareItem;

/**
 * 分润规则明细DAO接口
 * @author qchzhu
 * @version 2019-10-14
 */
@MyBatisDao
public interface ProfitShareItemDao extends CrudDao<ProfitShareItem> {
	//查询并返回单个记录
	public ProfitShareItem getByQuery(ProfitShareItem query);
	//查询并返回多个记录
	public List<ProfitShareItem> findListByQuery(ProfitShareItem query);
	
}