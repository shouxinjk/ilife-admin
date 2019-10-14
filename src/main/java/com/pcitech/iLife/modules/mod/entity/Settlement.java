/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 结算管理Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class Settlement extends DataEntity<Settlement> {
	
	private static final long serialVersionUID = 1L;
	private String brokerId;		// 达人ID
	private String voucher;		// 结算凭证
	private String paymentId;		// 支付记录ID
	private String type;		// 类型：订单、积分、意向
	private String amount;		// 结算金额
	private String status;		// 状态：锁定等
	
	public Settlement() {
		super();
	}

	public Settlement(String id){
		super(id);
	}

	@Length(min=1, max=20, message="达人ID长度必须介于 1 和 20 之间")
	public String getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(String brokerId) {
		this.brokerId = brokerId;
	}
	
	@Length(min=0, max=50, message="结算凭证长度必须介于 0 和 50 之间")
	public String getVoucher() {
		return voucher;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}
	
	@Length(min=1, max=50, message="支付记录ID长度必须介于 1 和 50 之间")
	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
	@Length(min=0, max=50, message="类型：订单、积分、意向长度必须介于 0 和 50 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	@Length(min=1, max=1, message="状态：锁定等长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}