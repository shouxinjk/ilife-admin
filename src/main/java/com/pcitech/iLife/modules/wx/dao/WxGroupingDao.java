/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxGrouping;

/**
 * 互助班车DAO接口
 * @author ilife
 * @version 2022-04-22
 */
@MyBatisDao
public interface WxGroupingDao extends CrudDao<WxGrouping> {
	
}