/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 类目专家授权Entity
 * @author ilife
 * @version 2022-12-27
 */
public class CategoryBroker extends DataEntity<CategoryBroker> {
	
	private static final long serialVersionUID = 1L;
	private ItemCategory category;		// 关联的品类
	private Broker broker;		// 关联的达人
	private Badge badge;		// 关联的勋章
	
	public CategoryBroker() {
		super();
	}

	public CategoryBroker(String id){
		super(id);
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
	public Badge getBadge() {
		return badge;
	}

	public void setBadge(Badge badge) {
		this.badge = badge;
	}
	
}