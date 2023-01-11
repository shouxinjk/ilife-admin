/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.RankItemDimension;
import com.pcitech.iLife.modules.mod.dao.RankItemDimensionDao;

/**
 * 排行榜条目Service
 * @author ilife
 * @version 2023-01-11
 */
@Service
@Transactional(readOnly = true)
public class RankItemDimensionService extends CrudService<RankItemDimensionDao, RankItemDimension> {

	public RankItemDimension get(String id) {
		return super.get(id);
	}
	
	public List<RankItemDimension> findList(RankItemDimension rankItemDimension) {
		return super.findList(rankItemDimension);
	}
	
	public Page<RankItemDimension> findPage(Page<RankItemDimension> page, RankItemDimension rankItemDimension) {
		return super.findPage(page, rankItemDimension);
	}
	
	@Transactional(readOnly = false)
	public void save(RankItemDimension rankItemDimension) {
		super.save(rankItemDimension);
	}
	
	@Transactional(readOnly = false)
	public void delete(RankItemDimension rankItemDimension) {
		super.delete(rankItemDimension);
	}
	
}