package com.pcitech.iLife.cps.kaola;

import java.util.List;

public class GoodsInfoResponse {
	private 	Integer	code	;//	返回码200  正常
	private 	String	msg	;//	描述  SUCCESS
	private 	List<GoodInfo>	data	;//	商品信息列表
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
	public List<GoodInfo> getData() {
		return data;
	}
	public void setData(List<GoodInfo> data) {
		this.data = data;
	}
	
	
}
