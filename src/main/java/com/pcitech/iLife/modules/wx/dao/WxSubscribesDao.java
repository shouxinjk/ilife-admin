/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxReads;
import com.pcitech.iLife.modules.wx.entity.WxSubscribes;

/**
 * 关注记录DAO接口
 * @author ilife
 * @version 2022-03-31
 */
@MyBatisDao
public interface WxSubscribesDao extends CrudDao<WxSubscribes> {
	//查询关注列表 
	public List<WxSubscribes> findSubscribingList(Map<String,Object> param);
}