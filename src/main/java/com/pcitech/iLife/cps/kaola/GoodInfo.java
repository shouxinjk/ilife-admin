package com.pcitech.iLife.cps.kaola;

import java.util.List;

public class GoodInfo {
	private 	Long	goodsId	;//	商品ID
	private 	BaseInfo	baseInfo	;//	基础信息
	private 	List<CategoryInfo>	categoryInfo	;//	类目信息
	private 	PriceInfo	priceInfo	;//	价格信息
	private 	ActivityInfo	activityInfo	;//	活动信息
	private 	CommissionInfo	commissionInfo	;//	佣金信息
	private 	LinkInfo	linkInfo	;//	跟踪链接
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public BaseInfo getBaseInfo() {
		return baseInfo;
	}
	public void setBaseInfo(BaseInfo baseInfo) {
		this.baseInfo = baseInfo;
	}
	public List<CategoryInfo> getCategoryInfo() {
		return categoryInfo;
	}
	public void setCategoryInfo(List<CategoryInfo> categoryInfo) {
		this.categoryInfo = categoryInfo;
	}
	public PriceInfo getPriceInfo() {
		return priceInfo;
	}
	public void setPriceInfo(PriceInfo priceInfo) {
		this.priceInfo = priceInfo;
	}
	public ActivityInfo getActivityInfo() {
		return activityInfo;
	}
	public void setActivityInfo(ActivityInfo activityInfo) {
		this.activityInfo = activityInfo;
	}
	public CommissionInfo getCommissionInfo() {
		return commissionInfo;
	}
	public void setCommissionInfo(CommissionInfo commissionInfo) {
		this.commissionInfo = commissionInfo;
	}
	public LinkInfo getLinkInfo() {
		return linkInfo;
	}
	public void setLinkInfo(LinkInfo linkInfo) {
		this.linkInfo = linkInfo;
	}
	
	
}
