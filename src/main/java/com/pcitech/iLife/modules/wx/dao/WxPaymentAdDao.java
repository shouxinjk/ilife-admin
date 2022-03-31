/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.List;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxPaymentAd;

/**
 * 置顶广告付款DAO接口
 * @author ilife
 * @version 2022-03-31
 */
@MyBatisDao
public interface WxPaymentAdDao extends CrudDao<WxPaymentAd> {
	//获取从今天开始的时间段内 已售卖广告记录 
	public List<WxPaymentAd> findSoldAds(int days);
}