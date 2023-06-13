/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.ilife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.ilife.modules.mod.entity.Subscription;
import com.pcitech.ilife.modules.mod.dao.SubscriptionDao;

/**
 * SaaS订阅Service
 * @author ilife
 * @version 2023-06-13
 */
@Service
@Transactional(readOnly = true)
public class SubscriptionService extends CrudService<SubscriptionDao, Subscription> {

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