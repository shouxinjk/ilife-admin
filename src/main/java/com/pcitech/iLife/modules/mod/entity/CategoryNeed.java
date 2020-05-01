/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 品类需要满足Entity
 * @author qchzhu
 * @version 2020-04-30
 */
public class CategoryNeed extends DataEntity<CategoryNeed> {
	
	private static final long serialVersionUID = 1L;
	private String description;		// 概要描述
	private Double weight;		// 满足度
	private ItemCategory category;		// 关联的品类
	private Motivation need;		// 关联的动机
	private String expression;		// 满足度表达式
	private String sort;		// sort
	
	public CategoryNeed() {
		super();
	}

	public CategoryNeed(String id){
		super(id);
	}

	@Length(min=0, max=255, message="概要描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@NotNull(message="满足度不能为空")
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
	public Motivation getNeed() {
		return need;
	}

	public void setNeed(Motivation need) {
		this.need = need;
	}
	
	@Length(min=0, max=1024, message="满足度表达式长度必须介于 0 和 1024 之间")
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Length(min=0, max=11, message="sort长度必须介于 0 和 11 之间")
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
}