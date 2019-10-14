/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.ProfitShareItem;
import com.pcitech.iLife.modules.mod.dao.ProfitShareItemDao;

/**
 * 分润规则明细Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class ProfitShareItemService extends CrudService<ProfitShareItemDao, ProfitShareItem> {

	public ProfitShareItem get(String id) {
		return super.get(id);
	}
	
	public List<ProfitShareItem> findList(ProfitShareItem profitShareItem) {
		return super.findList(profitShareItem);
	}
	
	public Page<ProfitShareItem> findPage(Page<ProfitShareItem> page, ProfitShareItem profitShareItem) {
		return super.findPage(page, profitShareItem);
	}
	
	@Transactional(readOnly = false)
	public void save(ProfitShareItem profitShareItem) {
		super.save(profitShareItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProfitShareItem profitShareItem) {
		super.delete(profitShareItem);
	}
	
}