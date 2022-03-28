/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxPoints;
import com.pcitech.iLife.modules.wx.dao.WxPointsDao;

/**
 * 微信虚拟豆管理Service
 * @author ilife
 * @version 2022-03-28
 */
@Service
@Transactional(readOnly = true)
public class WxPointsService extends CrudService<WxPointsDao, WxPoints> {

	public WxPoints get(String id) {
		return super.get(id);
	}
	
	public List<WxPoints> findList(WxPoints wxPoints) {
		return super.findList(wxPoints);
	}
	
	public Page<WxPoints> findPage(Page<WxPoints> page, WxPoints wxPoints) {
		return super.findPage(page, wxPoints);
	}
	
	@Transactional(readOnly = false)
	public void save(WxPoints wxPoints) {
		super.save(wxPoints);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxPoints wxPoints) {
		super.delete(wxPoints);
	}
	
}