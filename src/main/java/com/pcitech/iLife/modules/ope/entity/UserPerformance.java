/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;

/**
 * 用户属性标注Entity
 * @author qchzhu
 * @version 2020-05-07
 */
public class UserPerformance extends DataEntity<UserPerformance> {
	
	private static final long serialVersionUID = 1L;
	private UserMeasure measure;		// 属性
	private Double markedValue;		// 标注值
	private String originalValue;		// 原始值
	private Double normalizedValue;		// 归一化参考值
	private Double controlValue;		// 控制值
	private Integer level;		// 等级
	private UserDimension dimension;		// 所属维度
	
	public UserPerformance() {
		super();
	}

	public UserPerformance(String id){
		super(id);
	}

	public UserMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(UserMeasure measure) {
		this.measure = measure;
	}
	
	public Double getMarkedValue() {
		return markedValue;
	}

	public void setMarkedValue(Double markedValue) {
		this.markedValue = markedValue;
	}
	
	@Length(min=0, max=1024, message="原始值长度必须介于 0 和 1024 之间")
	public String getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}
	
	public Double getNormalizedValue() {
		return normalizedValue;
	}

	public void setNormalizedValue(Double normalizedValue) {
		this.normalizedValue = normalizedValue;
	}
	
	public Double getControlValue() {
		return controlValue;
	}

	public void setControlValue(Double controlValue) {
		this.controlValue = controlValue;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public UserDimension getDimension() {
		return dimension;
	}

	public void setDimension(UserDimension dimension) {
		this.dimension = dimension;
	}
	
}