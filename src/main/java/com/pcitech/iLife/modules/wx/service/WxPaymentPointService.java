/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxPaymentPoint;
import com.pcitech.iLife.modules.wx.dao.WxPaymentPointDao;

/**
 * 阅豆付款Service
 * @author ilife
 * @version 2022-03-31
 */
@Service
@Transactional(readOnly = true)
public class WxPaymentPointService extends CrudService<WxPaymentPointDao, WxPaymentPoint> {

	public WxPaymentPoint get(String id) {
		return super.get(id);
	}
	
	public List<WxPaymentPoint> findList(WxPaymentPoint wxPaymentPoint) {
		return super.findList(wxPaymentPoint);
	}
	
	public Page<WxPaymentPoint> findPage(Page<WxPaymentPoint> page, WxPaymentPoint wxPaymentPoint) {
		return super.findPage(page, wxPaymentPoint);
	}
	
	@Transactional(readOnly = false)
	public void save(WxPaymentPoint wxPaymentPoint) {
		super.save(wxPaymentPoint);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxPaymentPoint wxPaymentPoint) {
		super.delete(wxPaymentPoint);
	}
	
}