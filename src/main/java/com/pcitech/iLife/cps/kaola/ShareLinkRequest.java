package com.pcitech.iLife.cps.kaola;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;

public class ShareLinkRequest {
	private 	String	trackingCode1	;//	渠道参数1
	private 	String	trackingCode2	;//	渠道参数2
	private List<String> linkList = new ArrayList<String>(); //需要转链的链接，必须是考拉链接。 List<String> 转JSON传输

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

	public List<String> getLinkList() {
		return linkList;
	}

	public void setLinkList(List<String> linkList) {
		this.linkList = linkList;
	}
	
	public void addLink(String link) {
		this.linkList.add(link);
	}

	/**
	 * 组装参数。注意需要默认预置调用方法
	 * @return
	 */
	public TreeMap<String,String> getMap() {
		TreeMap<String,String> map = new TreeMap<String,String>();
		map.put("method", "kaola.zhuanke.api.queryShareLink");
		if(trackingCode1!=null)map.put("trackingCode1", ""+trackingCode1);
		if(trackingCode2!=null)map.put("trackingCode2", ""+trackingCode2);
		map.put("linkList", JSON.toJSONString(linkList));
		return map;
	}
}
