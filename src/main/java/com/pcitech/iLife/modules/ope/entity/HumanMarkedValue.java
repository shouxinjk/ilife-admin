/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Measure;

/**
 * 数据标注Entity
 * @author chenci
 * @version 2017-09-28
 */
public class HumanMarkedValue extends DataEntity<HumanMarkedValue> {
	
	private static final long serialVersionUID = 1L;
	private String dimension;		// 维度
	private Double value;		// 数值
	private String originalValue;		// 数值
	private Person person;		// 用户
	private Item item;		// 商品
	private Measure measure;		// 关键属性
	
	public HumanMarkedValue() {
		super();
	}

	public HumanMarkedValue(String id){
		super(id);
	}

	@Length(min=0, max=255, message="维度长度必须介于 0 和 255 之间")
	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
	
	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	
}