/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxSubscribes;

/**
 * 关注记录DAO接口
 * @author ilife
 * @version 2022-03-31
 */
@MyBatisDao
public interface WxSubscribesDao extends CrudDao<WxSubscribes> {
	
}