/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 收款管理Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class Receipt extends DataEntity<Receipt> {
	
	private static final long serialVersionUID = 1L;
	private String platform;		// 电商平台
	private String type;		// 收款类型：个人或公司
	private String amountRequest;		// 申请提现总额
	private String amountReceived;		// 实际到账总额
	private String channel;		// 收款渠道：支付宝、微信、银联
	private String voucher;		// 收款凭证
	private String account;		// 收款账户
	private String filename;		// 对应清单文件路径
	private String status;		// 支付状态
	private String memo;		// 备注
	
	public Receipt() {
		super();
	}

	public Receipt(String id){
		super(id);
	}

	@Length(min=1, max=20, message="电商平台长度必须介于 1 和 20 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=0, max=50, message="收款类型：个人或公司长度必须介于 0 和 50 之间")
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
	
	public String getAmountReceived() {
		return amountReceived;
	}

	public void setAmountReceived(String amountReceived) {
		this.amountReceived = amountReceived;
	}
	
	@Length(min=0, max=50, message="收款渠道：支付宝、微信、银联长度必须介于 0 和 50 之间")
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	@Length(min=0, max=50, message="收款凭证长度必须介于 0 和 50 之间")
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
	
	@Length(min=0, max=255, message="对应清单文件路径长度必须介于 0 和 255 之间")
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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