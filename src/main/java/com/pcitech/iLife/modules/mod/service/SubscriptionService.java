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
import com.pcitech.iLife.modules.mod.entity.Subscription;
import com.pcitech.iLife.modules.mod.dao.SubscriptionDao;

/**
 * SaaS订阅Service
 * @author ilife
 * @version 2023-06-13
 */
@Service
@Transactional(readOnly = true)
public class SubscriptionService extends CrudService<SubscriptionDao, Subscription> {
	
	//更新微信支付记录状态
	@Transactional(readOnly = false)
	public void updateWxTransactionInfoByTradeNo(Map<String,String> params) {
		dao.updateWxTransactionInfoByTradeNo(params);
	}
	
	public Subscription get(String id) {
		return super.get(id);
	}
	
	public List<Subscription> findList(Subscription subscription) {
		return super.findList(subscription);
	}
	
	public Page<Subscription> findPage(Page<Subscription> page, Subscription subscription) {
		return super.findPage(page, subscription);
	}
	
	@Transactional(readOnly = false)
	public void save(Subscription subscription) {
		super.save(subscription);
	}
	
	@Transactional(readOnly = false)
	public void delete(Subscription subscription) {
		super.delete(subscription);
	}
	
}