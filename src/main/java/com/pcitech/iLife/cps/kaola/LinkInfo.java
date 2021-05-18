package com.pcitech.iLife.cps.kaola;

public class LinkInfo {
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public String getShortShareUrl() {
		return shortShareUrl;
	}
	public void setShortShareUrl(String shortShareUrl) {
		this.shortShareUrl = shortShareUrl;
	}
	public String getGoodsDetailUrl() {
		return goodsDetailUrl;
	}
	public void setGoodsDetailUrl(String goodsDetailUrl) {
		this.goodsDetailUrl = goodsDetailUrl;
	}
	public String getGroupBuyShareUrl() {
		return groupBuyShareUrl;
	}
	public void setGroupBuyShareUrl(String groupBuyShareUrl) {
		this.groupBuyShareUrl = groupBuyShareUrl;
	}
	public String getGroupBuyShortShareUrl() {
		return groupBuyShortShareUrl;
	}
	public void setGroupBuyShortShareUrl(String groupBuyShortShareUrl) {
		this.groupBuyShortShareUrl = groupBuyShortShareUrl;
	}
	public String getGroupBuyGoodsDetailUrl() {
		return groupBuyGoodsDetailUrl;
	}
	public void setGroupBuyGoodsDetailUrl(String groupBuyGoodsDetailUrl) {
		this.groupBuyGoodsDetailUrl = groupBuyGoodsDetailUrl;
	}
	public String getGoodsPCUrl() {
		return goodsPCUrl;
	}
	public void setGoodsPCUrl(String goodsPCUrl) {
		this.goodsPCUrl = goodsPCUrl;
	}
	public String getMiniShareUrl() {
		return miniShareUrl;
	}
	public void setMiniShareUrl(String miniShareUrl) {
		this.miniShareUrl = miniShareUrl;
	}
	private  	String	shareUrl	;//	分享链接
	private  	String	shortShareUrl	;//	分享短连接
	private  	String	goodsDetailUrl	;//	商品详情页
	private  	String	groupBuyShareUrl	;//	拼团分享链接。如果是拼团商品
	private  	String	groupBuyShortShareUrl	;//	拼团分享短链。如果是拼团商品
	private  	String	groupBuyGoodsDetailUrl	;//	拼团商详页。如果是拼团商品
	private  	String	goodsPCUrl	;//	pc端地址
	private  	String	miniShareUrl	;//	微信小程序唤醒二维码链接
}
