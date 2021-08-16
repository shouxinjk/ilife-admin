/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.TreeDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Persona;

/**
 * 用户分群DAO接口
 * @author chenci
 * @version 2017-09-15
 */
@MyBatisDao
public interface PersonaDao extends TreeDao<Persona> {
	public List<Persona> findByParentId(String parentId);
	public List<Persona> findByPhaseId(String phaseId);
}