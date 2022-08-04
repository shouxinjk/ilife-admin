/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxGrouping;
import com.pcitech.iLife.modules.wx.dao.WxGroupingDao;

/**
 * 互助班车Service
 * @author ilife
 * @version 2022-04-22
 */
@Service
@Transactional(readOnly = true)
public class WxGroupingService extends CrudService<WxGroupingDao, WxGrouping> {

	//查询班车阅读结果 
	public List<Map<String,Object>> findGroupingResultMap(Map<String,Object> param){
		return dao.findGroupingResultMap(param);
	}
	
	public WxGrouping get(String id) {
		return super.get(id);
	}
	
	public List<WxGrouping> findList(WxGrouping wxGrouping) {
		return super.findList(wxGrouping);
	}
	
	public Page<WxGrouping> findPage(Page<WxGrouping> page, WxGrouping wxGrouping) {
		return super.findPage(page, wxGrouping);
	}
	
	@Transactional(readOnly = false)
	public void save(WxGrouping wxGrouping) {
		super.save(wxGrouping);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxGrouping wxGrouping) {
		super.delete(wxGrouping);
	}
	
}