/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Receipt;
import com.pcitech.iLife.modules.mod.dao.ReceiptDao;

/**
 * 收款管理Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class ReceiptService extends CrudService<ReceiptDao, Receipt> {

	public Receipt get(String id) {
		return super.get(id);
	}
	
	public List<Receipt> findList(Receipt receipt) {
		return super.findList(receipt);
	}
	
	public Page<Receipt> findPage(Page<Receipt> page, Receipt receipt) {
		return super.findPage(page, receipt);
	}
	
	@Transactional(readOnly = false)
	public void save(Receipt receipt) {
		super.save(receipt);
	}
	
	@Transactional(readOnly = false)
	public void delete(Receipt receipt) {
		super.delete(receipt);
	}
	
}