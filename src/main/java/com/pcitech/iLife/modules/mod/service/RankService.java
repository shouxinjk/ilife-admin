/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Rank;
import com.pcitech.iLife.modules.mod.dao.RankDao;

/**
 * 排行榜Service
 * @author ilife
 * @version 2023-01-11
 */
@Service
@Transactional(readOnly = true)
public class RankService extends CrudService<RankDao, Rank> {
	//分页查询
	public List<Rank> findPagedList(Map<String,Object> params){
		return dao.findPagedList(params);
	}
	
	public Rank get(String id) {
		return super.get(id);
	}
	
	public List<Rank> findList(Rank rank) {
		return super.findList(rank);
	}
	
	public Page<Rank> findPage(Page<Rank> page, Rank rank) {
		return super.findPage(page, rank);
	}
	
	@Transactional(readOnly = false)
	public void save(Rank rank) {
		super.save(rank);
	}
	
	@Transactional(readOnly = false)
	public void delete(Rank rank) {
		super.delete(rank);
	}
	
}