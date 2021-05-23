package com.pcitech.iLife.cps.kaola;

import java.util.TreeMap;

public class QuerySelectedGoodsRequest {
//	1-每日平价商品
//	2-高佣必推商品
//	3-新人专享商品
//	4-会员专属商品
//	5-低价包邮商品
//	6-考拉自营爆款
//	7-考拉商家爆款
//	8-黑卡用户最爱买商品
//	9-美妆个护热销品
//	10-食品保健热销品
//	11-母婴热销品
//	12-时尚热销品
//	13-家居宠物热销品
//	14-每日秒杀商品
//	15-黑卡好价商品
//	16-高转化好物商品
//	17-开卡一单省回商品
//	18-会员专属促销选品池
//	19-好券商品
	private String poolName	;
	private Integer pageNo=0;//页码
	private Integer pageSize=100;//每页数量 ,默认200（最大值）,最小20
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
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

	/**
	 * 组装参数。注意需要默认预置调用方法
	 * @return
	 */
	public TreeMap<String,String> getMap() {
		TreeMap<String,String> map = new TreeMap<String,String>();
		map.put("method", "kaola.zhuanke.api.querySelectedGoods");
		map.put("poolName", poolName);//必填参数
		map.put("pageNo", ""+pageNo);
		map.put("pageSize", ""+pageSize);
		return map;
	}
	
}
