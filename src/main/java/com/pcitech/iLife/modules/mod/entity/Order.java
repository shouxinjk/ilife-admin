/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 订单管理Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class Order extends DataEntity<Order> {
	
	private static final long serialVersionUID = 1L;
	private String platform;		// 电商平台
	private String orderNo;		// 原始订单号
	private String traceCode;		// 跟踪码
	private String amount;		// 订单金额
	private String commissionEstimate;		// 预估佣金
	private String commissionSettlement;		// 结算佣金
	private String item;		// 商品名称
	private Date orderTime;		// 订单完成时间
	private Broker broker;		// 达人
	private String notification;		// 通知状态
	private String status;		// 订单状态
	
	public Order() {
		super();
	}

	public Order(String id){
		super(id);
	}

	@Length(min=1, max=50, message="电商平台长度必须介于 1 和 50 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=0, max=50, message="原始订单号长度必须介于 0 和 50 之间")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	@Length(min=0, max=50, message="跟踪码长度必须介于 0 和 50 之间")
	public String getTraceCode() {
		return traceCode;
	}

	public void setTraceCode(String traceCode) {
		this.traceCode = traceCode;
	}
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getCommissionEstimate() {
		return commissionEstimate;
	}

	public void setCommissionEstimate(String commissionEstimate) {
		this.commissionEstimate = commissionEstimate;
	}
	
	public String getCommissionSettlement() {
		return commissionSettlement;
	}

	public void setCommissionSettlement(String commissionSettlement) {
		this.commissionSettlement = commissionSettlement;
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

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
	@Length(min=1, max=1, message="通知状态长度必须介于 1 和 1 之间")
	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}
	
	@Length(min=0, max=20, message="订单状态长度必须介于 0 和 20 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}