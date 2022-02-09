/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 阶段需要构成Entity
 * @author ilife
 * @version 2022-02-09
 */
public class PhaseNeed extends DataEntity<PhaseNeed> {
	
	private static final long serialVersionUID = 1L;
	private String description;		// 概要描述
	private double weight;		// 满足度
	private Phase phase;		// 所属阶段
	private Motivation need;		// 关联的动机
	private String expression;		// 满足度表达式
	private String sort;		// sort
	private String needCategory=""; //需要的分类目录。注意：不能直接查询得到，需要前端组织
	
	public PhaseNeed() {
		super();
	}

	public PhaseNeed(String id){
		super(id);
	}

	@Length(min=0, max=255, message="概要描述长度必须介于 0 和 255 之间")
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
	
	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	
	@NotNull(message="关联的动机不能为空")
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

	public String getNeedCategory() {
		return needCategory;
	}

	public void setNeedCategory(String needCategory) {
		this.needCategory = needCategory;
	}
	
}