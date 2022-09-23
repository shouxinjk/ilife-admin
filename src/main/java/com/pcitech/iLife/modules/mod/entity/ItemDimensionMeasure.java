/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 客观评价明细Entity
 * @author qchzhu
 * @version 2018-12-12
 */
public class ItemDimensionMeasure extends DataEntity<ItemDimensionMeasure> {
	
	private static final long serialVersionUID = 1L;
	private ItemDimension dimension;		// 客观评价
	private Measure measure;		// 关键属性
	private String name;		// name
	private String description;		// description
	private double weight;		// weight
	private int sort=10;		// sort
	private ItemCategory category;		// category
	
	public ItemDimensionMeasure() {
		super();
	}

	public ItemDimensionMeasure(String id){
		super(id);
	}

	public ItemDimension getDimension() {
		return dimension;
	}

	public void setDimension(ItemDimension dimension) {
		this.dimension = dimension;
	}
	
	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
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
	
	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
}