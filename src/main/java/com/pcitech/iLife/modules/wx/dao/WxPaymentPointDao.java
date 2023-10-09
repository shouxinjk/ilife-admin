/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxPaymentPoint;

/**
 * 阅豆付款DAO接口
 * @author ilife
 * @version 2022-03-31
 */
@MyBatisDao
public interface WxPaymentPointDao extends CrudDao<WxPaymentPoint> {
	//更新微信支付记录状态
	public void updateWxTransactionInfoByTradeNo(Map<String,Object> params);
}