/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxGroup;
import com.pcitech.iLife.modules.wx.dao.WxGroupDao;

/**
 * 微信群Service
 * @author ilife
 * @version 2022-06-07
 */
@Service
@Transactional(readOnly = true)
public class WxGroupService extends CrudService<WxGroupDao, WxGroup> {
	//查询建立有sendFeature任务的微信群
	public List<WxGroup> findFeaturedGroup(WxGroup wxGroup){
		return dao.findFeaturedGroup(wxGroup);
	}
	
	//根据名称查询微信群
	public WxGroup findGroupByName(String name) {
		return dao.findGroupByName(name);
	}
	
	public WxGroup get(String id) {
		return super.get(id);
	}
	
	public List<WxGroup> findList(WxGroup wxGroup) {
		return super.findList(wxGroup);
	}
	
	public Page<WxGroup> findPage(Page<WxGroup> page, WxGroup wxGroup) {
		return super.findPage(page, wxGroup);
	}
	
	@Transactional(readOnly = false)
	public void save(WxGroup wxGroup) {
		super.save(wxGroup);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxGroup wxGroup) {
		super.delete(wxGroup);
	}
	
}