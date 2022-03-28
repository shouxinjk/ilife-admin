/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxAdvertise;

/**
 * 微信广告位管理DAO接口
 * @author ilife
 * @version 2022-03-28
 */
@MyBatisDao
public interface WxAdvertiseDao extends CrudDao<WxAdvertise> {
	
}