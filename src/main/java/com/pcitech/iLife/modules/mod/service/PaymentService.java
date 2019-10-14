/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Payment;
import com.pcitech.iLife.modules.mod.dao.PaymentDao;

/**
 * 支付管理Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class PaymentService extends CrudService<PaymentDao, Payment> {

	public Payment get(String id) {
		return super.get(id);
	}
	
	public List<Payment> findList(Payment payment) {
		return super.findList(payment);
	}
	
	public Page<Payment> findPage(Page<Payment> page, Payment payment) {
		return super.findPage(page, payment);
	}
	
	@Transactional(readOnly = false)
	public void save(Payment payment) {
		super.save(payment);
	}
	
	@Transactional(readOnly = false)
	public void delete(Payment payment) {
		super.delete(payment);
	}
	
}