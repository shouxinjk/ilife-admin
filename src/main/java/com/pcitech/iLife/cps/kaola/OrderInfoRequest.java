package com.pcitech.iLife.cps.kaola;

import java.util.TreeMap;

public class OrderInfoRequest {
	private 	Integer	type	;//	1:根据下单时间段查询；2:根据订单号查询； 3:根据更新时间查询
	private 	Long	 startDate	;//	毫秒数，查询开始时间；Type为1，3必填
	private 	Long	 endDate	;//	毫秒数，查询结束时间；Type为1，3必填
	private 	String	orderId	;//	订单号，Type为2必填
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getStartDate() {
		return startDate;
	}
	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}
	public Long getEndDate() {
		return endDate;
	}
	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	/**
	 * 组装参数。注意需要默认预置调用方法
	 * @return
	 */
	public TreeMap<String,String> getMap() {
		TreeMap<String,String> map = new TreeMap<String,String>();
		map.put("method", "kaola.zhuanke.api.queryOrderInfo");
		if(type!=null)map.put("type", ""+type);
		if(startDate!=null)map.put("startDate", ""+startDate);
		if(endDate!=null)map.put("endDate", ""+endDate);
		if(orderId!=null)map.put("orderId", ""+orderId);
		return map;
	}
	
}
