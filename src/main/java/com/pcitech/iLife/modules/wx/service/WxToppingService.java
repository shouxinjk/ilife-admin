/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxTopping;
import com.pcitech.iLife.modules.wx.dao.WxToppingDao;

/**
 * 置顶记录Service
 * @author ilife
 * @version 2022-04-02
 */
@Service
@Transactional(readOnly = true)
public class WxToppingService extends CrudService<WxToppingDao, WxTopping> {

	public WxTopping get(String id) {
		return super.get(id);
	}
	
	public List<WxTopping> findList(WxTopping wxTopping) {
		return super.findList(wxTopping);
	}
	
	public Page<WxTopping> findPage(Page<WxTopping> page, WxTopping wxTopping) {
		return super.findPage(page, wxTopping);
	}
	
	@Transactional(readOnly = false)
	public void save(WxTopping wxTopping) {
		super.save(wxTopping);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxTopping wxTopping) {
		super.delete(wxTopping);
	}
	
}