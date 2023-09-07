/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 置顶广告付款Entity
 * @author ilife
 * @version 2022-03-31
 */
public class WxPaymentAd extends DataEntity<WxPaymentAd> {
	
	private static final long serialVersionUID = 1L;
	private Broker broker;		// 付款达人
	private String subjectId;	//展示内容ID，包括article及account两种类型
	private WxArticle  article; //展示主题：文章
	private WxAccount account;	//展示主题：公众号
	private WxAdvertise advertise;		// 广告位
	private Date advertiseDate;		// 展示日期
	private java.math.BigDecimal amount;		// 付款金额
	private Date paymentDate;		// 付款时间
	private String tradeNo;		// 商户订单号
	private String tradeState;		// 交易状态
	private String transactionId;		// 支付流水号
	
	public WxPaymentAd() {
		super();
	}

	public WxPaymentAd(String id){
		super(id);
	}
	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public WxArticle getArticle() {
		return article;
	}

	public void setArticle(WxArticle article) {
		this.article = article;
	}

	public WxAccount getAccount() {
		return account;
	}

	public void setAccount(WxAccount account) {
		this.account = account;
	}

	public WxAdvertise getAdvertise() {
		return advertise;
	}

	public void setAdvertise(WxAdvertise advertise) {
		this.advertise = advertise;
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getAdvertiseDate() {
		return advertiseDate;
	}

	public void setAdvertiseDate(Date advertiseDate) {
		this.advertiseDate = advertiseDate;
	}
	
	public java.math.BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(java.math.BigDecimal amount) {
		this.amount = amount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	@Length(min=0, max=64, message="商户订单号长度必须介于 0 和 64 之间")
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	
	@Length(min=0, max=20, message="交易状态长度必须介于 0 和 20 之间")
	public String getTradeState() {
		return tradeState;
	}

	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}
	
	@Length(min=1, max=64, message="支付流水号长度必须介于 1 和 64 之间")
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
}