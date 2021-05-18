package com.pcitech.iLife.cps.kaola;

import java.math.BigDecimal;

public class PriceInfo {

	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}
	public BigDecimal getNewUserPrice() {
		return newUserPrice;
	}
	public void setNewUserPrice(BigDecimal newUserPrice) {
		this.newUserPrice = newUserPrice;
	}
	public BigDecimal getMemberCurrentPrice() {
		return memberCurrentPrice;
	}
	public void setMemberCurrentPrice(BigDecimal memberCurrentPrice) {
		this.memberCurrentPrice = memberCurrentPrice;
	}
	public BigDecimal getGroupBuyPrice() {
		return groupBuyPrice;
	}
	public void setGroupBuyPrice(BigDecimal groupBuyPrice) {
		this.groupBuyPrice = groupBuyPrice;
	}
	public BigDecimal getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}
	public BigDecimal getMemberPriceSpread() {
		return memberPriceSpread;
	}
	public void setMemberPriceSpread(BigDecimal memberPriceSpread) {
		this.memberPriceSpread = memberPriceSpread;
	}
	public String getDiscountLabel() {
		return discountLabel;
	}
	public void setDiscountLabel(String discountLabel) {
		this.discountLabel = discountLabel;
	}
	private 	BigDecimal	currentPrice	;//	当前价格(普通会员享受的价格)
	private 	BigDecimal	newUserPrice	;//	新用户促销价格
	private 	BigDecimal	memberCurrentPrice	;//	黑卡会员专享价
	private 	BigDecimal	groupBuyPrice	;//	拼团价格（如果是拼团商品）
	private 	BigDecimal	marketPrice	;//	市场价
	private 	BigDecimal	memberPriceSpread	;//	黑卡会员与p普通会员价差（currentPrice-memberCurrentPrice）
	private 	String	discountLabel	;//	折扣(暂无)
}
