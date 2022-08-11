/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxBot;
import com.pcitech.iLife.modules.wx.dao.WxBotDao;

/**
 * 微信机器人Service
 * @author ilife
 * @version 2022-06-07
 */
@Service
@Transactional(readOnly = true)
public class WxBotService extends CrudService<WxBotDao, WxBot> {
	//获取待更新Bot记录 
	public WxBot getPendingBot() {
		return dao.getPendingBot();
	}
	
	//根据达人微信任务获取所有代理Bot记录 
	public List<WxBot> listAgentBotByBrokerId(String brokerId) {
		return dao.listAgentBotByBrokerId(brokerId);
	}
	
	public WxBot get(String id) {
		return super.get(id);
	}
	
	public List<WxBot> findList(WxBot wxBot) {
		return super.findList(wxBot);
	}
	
	public Page<WxBot> findPage(Page<WxBot> page, WxBot wxBot) {
		return super.findPage(page, wxBot);
	}
	
	@Transactional(readOnly = false)
	public void save(WxBot wxBot) {
		super.save(wxBot);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxBot wxBot) {
		super.delete(wxBot);
	}
	
}