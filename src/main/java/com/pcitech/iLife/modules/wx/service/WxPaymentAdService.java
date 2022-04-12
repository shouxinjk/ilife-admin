/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxPaymentAd;
import com.pcitech.iLife.modules.wx.dao.WxPaymentAdDao;

/**
 * 置顶广告付款Service
 * @author ilife
 * @version 2022-03-31
 */
@Service
@Transactional(readOnly = true)
public class WxPaymentAdService extends CrudService<WxPaymentAdDao, WxPaymentAd> {

	//获取指定天数的已售卖广告
	public List<WxPaymentAd> findSoldAds(int days){
		return dao.findSoldAds(days);
	}
	//更新微信支付记录状态
	@Transactional(readOnly = false)
	public void updateWxTransactionInfoByTradeNo(Map<String,String> params) {
		dao.updateWxTransactionInfoByTradeNo(params);
	}
	
	//查询缺少TransactionId的记录，包括paymentAd及paymentPoint
	public List<String> findItemsWithoutTransactionId(){
		return dao.findItemsWithoutTransactionId();
	}
	
	public WxPaymentAd get(String id) {
		return super.get(id);
	}
	
	public List<WxPaymentAd> findList(WxPaymentAd wxPaymentAd) {
		return super.findList(wxPaymentAd);
	}
	
	public Page<WxPaymentAd> findPage(Page<WxPaymentAd> page, WxPaymentAd wxPaymentAd) {
		return super.findPage(page, wxPaymentAd);
	}
	
	@Transactional(readOnly = false)
	public void save(WxPaymentAd wxPaymentAd) {
		super.save(wxPaymentAd);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxPaymentAd wxPaymentAd) {
		super.delete(wxPaymentAd);
	}
	
}