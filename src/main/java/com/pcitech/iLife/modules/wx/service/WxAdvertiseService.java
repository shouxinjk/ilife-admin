/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxAdvertise;
import com.pcitech.iLife.modules.wx.dao.WxAdvertiseDao;

/**
 * 微信广告位管理Service
 * @author ilife
 * @version 2022-03-28
 */
@Service
@Transactional(readOnly = true)
public class WxAdvertiseService extends CrudService<WxAdvertiseDao, WxAdvertise> {

	public WxAdvertise get(String id) {
		return super.get(id);
	}
	
	public List<WxAdvertise> findList(WxAdvertise wxAdvertise) {
		return super.findList(wxAdvertise);
	}
	
	public Page<WxAdvertise> findPage(Page<WxAdvertise> page, WxAdvertise wxAdvertise) {
		return super.findPage(page, wxAdvertise);
	}
	
	@Transactional(readOnly = false)
	public void save(WxAdvertise wxAdvertise) {
		super.save(wxAdvertise);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxAdvertise wxAdvertise) {
		super.delete(wxAdvertise);
	}
	
}