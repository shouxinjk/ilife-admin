package com.pcitech.iLife.cps.kaola;

import java.util.List;

public class ShareLinkResponse {
	private 	Integer	code	;//	返回码200  正常
	private 	String	msg	;//	描述  SUCCESS
	private 	List<ShareLinkInfo>	data	;//	转链列表
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
	public List<ShareLinkInfo> getData() {
		return data;
	}
	public void setData(List<ShareLinkInfo> data) {
		this.data = data;
	}
	
}
