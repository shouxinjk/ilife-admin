/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxSubscribes;
import com.pcitech.iLife.modules.wx.dao.WxSubscribesDao;

/**
 * 关注记录Service
 * @author ilife
 * @version 2022-03-31
 */
@Service
@Transactional(readOnly = true)
public class WxSubscribesService extends CrudService<WxSubscribesDao, WxSubscribes> {

	public WxSubscribes get(String id) {
		return super.get(id);
	}
	
	public List<WxSubscribes> findList(WxSubscribes wxSubscribes) {
		return super.findList(wxSubscribes);
	}
	
	public Page<WxSubscribes> findPage(Page<WxSubscribes> page, WxSubscribes wxSubscribes) {
		return super.findPage(page, wxSubscribes);
	}
	
	@Transactional(readOnly = false)
	public void save(WxSubscribes wxSubscribes) {
		super.save(wxSubscribes);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxSubscribes wxSubscribes) {
		super.delete(wxSubscribes);
	}
	
}