package com.pcitech.iLife.cps.kaola;

public class QuerySelectedGoodsResponse {
	private 	Integer	code	;//	返回码200  正常
	private 	String	msg	;//	描述  SUCCESS
	private SelectedGoods	data	;//	返回商品列表
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public SelectedGoods getData() {
		return data;
	}
	public void setData(SelectedGoods data) {
		this.data = data;
	}
	
	
	
}
