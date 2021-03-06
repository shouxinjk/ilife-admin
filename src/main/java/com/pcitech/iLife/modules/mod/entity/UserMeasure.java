/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 用户属性定义Entity
 * @author qchzhu
 * @version 2019-03-13
 */
public class UserMeasure extends DataEntity<UserMeasure> {
	
	private static final long serialVersionUID = 1L;
	private UserCategory category;		// category
	private String dimension;		// dimension
	private String property;		// property
	private String name;		// name
	private String weight;		// weight
	private String controlValue;		// control_value
	private String defaultScore;		// default_score
	private String defaultRank;		// default_rank
	private String type;		// type
	private String tags;		// tags
	private UserCategory autoLabelCategory;		// 自动标注引用类别
	private String autoLabelDict;		// 自动标注引用字典
	private String autoLabelType;		// 自动标注引用字典
	
	public UserMeasure() {
		super();
	}

	public UserMeasure(String id){
		super(id);
	}

	public UserCategory getCategory() {
		return category;
	}

	public void setCategory(UserCategory category) {
		this.category = category;
	}
	
	@Length(min=0, max=100, message="dimension长度必须介于 0 和 100 之间")
	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
	
	@Length(min=0, max=100, message="property长度必须介于 0 和 100 之间")
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	@Length(min=0, max=100, message="name长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	public String getControlValue() {
		return controlValue;
	}

	public void setControlValue(String controlValue) {
		this.controlValue = controlValue;
	}
	
	public String getDefaultScore() {
		return defaultScore;
	}

	public void setDefaultScore(String defaultScore) {
		this.defaultScore = defaultScore;
	}
	
	@Length(min=0, max=8, message="default_rank长度必须介于 0 和 8 之间")
	public String getDefaultRank() {
		return defaultRank;
	}

	public void setDefaultRank(String defaultRank) {
		this.defaultRank = defaultRank;
	}
	
	public UserCategory getAutoLabelCategory() {
		return autoLabelCategory;
	}

	public void setAutoLabelCategory(UserCategory autoLabelCategory) {
		this.autoLabelCategory = autoLabelCategory;
	}

	public String getAutoLabelDict() {
		return autoLabelDict;
	}

	public void setAutoLabelDict(String autoLabelDict) {
		this.autoLabelDict = autoLabelDict;
	}

	public String getAutoLabelType() {
		return autoLabelType;
	}

	public void setAutoLabelType(String autoLabelType) {
		this.autoLabelType = autoLabelType;
	}

	@Length(min=0, max=100, message="type长度必须介于 0 和 100 之间")
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