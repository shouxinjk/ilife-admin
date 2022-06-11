/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxGroupTask;
import com.pcitech.iLife.modules.wx.dao.WxGroupTaskDao;

/**
 * 微信群任务Service
 * @author ilife
 * @version 2022-06-11
 */
@Service
@Transactional(readOnly = true)
public class WxGroupTaskService extends CrudService<WxGroupTaskDao, WxGroupTask> {
	//根据nickname获取指定达人的微信群任务
	public List<WxGroupTask> getByNickname(String nickname){
		return dao.getByNickname(nickname);
	}
		
	public WxGroupTask get(String id) {
		return super.get(id);
	}
	
	public List<WxGroupTask> findList(WxGroupTask wxGroupTask) {
		return super.findList(wxGroupTask);
	}
	
	public Page<WxGroupTask> findPage(Page<WxGroupTask> page, WxGroupTask wxGroupTask) {
		return super.findPage(page, wxGroupTask);
	}
	
	@Transactional(readOnly = false)
	public void save(WxGroupTask wxGroupTask) {
		super.save(wxGroupTask);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxGroupTask wxGroupTask) {
		super.delete(wxGroupTask);
	}
	
}