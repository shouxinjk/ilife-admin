package com.pcitech.iLife.cps.kaola;

import java.util.List;

public class SelectedGoods {
	private Integer totalRecord;	//总记录数
	private Integer pageNo;	//页码
	private Integer pageSize;	//每页数量
	private List<Long> goodsIdList;//商品Id列表
	public Integer getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(Integer totalRecord) {
		this.totalRecord = totalRecord;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public List<Long> getGoodsIdList() {
		return goodsIdList;
	}
	public void setGoodsIdList(List<Long> goodsIdList) {
		this.goodsIdList = goodsIdList;
	}

	
	
}
