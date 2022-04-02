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
 * 阅豆付款Entity
 * @author ilife
 * @version 2022-03-31
 */
public class WxPaymentPoint extends DataEntity<WxPaymentPoint> {
	
	private static final long serialVersionUID = 1L;
	private Broker broker;		// 付款达人
	private WxPoints points;		// 阅豆产品
	private int amount;		// 付款金额
	private Date paymentDate;		// 付款时间
	private String tradeNo;		// 商户订单号
	private String tradeState;		// 交易状态
	private String transactionId;		// 支付流水号
	
	public WxPaymentPoint() {
		super();
	}

	public WxPaymentPoint(String id){
		super(id);
	}


	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public WxPoints getPoints() {
		return points;
	}

	public void setPoints(WxPoints points) {
		this.points = points;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
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