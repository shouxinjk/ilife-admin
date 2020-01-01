/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 推广效果Entity
 * @author qchzhu
 * @version 2019-12-31
 */
public class BrokerPerformance extends DataEntity<BrokerPerformance> {
	
	private static final long serialVersionUID = 1L;
	private Broker broker;		// 达人
	private String taskType;		// 任务类型：daily、weekly、monthly
	private int countShare;		// 分享量
	private int countView;		// 浏览量
	private int countBuy;		// 意向数
	private int countOrder;		// 订单数
	private int countTeam;		// 团队人数
	private double amountOrder;		// 订单佣金
	private double amountTeam;		// 团队佣金
	private double amountBuy;		// 意向激励
	private double amountCredit;		// 积分激励
	private String statusCalc;		// 计算状态
	private String statusNotify;		// 通知状态
	private Date dateCalc;		// 计算完成时间
	private Date dateNotify;		// 通知完成时间
	
	public BrokerPerformance() {
		super();
	}

	public BrokerPerformance(String id){
		super(id);
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
	@Length(min=0, max=20, message="任务类型：daily、weekly、monthly长度必须介于 0 和 20 之间")
	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	public int getCountShare() {
		return countShare;
	}

	public void setCountShare(int countShare) {
		this.countShare = countShare;
	}
	
	public int getCountView() {
		return countView;
	}

	public void setCountView(int countView) {
		this.countView = countView;
	}
	
	public int getCountBuy() {
		return countBuy;
	}

	public void setCountBuy(int countBuy) {
		this.countBuy = countBuy;
	}
	
	public int getCountOrder() {
		return countOrder;
	}

	public void setCountOrder(int countOrder) {
		this.countOrder = countOrder;
	}
	
	public int getCountTeam() {
		return countTeam;
	}

	public void setCountTeam(int countTeam) {
		this.countTeam = countTeam;
	}
	
	public double getAmountOrder() {
		return amountOrder;
	}

	public void setAmountOrder(double amountOrder) {
		this.amountOrder = amountOrder;
	}
	
	public double getAmountTeam() {
		return amountTeam;
	}

	public void setAmountTeam(double amountTeam) {
		this.amountTeam = amountTeam;
	}
	
	public double getAmountBuy() {
		return amountBuy;
	}

	public void setAmountBuy(double amountBuy) {
		this.amountBuy = amountBuy;
	}
	
	public double getAmountCredit() {
		return amountCredit;
	}

	public void setAmountCredit(double amountCredit) {
		this.amountCredit = amountCredit;
	}
	
	@Length(min=0, max=20, message="计算状态长度必须介于 0 和 20 之间")
	public String getStatusCalc() {
		return statusCalc;
	}

	public void setStatusCalc(String statusCalc) {
		this.statusCalc = statusCalc;
	}
	
	@Length(min=0, max=20, message="通知状态长度必须介于 0 和 20 之间")
	public String getStatusNotify() {
		return statusNotify;
	}

	public void setStatusNotify(String statusNotify) {
		this.statusNotify = statusNotify;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDateCalc() {
		return dateCalc;
	}

	public void setDateCalc(Date dateCalc) {
		this.dateCalc = dateCalc;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDateNotify() {
		return dateNotify;
	}

	public void setDateNotify(Date dateNotify) {
		this.dateNotify = dateNotify;
	}
	
}