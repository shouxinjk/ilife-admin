/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxForward;
import com.pcitech.iLife.modules.wx.dao.WxForwardDao;

/**
 * 开白请求Service
 * @author ilife
 * @version 2022-05-11
 */
@Service
@Transactional(readOnly = true)
public class WxForwardService extends CrudService<WxForwardDao, WxForward> {

	public WxForward get(String id) {
		return super.get(id);
	}
	
	public List<WxForward> findList(WxForward wxForward) {
		return super.findList(wxForward);
	}
	
	public Page<WxForward> findPage(Page<WxForward> page, WxForward wxForward) {
		return super.findPage(page, wxForward);
	}
	
	@Transactional(readOnly = false)
	public void save(WxForward wxForward) {
		super.save(wxForward);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxForward wxForward) {
		super.delete(wxForward);
	}
	
}