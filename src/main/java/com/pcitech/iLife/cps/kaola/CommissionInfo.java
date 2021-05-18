package com.pcitech.iLife.cps.kaola;

import java.math.BigDecimal;

public class CommissionInfo {
	public BigDecimal getCommissionRate() {
		return commissionRate;
	}
	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}
	public BigDecimal getGroupBuyCommissionRate() {
		return groupBuyCommissionRate;
	}
	public void setGroupBuyCommissionRate(BigDecimal groupBuyCommissionRate) {
		this.groupBuyCommissionRate = groupBuyCommissionRate;
	}
	public Integer getExpireType() {
		return expireType;
	}
	public void setExpireType(Integer expireType) {
		this.expireType = expireType;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Integer getGroupBuyExpireType() {
		return groupBuyExpireType;
	}
	public void setGroupBuyExpireType(Integer groupBuyExpireType) {
		this.groupBuyExpireType = groupBuyExpireType;
	}
	public String getGroupBuyStartTime() {
		return groupBuyStartTime;
	}
	public void setGroupBuyStartTime(String groupBuyStartTime) {
		this.groupBuyStartTime = groupBuyStartTime;
	}
	public String getGroupBuyEndTime() {
		return groupBuyEndTime;
	}
	public void setGroupBuyEndTime(String groupBuyEndTime) {
		this.groupBuyEndTime = groupBuyEndTime;
	}
	private 	BigDecimal	commissionRate	;//	一般佣金比例
	private 	BigDecimal	groupBuyCommissionRate	;//	拼团佣金比例（如果是拼团商品）
	private 	Integer	expireType	;//	一般佣金有效期:1 长期有效,0 时间段有效
	private 	String	startTime	;//	一般佣金开始时间:yyyy-mm-dd HH:mm:ss
	private 	String	endTime	;//	一般佣金结束时间:yyyy-mm-dd HH:mm:ss
	private 	Integer	groupBuyExpireType	;//	拼团佣金有效期:1 长期有效,0 时间段有效
	private 	String	groupBuyStartTime	;//	拼团佣金开始时间：yyyy-mm-dd HH:mm:ss
	private 	String	groupBuyEndTime	;//	拼团佣金结束时间：yyyy-mm-dd HH:mm:ss
}
