/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 清算Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class Clearing extends DataEntity<Clearing> {
	
	private static final long serialVersionUID = 1L;
	private String platform;		// 电商平台
	private String orderId;		// 内部订单ID
	private String category;		// 商品类别
	private String amountOrder;		// 订单金额
	private String amountCommission;		// 佣金总额
	private String amountProfit;		// 分润金额
	private String schemeId;		// 所属分润规则
	private String beneficiary;		// 受益方
	private String beneficiaryType;		// 受益方类型：个人、整体
	private String share;		// 所占份额
	private String item;		// 商品名称
	private Date orderTime;		// 订单完成时间
	private String status;		// 分润完成状态
	private String voucherCollection;		// 收款凭证号
	private String voucherSettlement;		// 结算凭证号
	
	public Clearing() {
		super();
	}

	public Clearing(String id){
		super(id);
	}

	@Length(min=1, max=50, message="电商平台长度必须介于 1 和 50 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=0, max=50, message="内部订单ID长度必须介于 0 和 50 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Length(min=0, max=50, message="商品类别长度必须介于 0 和 50 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getAmountOrder() {
		return amountOrder;
	}

	public void setAmountOrder(String amountOrder) {
		this.amountOrder = amountOrder;
	}
	
	public String getAmountCommission() {
		return amountCommission;
	}

	public void setAmountCommission(String amountCommission) {
		this.amountCommission = amountCommission;
	}
	
	public String getAmountProfit() {
		return amountProfit;
	}

	public void setAmountProfit(String amountProfit) {
		this.amountProfit = amountProfit;
	}
	
	@Length(min=1, max=20, message="所属分润规则长度必须介于 1 和 20 之间")
	public String getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(String schemeId) {
		this.schemeId = schemeId;
	}
	
	@Length(min=0, max=50, message="受益方长度必须介于 0 和 50 之间")
	public String getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}
	
	@Length(min=0, max=50, message="受益方类型：个人、整体长度必须介于 0 和 50 之间")
	public String getBeneficiaryType() {
		return beneficiaryType;
	}

	public void setBeneficiaryType(String beneficiaryType) {
		this.beneficiaryType = beneficiaryType;
	}
	
	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}
	
	@Length(min=0, max=255, message="商品名称长度必须介于 0 和 255 之间")
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	
	@Length(min=0, max=20, message="分润完成状态长度必须介于 0 和 20 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=1, max=50, message="收款凭证号长度必须介于 1 和 50 之间")
	public String getVoucherCollection() {
		return voucherCollection;
	}

	public void setVoucherCollection(String voucherCollection) {
		this.voucherCollection = voucherCollection;
	}
	
	@Length(min=1, max=50, message="结算凭证号长度必须介于 1 和 50 之间")
	public String getVoucherSettlement() {
		return voucherSettlement;
	}

	public void setVoucherSettlement(String voucherSettlement) {
		this.voucherSettlement = voucherSettlement;
	}
	
}