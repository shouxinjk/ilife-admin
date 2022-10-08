/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;

/**
 * 标注Entity
 * @author chenci
 * @version 2017-09-28
 */
public class Performance extends DataEntity<Performance> {
	
	private static final long serialVersionUID = 1L;
	private ItemCategory category;		// 关键属性
	private Measure measure;		// 关键属性
	private Double markedValue;		// 标注值
	private String originalValue;		// 原始值
	private Double normalizedValue;		// 归一值
	private Double controlValue;		// 对照值
	private Integer level;		// 等级
	private String dimension;		// 所属维度
	private Integer isReady; //是否已经同步：1-已同步 0-待同步
	private Integer isMarked=0; //是否已经手动标注：1-已手动标注 0-待手动标注
	
	public Performance() {
		super();
	}

	public Performance(String id){
		super(id);
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	public Double getMarkedValue() {
		return markedValue;
	}

	public void setMarkedValue(Double markedValue) {
		this.markedValue = markedValue;
	}
	
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
	
	@Length(min=0, max=500, message="所属维度长度必须介于 0 和 500 之间")
	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public Integer getIsReady() {
		return isReady;
	}

	public void setIsReady(Integer isReady) {
		this.isReady = isReady;
	}

	public Integer getIsMarked() {
		return isMarked;
	}

	public void setIsMarked(Integer isMarked) {
		this.isMarked = isMarked;
	}
	
}