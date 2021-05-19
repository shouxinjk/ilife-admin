package com.pcitech.iLife.cps.kaola;

import java.math.BigDecimal;
import java.util.List;

public class GoodItem {
	private 	String	itemId	;//	itemId （唯一性保证）
	private 	Long	goodsId	;//	商品ID（不唯一）
	private 	String	goodsName	;//	商品名称
	private 	Integer	buyCount	;//	购买件数
	private 	BigDecimal	commissionRate	;//	佣金比例
	private 	Long	categoryId	;//	一级类目id
	private 	String	categoryName	;//	一级类目名称
	private 	BigDecimal	unitPrice	;//	商品单价（纯计算佣金的单价）
	private 	BigDecimal	commissionAmount	;//	该商品佣金
	private 	BigDecimal	refundAmount	;//	退款金额
	private 	List<CategoryInfo>	categoryInfoList	;//	商品所有类目信息，同商品信息返回对象中的类目信息。type = 2返回
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public Integer getBuyCount() {
		return buyCount;
	}
	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}
	public BigDecimal getCommissionRate() {
		return commissionRate;
	}
	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}
	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}
	public BigDecimal getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}
	public List<CategoryInfo> getCategoryInfoList() {
		return categoryInfoList;
	}
	public void setCategoryInfoList(List<CategoryInfo> categoryInfoList) {
		this.categoryInfoList = categoryInfoList;
	}
	
	
}
