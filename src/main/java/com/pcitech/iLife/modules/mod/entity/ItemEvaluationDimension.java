/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 主观评价-维度Entity
 * @author qchzhu
 * @version 2018-12-14
 */
public class ItemEvaluationDimension extends DataEntity<ItemEvaluationDimension> {
	
	private static final long serialVersionUID = 1L;
	private ItemEvaluation evaluation;		// 主观评价编号
	private ItemDimension dimension;		// 客观评价编号
	private String name;		// name
	private String description;		// description
	private double weight;		// weight
	private String sort;		// sort
	private ItemCategory category;		// category
	
	public ItemEvaluationDimension() {
		super();
	}

	public ItemEvaluationDimension(String id){
		super(id);
	}

	@JsonBackReference
	@NotNull(message="主观评价编号不能为空")
	public ItemEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(ItemEvaluation evaluation) {
		this.evaluation = evaluation;
	}
	
	@JsonBackReference
	@NotNull(message="客观评价编号不能为空")
	public ItemDimension getDimension() {
		return dimension;
	}

	public void setDimension(ItemDimension dimension) {
		this.dimension = dimension;
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
	
	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
}