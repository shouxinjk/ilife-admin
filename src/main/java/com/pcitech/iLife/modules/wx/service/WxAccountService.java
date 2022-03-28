/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxAccount;
import com.pcitech.iLife.modules.wx.dao.WxAccountDao;

/**
 * 微信公众号管理Service
 * @author ilife
 * @version 2022-03-28
 */
@Service
@Transactional(readOnly = true)
public class WxAccountService extends CrudService<WxAccountDao, WxAccount> {

	public WxAccount get(String id) {
		return super.get(id);
	}
	
	public List<WxAccount> findList(WxAccount wxAccount) {
		return super.findList(wxAccount);
	}
	
	public Page<WxAccount> findPage(Page<WxAccount> page, WxAccount wxAccount) {
		return super.findPage(page, wxAccount);
	}
	
	@Transactional(readOnly = false)
	public void save(WxAccount wxAccount) {
		super.save(wxAccount);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxAccount wxAccount) {
		super.delete(wxAccount);
	}
	
}