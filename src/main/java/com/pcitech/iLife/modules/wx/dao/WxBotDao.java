/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxBot;
import com.pcitech.iLife.modules.wx.entity.WxTopping;

/**
 * 微信机器人DAO接口
 * @author ilife
 * @version 2022-06-07
 */
@MyBatisDao
public interface WxBotDao extends CrudDao<WxBot> {
	//获取待更新Bot记录 
	public WxBot getPendingBot();
	//根据达人微信任务获取所有代理Bot记录 
	public List<WxBot> listAgentBotByBrokerId(@Param("brokerId") String brokerId);
}