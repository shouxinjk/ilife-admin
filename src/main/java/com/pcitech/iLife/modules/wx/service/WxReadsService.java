/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxReads;
import com.pcitech.iLife.modules.wx.dao.WxReadsDao;

/**
 * 阅读记录Service
 * @author ilife
 * @version 2022-03-31
 */
@Service
@Transactional(readOnly = true)
public class WxReadsService extends CrudService<WxReadsDao, WxReads> {

	public WxReads get(String id) {
		return super.get(id);
	}
	
	public List<WxReads> findList(WxReads wxReads) {
		return super.findList(wxReads);
	}
	
	public Page<WxReads> findPage(Page<WxReads> page, WxReads wxReads) {
		return super.findPage(page, wxReads);
	}
	
	@Transactional(readOnly = false)
	public void save(WxReads wxReads) {
		super.save(wxReads);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxReads wxReads) {
		super.delete(wxReads);
	}
	
}