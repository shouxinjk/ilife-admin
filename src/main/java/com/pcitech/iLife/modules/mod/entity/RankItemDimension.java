/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 排行榜条目Entity
 * @author ilife
 * @version 2023-01-11
 */
public class RankItemDimension extends DataEntity<RankItemDimension> implements Comparable<RankItemDimension> {
	
	private static final long serialVersionUID = 1L;
	private Rank rank;		// 排行榜
	private ItemDimension dimension;		// 评价维度
	private Double priority;		// 优先级
	
	public RankItemDimension() {
		super();
	}

	public RankItemDimension(String id){
		super(id);
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}
	
	public ItemDimension getDimension() {
		return dimension;
	}

	public void setDimension(ItemDimension dimension) {
		this.dimension = dimension;
	}
	
	public Double getPriority() {
		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(RankItemDimension item) {
		return item.getPriority() - this.priority>=0?1:-1;
	}
	
}