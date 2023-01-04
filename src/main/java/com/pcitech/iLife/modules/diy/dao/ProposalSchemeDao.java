/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.diy.entity.ProposalScheme;

/**
 * 个性化定制模板DAO接口
 * @author chenci
 * @version 2022-10-29
 */
@MyBatisDao
public interface ProposalSchemeDao extends CrudDao<ProposalScheme> {
	//获取分页列表
	public List<ProposalScheme> findPagedList(Map<String,Object> param);
}