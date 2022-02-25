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
import com.pcitech.iLife.modules.mod.entity.ChannelNeed;
import com.pcitech.iLife.modules.mod.dao.ChannelNeedDao;

/**
 * 频道需要构成Service
 * @author ilife
 * @version 2022-02-17
 */
@Service
@Transactional(readOnly = true)
public class ChannelNeedService extends CrudService<ChannelNeedDao, ChannelNeed> {

	@Transactional(readOnly = false)
	public void updateWeight(Map<String,Object> params) {
		dao.updateWeight(params);
	}
	
	public ChannelNeed get(String id) {
		return super.get(id);
	}
	
	public List<ChannelNeed> findList(ChannelNeed channelNeed) {
		return super.findList(channelNeed);
	}
	
	public Page<ChannelNeed> findPage(Page<ChannelNeed> page, ChannelNeed channelNeed) {
		return super.findPage(page, channelNeed);
	}
	
	@Transactional(readOnly = false)
	public void save(ChannelNeed channelNeed) {
		super.save(channelNeed);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChannelNeed channelNeed) {
		super.delete(channelNeed);
	}
	
}