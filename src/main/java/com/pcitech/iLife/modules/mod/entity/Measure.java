/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 商品属性Entity
 * @author chenci
 * @version 2017-09-22
 */
public class Measure extends DataEntity<Measure> {
	
	private static final long serialVersionUID = 1L;
	private ItemCategory category;		// 类别
	private ItemDimension itemDimension;		// 维度
	private String property;		// 属性定义
	private String name;		// 属性名称
	private Double percentage;		// 属性占比
	private Double controlValue;		// 归一化参照值
	private Double defaultScore;		// 默认评分
	private Integer defaultLevel;		// 默认等级
	private String type;		// 类型
	private String tags;		// 标签
	
	public Measure() {
		super();
	}

	public Measure(String id){
		super(id);
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	public ItemDimension getItemDimension() {
		return itemDimension;
	}

	public void setItemDimension(ItemDimension itemDimension) {
		this.itemDimension = itemDimension;
	}

	@Length(min=0, max=100, message="属性定义长度必须介于 0 和 100 之间")
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	@Length(min=0, max=100, message="属性名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public Double getControlValue() {
		return controlValue;
	}

	public void setControlValue(Double controlValue) {
		this.controlValue = controlValue;
	}

	public Double getDefaultScore() {
		return defaultScore;
	}

	public void setDefaultScore(Double defaultScore) {
		this.defaultScore = defaultScore;
	}

	public Integer getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(Integer defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	@Length(min=0, max=100, message="类型长度必须介于 0 和 100 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
}