/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * SaaS订阅Entity
 * @author ilife
 * @version 2023-06-13
 */
public class Subscription extends DataEntity<Subscription> {
	
	private static final long serialVersionUID = 1L;
	private Date createTime;		// 创建日期
	private Date updateTime;		// 更新日期
	private String sysOrgCode;		// 所属部门
	private SysTenant tenant;		// 租户
	private Broker broker;			//付款达人：可能为空
	private StoSoftware software;		// 订阅内容
	private StoPricePlan pricePlan;		// 单品订阅计划类型
	private StoSalePackage salePackage; // 套餐类型
	private String payerOpenid; //付款人openid
	private String subscribeType;		// 订阅类型
	private Date effectiveOn;		// 生效时间
	private Date expireOn;		// 失效时间
	private Integer paymentAmount;		// 支付金额
	private Date paymentTime;		// 支付时间
	private String tradeNo;		// 商户订单号
	private String tradeState;		// 交易状态
	private String transactionCode;		// 支付流水号
	private String invoiceId;		// 发票
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	private String businessType;		// 业务类型： 旅游、体检……
	private String userType;		// 用户类型：person、team、company
	private String userName;		// 用户全称：订阅时输入的公司全称或用户姓名
	private String userPhone;		// 用户电话：订阅时输入的电话
	
	public Subscription() {
		super();
	}

	public Subscription(String id){
		super(id);
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Length(min=0, max=64, message="所属部门长度必须介于 0 和 64 之间")
	public String getSysOrgCode() {
		return sysOrgCode;
	}

	public void setSysOrgCode(String sysOrgCode) {
		this.sysOrgCode = sysOrgCode;
	}
	
	public StoSoftware getSoftware() {
		return software;
	}

	public void setSoftware(StoSoftware software) {
		this.software = software;
	}
	
	public StoPricePlan getPricePlan() {
		return pricePlan;
	}

	public void setPricePlan(StoPricePlan pricePlan) {
		this.pricePlan = pricePlan;
	}
	
	
	public StoSalePackage getSalePackage() {
		return salePackage;
	}

	public void setSalePackage(StoSalePackage salePackage) {
		this.salePackage = salePackage;
	}
	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
	public String getPayerOpenid() {
		return payerOpenid;
	}

	public void setPayerOpenid(String payerOpenid) {
		this.payerOpenid = payerOpenid;
	}
	
	@Length(min=0, max=32, message="订阅类型长度必须介于 0 和 32 之间")
	public String getSubscribeType() {
		return subscribeType;
	}

	public void setSubscribeType(String subscribeType) {
		this.subscribeType = subscribeType;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEffectiveOn() {
		return effectiveOn;
	}

	public void setEffectiveOn(Date effectiveOn) {
		this.effectiveOn = effectiveOn;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getExpireOn() {
		return expireOn;
	}

	public void setExpireOn(Date expireOn) {
		this.expireOn = expireOn;
	}
	
	public Integer getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Integer paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}
	
	@Length(min=0, max=100, message="商户订单号长度必须介于 0 和 100 之间")
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	
	@Length(min=0, max=100, message="交易状态长度必须介于 0 和 100 之间")
	public String getTradeState() {
		return tradeState;
	}

	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}
	
	@Length(min=0, max=200, message="支付流水号长度必须介于 0 和 200 之间")
	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	
	@Length(min=0, max=32, message="发票长度必须介于 0 和 32 之间")
	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public SysTenant getTenant() {
		return tenant;
	}

	public void setTenant(SysTenant tenant) {
		this.tenant = tenant;
	}
	
}