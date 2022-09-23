/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 画像需要构成Entity
 * @author qchzhu
 * @version 2020-04-30
 */
public class PersonaNeed extends DataEntity<PersonaNeed> {
	
	private static final long serialVersionUID = 1L;
	private String description;		// 概要描述
	private Double weight;		// 满足度
	private Persona persona;		// 关联的画像
	private Motivation need;		// 关联的动机
	private Phase phase;	//所属阶段
	private String expression;		// 满足度表达式
	private int sort=10;		// sort
	private String needCategory=""; //需要的分类目录。注意：不能直接查询得到，需要前端组织
	
	public PersonaNeed() {
		super();
	}

	public PersonaNeed(String id){
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
	
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}
	
	public Motivation getNeed() {
		return need;
	}

	public void setNeed(Motivation need) {
		this.need = need;
	}
	
	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	@Length(min=0, max=1024, message="满足度表达式长度必须介于 0 和 1024 之间")
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getNeedCategory() {
		return needCategory;
	}

	public void setNeedCategory(String needCategory) {
		this.needCategory = needCategory;
	}
	
}