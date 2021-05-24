package com.pcitech.iLife.cps.kaola;

import java.util.TreeMap;

public class QueryRecommendGoodsListRequest {
	Long categoryId;//类目ID,支持两级类目
	Integer sortType	=1;//1:按佣金比例排序 默认,2:按佣金排序,3:按黑卡价格排序,4:按普通价格排序,5:按30天黑卡用户销量排序,6:按黑卡价差进行排序
	Integer desc=0;//0倒序 默认,1 升序 
	Integer pageIndex=1;	//页码。起始下标1，默认为1
	Integer pageSize=1000;//每页数量,默认 1000（最大值）,最小20
	Integer self=0;//0 全部,1 自营,2 非自营
	
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Integer getSortType() {
		return sortType;
	}
	public void setSortType(Integer sortType) {
		this.sortType = sortType;
	}
	public Integer getDesc() {
		return desc;
	}
	public void setDesc(Integer desc) {
		this.desc = desc;
	}
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getSelf() {
		return self;
	}
	public void setSelf(Integer self) {
		this.self = self;
	}

	/**
	 * 组装参数。注意需要默认预置调用方法
	 * @return
	 */
	public TreeMap<String,String> getMap() {
		TreeMap<String,String> map = new TreeMap<String,String>();
		map.put("method", "kaola.zhuanke.api.queryRecommendGoodsList");
		map.put("categoryId", ""+categoryId);//必填参数
		map.put("sortType", ""+sortType);
		map.put("desc", ""+desc);
		map.put("pageIndex", ""+pageIndex);
		map.put("pageSize", ""+pageSize);
		map.put("self", ""+self);
		return map;
	}
	
}
