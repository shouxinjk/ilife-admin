/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxGrouping;
import com.pcitech.iLife.modules.wx.entity.WxReads;

/**
 * 互助班车DAO接口
 * @author ilife
 * @version 2022-04-22
 */
@MyBatisDao
public interface WxGroupingDao extends CrudDao<WxGrouping> {
	//查询班车阅读结果 
	public List<Map<String,Object>> findGroupingResultMap(Map<String,Object> param);
}