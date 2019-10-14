/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Settlement;
import com.pcitech.iLife.modules.mod.dao.SettlementDao;

/**
 * 结算管理Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class SettlementService extends CrudService<SettlementDao, Settlement> {

	public Settlement get(String id) {
		return super.get(id);
	}
	
	public List<Settlement> findList(Settlement settlement) {
		return super.findList(settlement);
	}
	
	public Page<Settlement> findPage(Page<Settlement> page, Settlement settlement) {
		return super.findPage(page, settlement);
	}
	
	@Transactional(readOnly = false)
	public void save(Settlement settlement) {
		super.save(settlement);
	}
	
	@Transactional(readOnly = false)
	public void delete(Settlement settlement) {
		super.delete(settlement);
	}
	
}