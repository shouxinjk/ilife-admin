/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 达人管理DAO接口
 * @author qchzhu
 * @version 2019-10-14
 */
@MyBatisDao
public interface BrokerDao extends CrudDao<Broker> {
	//查询指定天数内有收益的流量主ID
	public List<String> findNotifyCandidatePublisherIdList(int days);
	
	//查询沉寂流量主
	public List<Broker> findInactivePublisherIdList(Map<String,Object> params);
	
	//根据达人ID、天数，查询流量主收益数据
	public Map<String,Object> findNotifyPublisherStat(Map<String,Object> params);
	
	//根据openid获取指定达人信息
	public Broker getByOpenid(String openid);
	
	//根据id查询达人收益信息
	public Map<String,Object> getMoney(String id);
	
	//根据id查询下级数量
	public int countChilds(String id);
	
	//根据parentId查询下级列表，分页返回
	public List<Broker> findChildList(Map<String,Object> params);
}