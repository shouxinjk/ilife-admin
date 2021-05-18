package com.pcitech.iLife.cps.kaola;

import java.util.List;

public class BaseInfo {
	private 	String	goodsTitle	;//	商品名称
	private 	String	goodsSubTitle	;//	商品副标题
	private 	List<String>	 imageList	;//	商品图片地址
	private 	Integer	self	;//	是否自营：0否 1是
	private 	String	brandName	;//	品牌名
	private 	String	brandCountryName	;//	品牌国简称
	private 	Integer	groupBuyGoods	;//	是否拼团商品：0否 1是
	private 	Integer	onlineStatus	;//	上架状态 ：1上架，0下架
	private 	Integer	interPurch	;//	是否内购：0否 1是
	private 	Integer	store	;//	是否有库存：0否 1是
	private 	List<String>	 detailImgList	;//	页面图文详情中的图片
	private 	Integer	importType	;//	贸易类型：0 直邮 1 保税 2 海淘 3 国内贸易 4 个人清关
	public String getGoodsTitle() {
		return goodsTitle;
	}
	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}
	public String getGoodsSubTitle() {
		return goodsSubTitle;
	}
	public void setGoodsSubTitle(String goodsSubTitle) {
		this.goodsSubTitle = goodsSubTitle;
	}
	public List<String> getImageList() {
		return imageList;
	}
	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}
	public Integer getSelf() {
		return self;
	}
	public void setSelf(Integer self) {
		this.self = self;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getBrandCountryName() {
		return brandCountryName;
	}
	public void setBrandCountryName(String brandCountryName) {
		this.brandCountryName = brandCountryName;
	}
	public Integer getGroupBuyGoods() {
		return groupBuyGoods;
	}
	public void setGroupBuyGoods(Integer groupBuyGoods) {
		this.groupBuyGoods = groupBuyGoods;
	}
	public Integer getOnlineStatus() {
		return onlineStatus;
	}
	public void setOnlineStatus(Integer onlineStatus) {
		this.onlineStatus = onlineStatus;
	}
	public Integer getInterPurch() {
		return interPurch;
	}
	public void setInterPurch(Integer interPurch) {
		this.interPurch = interPurch;
	}
	public Integer getStore() {
		return store;
	}
	public void setStore(Integer store) {
		this.store = store;
	}
	public List<String> getDetailImgList() {
		return detailImgList;
	}
	public void setDetailImgList(List<String> detailImgList) {
		this.detailImgList = detailImgList;
	}
	public Integer getImportType() {
		return importType;
	}
	public void setImportType(Integer importType) {
		this.importType = importType;
	}
	
}
