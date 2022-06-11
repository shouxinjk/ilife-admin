/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.List;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.wx.entity.WxGroupTask;

/**
 * 微信群任务DAO接口
 * @author ilife
 * @version 2022-06-11
 */
@MyBatisDao
public interface WxGroupTaskDao extends CrudDao<WxGroupTask> {
	//根据nickname获取指定达人的微信群任务
	public List<WxGroupTask> getByNickname(String nickname);
}