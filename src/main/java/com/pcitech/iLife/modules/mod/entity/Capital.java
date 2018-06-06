/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 资本类型Entity
 * @author chenci
 * @version 2017-09-15
 */
public class Capital extends DataEntity<Capital> {
	
	private static final long serialVersionUID = 1L;
	private String category;		// category
	private String name;		// name
	private Double percentage;		// percentage
	private String property;		// property
	private Double defaultValue;		// default_value
	private Integer defaultLevel;		// default_level
	private Double controlValue;
	private String description;		// description
	
	public Capital() {
		super();
	}

	public Capital(String id){
		super(id);
	}

	@Length(min=0, max=10, message="category长度必须介于 0 和 10 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Length(min=0, max=100, message="name长度必须介于 0 和 100 之间")
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
	
	@Length(min=0, max=200, message="property长度必须介于 0 和 200 之间")
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	public Double getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Double defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Integer getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(Integer defaultLevel) {
		this.defaultLevel = defaultLevel;
	}
	
	@Length(min=0, max=1000, message="description长度必须介于 0 和 1000 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getControlValue() {
		return controlValue;
	}

	public void setControlValue(Double controlValue) {
		this.controlValue = controlValue;
	}
	
}