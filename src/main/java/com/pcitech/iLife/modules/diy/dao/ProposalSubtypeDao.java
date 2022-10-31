/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.diy.entity.ProposalSubtype;

/**
 * 个性化定制小类DAO接口
 * @author chenci
 * @version 2022-10-31
 */
@MyBatisDao
public interface ProposalSubtypeDao extends CrudDao<ProposalSubtype> {
	
}