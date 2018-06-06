/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 资本标注Entity
 * @author chenci
 * @version 2017-09-15
 */
public class Capability extends DataEntity<Capability> {
	
	private static final long serialVersionUID = 1L;
	private Capital capital;		// capital_id
	private Double markedValue;		// marked_value
	private Double normalizedValue;		// normalized_value
	private Double descValue;		// control_value
	private Double originalValue;		// original_value
	private Integer level;		// level
	private String description;		// description
	
	public Capability() {
		super();
	}

	public Capability(String id){
		super(id);
	}
	
	public Capital getCapital() {
		return capital;
	}

	public void setCapital(Capital capital) {
		this.capital = capital;
	}

	public Double getMarkedValue() {
		return markedValue;
	}

	public void setMarkedValue(Double markedValue) {
		this.markedValue = markedValue;
	}
	
	public Double getNormalizedValue() {
		return normalizedValue;
	}

	public void setNormalizedValue(Double normalizedValue) {
		this.normalizedValue = normalizedValue;
	}
	
	public Double getDescValue() {
		return descValue;
	}

	public void setDescValue(Double descValue) {
		this.descValue = descValue;
	}

	public Double getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(Double originalValue) {
		this.originalValue = originalValue;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
	@Length(min=0, max=1000, message="description长度必须介于 0 和 1000 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}