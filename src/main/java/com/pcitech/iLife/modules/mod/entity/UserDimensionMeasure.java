/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 用户客观评价-属性Entity
 * @author qchzhu
 * @version 2019-03-13
 */
public class UserDimensionMeasure extends DataEntity<UserDimensionMeasure> {
	
	private static final long serialVersionUID = 1L;
	private UserDimension dimension;		// 客观评价编号
	private UserMeasure measure;		// 关键属性编号
	private String name;		// name
	private String description;		// description
	private double weight;		// weight
	private String sort;		// sort
	private String category;		// category
	
	public UserDimensionMeasure() {
		super();
	}

	public UserDimensionMeasure(String id){
		super(id);
	}

	public UserDimension getDimension() {
		return dimension;
	}

	public void setDimension(UserDimension dimension) {
		this.dimension = dimension;
	}
	
	public UserMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(UserMeasure measure) {
		this.measure = measure;
	}
	
	@Length(min=0, max=20, message="name长度必须介于 0 和 20 之间")
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
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	@Length(min=0, max=11, message="sort长度必须介于 0 和 11 之间")
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
	@Length(min=0, max=64, message="category长度必须介于 0 和 64 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}