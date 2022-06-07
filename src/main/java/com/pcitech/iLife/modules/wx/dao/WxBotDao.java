/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxBot;

/**
 * 微信机器人DAO接口
 * @author ilife
 * @version 2022-06-07
 */
@MyBatisDao
public interface WxBotDao extends CrudDao<WxBot> {
	
}