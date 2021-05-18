package com.pcitech.iLife.cps.kaola;

import java.math.BigDecimal;

public class ActivityInfo {
	private 	Integer	noPostage	;//	是否包邮：0否，1是
	private 	String	activityLable	;//	活动标签（多个标签逗号分隔）
	private 	Integer	singleTaxReduction	;//	是否单品减税：0否，1是(暂无)
	private 	BigDecimal	singleTaxRate	;//	单品减税比例(暂无)
	private 	Integer	fullTaxReduction	;//	是否满额减税：0否，1是(暂无)
	private 	BigDecimal	FullTaxMoney	;//	满减需达到额度(暂无)
	private 	BigDecimal	FullTaxRate	;//	满额减税比例(暂无)
	public Integer getNoPostage() {
		return noPostage;
	}
	public void setNoPostage(Integer noPostage) {
		this.noPostage = noPostage;
	}
	public String getActivityLable() {
		return activityLable;
	}
	public void setActivityLable(String activityLable) {
		this.activityLable = activityLable;
	}
	public Integer getSingleTaxReduction() {
		return singleTaxReduction;
	}
	public void setSingleTaxReduction(Integer singleTaxReduction) {
		this.singleTaxReduction = singleTaxReduction;
	}
	public BigDecimal getSingleTaxRate() {
		return singleTaxRate;
	}
	public void setSingleTaxRate(BigDecimal singleTaxRate) {
		this.singleTaxRate = singleTaxRate;
	}
	public Integer getFullTaxReduction() {
		return fullTaxReduction;
	}
	public void setFullTaxReduction(Integer fullTaxReduction) {
		this.fullTaxReduction = fullTaxReduction;
	}
	public BigDecimal getFullTaxMoney() {
		return FullTaxMoney;
	}
	public void setFullTaxMoney(BigDecimal fullTaxMoney) {
		FullTaxMoney = fullTaxMoney;
	}
	public BigDecimal getFullTaxRate() {
		return FullTaxRate;
	}
	public void setFullTaxRate(BigDecimal fullTaxRate) {
		FullTaxRate = fullTaxRate;
	}
}
