/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.dao.BrokerDao;

/**
 * 达人管理Service
 * @author qchzhu
 * @version 2019-10-14
 */
@Service
@Transactional(readOnly = true)
public class BrokerService extends CrudService<BrokerDao, Broker> {
	
	//查询指定天数内有收益的流量主ID
	public List<String> findNotifyCandidatePublisherIdList(int days){
		return dao.findNotifyCandidatePublisherIdList(days);
	}
	
	//根据达人ID、天数，查询流量主收益数据
	public Map<String,Object> findNotifyPublisherStat(Map<String,Object> params){
		return dao.findNotifyPublisherStat(params);
	}
	
	
	//根据parentId查询下级列表，分页返回
	public List<Broker> findChildList(Map<String,Object> params){
		return dao.findChildList(params);
	}
	
	public Broker get(String id) {
		return super.get(id);
	}
	
	public Broker getByOpenid(String openid) {
		return dao.getByOpenid(openid);
	}
	
	public Map<String,Object> getMoney(String id) {
		return dao.getMoney(id);
	}	
	
	public int countChilds(String id) {
		return dao.countChilds(id);
	}
	
	public List<Broker> findList(Broker broker) {
		return super.findList(broker);
	}
	
	public Page<Broker> findPage(Page<Broker> page, Broker broker) {
		return super.findPage(page, broker);
	}
	
	@Transactional(readOnly = false)
	public void save(Broker broker) {
		super.save(broker);
	}
	
	@Transactional(readOnly = false)
	public void delete(Broker broker) {
		super.delete(broker);
	}

	
}