/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.diy.entity.GuideBookProposal;

/**
 * 定制指南主题关联DAO接口
 * @author chenci
 * @version 2022-10-29
 */
@MyBatisDao
public interface GuideBookProposalDao extends CrudDao<GuideBookProposal> {
	
}