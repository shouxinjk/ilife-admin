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
	private String name;		// 真实姓名
	private String description;		//简介
	private String job;		//工作
	private String company;		// 公司院校
	private String status;		//状态
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}