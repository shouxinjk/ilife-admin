/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 支付管理Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class Payment extends DataEntity<Payment> {
	
	private static final long serialVersionUID = 1L;
	private Broker broker;		// 达人ID
	private String type;		// 类型：个人或公司
	private String amountRequest;		// 申请提现金额
	private String amountPayment;		// 实际支付金额
	private String channel;		// 付款渠道：支付宝、微信、银联
	private String voucher;		// 付款凭证
	private String account;		// 收款账户
	private String status;		// 支付状态
	private String memo;		// 备注
	
	public Payment() {
		super();
	}

	public Payment(String id){
		super(id);
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
	@Length(min=0, max=50, message="类型：个人或公司长度必须介于 0 和 50 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getAmountRequest() {
		return amountRequest;
	}

	public void setAmountRequest(String amountRequest) {
		this.amountRequest = amountRequest;
	}
	
	public String getAmountPayment() {
		return amountPayment;
	}

	public void setAmountPayment(String amountPayment) {
		this.amountPayment = amountPayment;
	}
	
	@Length(min=0, max=50, message="付款渠道：支付宝、微信、银联长度必须介于 0 和 50 之间")
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	@Length(min=0, max=100, message="付款凭证长度必须介于 0 和 100 之间")
	public String getVoucher() {
		return voucher;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}
	
	@Length(min=0, max=50, message="收款账户长度必须介于 0 和 50 之间")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	@Length(min=0, max=50, message="支付状态长度必须介于 0 和 50 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
}