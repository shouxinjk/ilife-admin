package com.pcitech.iLife.cps.kaola;

import java.util.TreeMap;

public class GoodsInfoRequest {
	private 	String	goodsIds	;//	商品ID列表，多个ID用英文逗号分隔，每次限制20
	private 	String	trackingCode1	;//	渠道参数1
	private 	String	trackingCode2	;//	渠道参数2
	private 	int	type	=0;//	0 按照goodsIds维度（默认），1 按照goodsUrl
	private 	String	goodsUrl	;//	解析出url中商品ID，每次只能传一个
	private 	String	needShortLink	;//	是否需要短链接。N:不需要（响应速度快），其他：需要
	private 	String	needGroupBuyInfo	;//	是否需要拼团信息。N:不需要，其他：需要
	public String getGoodsIds() {
		return goodsIds;
	}
	public void setGoodsIds(String goodsIds) {
		this.goodsIds = goodsIds;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getGoodsUrl() {
		return goodsUrl;
	}
	public void setGoodsUrl(String goodsUrl) {
		this.goodsUrl = goodsUrl;
	}
	public String getNeedShortLink() {
		return needShortLink;
	}
	public void setNeedShortLink(String needShortLink) {
		this.needShortLink = needShortLink;
	}
	public String getNeedGroupBuyInfo() {
		return needGroupBuyInfo;
	}
	public void setNeedGroupBuyInfo(String needGroupBuyInfo) {
		this.needGroupBuyInfo = needGroupBuyInfo;
	}
	
	/**
	 * 组装参数。注意需要默认预置调用方法
	 * @return
	 */
	public TreeMap<String,String> getMap() {
		TreeMap<String,String> map = new TreeMap<String,String>();
		map.put("method", "kaola.zhuanke.api.queryGoodsInfo");
		map.put("type", ""+type);//必填参数
		if(goodsIds!=null)map.put("goodsIds", ""+goodsIds);
		if(trackingCode1!=null)map.put("trackingCode1", ""+trackingCode1);
		if(trackingCode2!=null)map.put("trackingCode2", ""+trackingCode2);
		if(goodsUrl!=null)map.put("goodsUrl", ""+goodsUrl);
		if(needShortLink!=null)map.put("needShortLink", ""+needShortLink);
		if(needGroupBuyInfo!=null)map.put("needGroupBuyInfo", ""+needGroupBuyInfo);
		return map;
	}
}
