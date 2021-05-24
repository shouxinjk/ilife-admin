package com.pcitech.iLife.cps.kaola;

import java.math.BigDecimal;
import java.util.List;

public class OrderInfoPush {
	private String messageId;//唯一ID
	/**
	 * 推送类型
	PAYSUCCESS：支付
	ORDERSENDED： 发货
	TRADESUCCESS：交易成功
	TRADEFAILURE：交易失败
	SETTLED:结算
	 */
	private String messageType;//推送类型
	private 	String	orderId	;//	订单号
	private 	String	parentOrderId	;//	大订单号（整体下单单号，不能做查询依据）
	private 	String	trackingCode1	;//	渠道参数1
	private 	String	trackingCode2	;//	渠道参数2
	private 	String	orderTime	;//	下单时间
	private 	String	payTime	;//	支付时间
	private 	String	lastUpdateTime	;//	最后更新时间
	private 	Integer	orderStatus	;//	订单状态。1已支付，2已发货，3交易成功，4交易失败
	private 	Integer	hasAfterSale	;//	是否售后退款。0 否 1 是
	private 	BigDecimal	payAmount	;//	支付金额
	private 	BigDecimal	actualPayAmount	;//	计算佣金金额
	private 	BigDecimal	commissionAmount	;//	佣金
	private 	Integer	settleStatus	;//	结算状态。0 未结算 1 已结算 2 已失效
	private 	String	noCommissionReason	;//	订单佣金失效原因
	private 	Integer	newAccount	;//	是否新客首单。0 否 1 是
	private 	List<GoodItemPush>	goodItemList	;//	订单商品信息
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getParentOrderId() {
		return parentOrderId;
	}
	public void setParentOrderId(String parentOrderId) {
		this.parentOrderId = parentOrderId;
	}
	public String getTrackingCode1() {
		return trackingCode1;
	}
	public void setTrackingCode1(String trackingCode1) {
		this.trackingCode1 = trackingCode1;
	}
	public String getTrackingCode2() {
		return trackingCode2;
	}
	public void setTrackingCode2(String trackingCode2) {
		this.trackingCode2 = trackingCode2;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Integer getHasAfterSale() {
		return hasAfterSale;
	}
	public void setHasAfterSale(Integer hasAfterSale) {
		this.hasAfterSale = hasAfterSale;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public BigDecimal getActualPayAmount() {
		return actualPayAmount;
	}
	public void setActualPayAmount(BigDecimal actualPayAmount) {
		this.actualPayAmount = actualPayAmount;
	}
	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}
	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}
	public Integer getSettleStatus() {
		return settleStatus;
	}
	public void setSettleStatus(Integer settleStatus) {
		this.settleStatus = settleStatus;
	}
	public String getNoCommissionReason() {
		return noCommissionReason;
	}
	public void setNoCommissionReason(String noCommissionReason) {
		this.noCommissionReason = noCommissionReason;
	}
	public Integer getNewAccount() {
		return newAccount;
	}
	public void setNewAccount(Integer newAccount) {
		this.newAccount = newAccount;
	}

	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public List<GoodItemPush> getGoodItemList() {
		return goodItemList;
	}
	public void setGoodItemList(List<GoodItemPush> goodItemList) {
		this.goodItemList = goodItemList;
	}
	
	
}
