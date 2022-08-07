/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxGroup;

/**
 * 微信群DAO接口
 * @author ilife
 * @version 2022-06-07
 */
@MyBatisDao
public interface WxGroupDao extends CrudDao<WxGroup> {
	//根据brokerId查询建立有sendFeature任务的微信群
	public List<WxGroup> findFeaturedGroup(WxGroup wxGroup);
	
	//根据userId查询建立有sendFeature任务的微信群
	public List<WxGroup> findFeaturedGroupByUserId(Map<String,String> param);
	
	//根据名称查询微信群
	public WxGroup findGroupByName(@Param("name") String name);
	
	//根据gid查询微信群
	public WxGroup findGroupByGid(@Param("gid") String gid);
	
}