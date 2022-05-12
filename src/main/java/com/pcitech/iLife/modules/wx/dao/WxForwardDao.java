/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxForward;

/**
 * 开白请求DAO接口
 * @author ilife
 * @version 2022-05-11
 */
@MyBatisDao
public interface WxForwardDao extends CrudDao<WxForward> {
	
}