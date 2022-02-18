/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.Channel;
import com.pcitech.iLife.modules.mod.dao.ChannelDao;

/**
 * 频道管理Service
 * @author ilife
 * @version 2022-02-17
 */
@Service
@Transactional(readOnly = true)
public class ChannelService extends CrudService<ChannelDao, Channel> {

	public Channel get(String id) {
		return super.get(id);
	}
	
	public List<Channel> findListByStatus(String status) {
		Channel channel = new Channel();
		channel.setStatus(status);
		return dao.findListByStatus(channel);
	}
	public List<Channel> findListByStatus(Channel channel) {
		return dao.findListByStatus(channel);
	}
	
	public List<Channel> findList(Channel channel) {
		return super.findList(channel);
	}
	
	public Page<Channel> findPage(Page<Channel> page, Channel channel) {
		return super.findPage(page, channel);
	}
	
	@Transactional(readOnly = false)
	public void save(Channel channel) {
		super.save(channel);
	}
	
	@Transactional(readOnly = false)
	public void delete(Channel channel) {
		super.delete(channel);
	}
	
}