/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.ope.entity.Person;

/**
 * 用户需要构成Entity
 * @author ilife
 * @version 2022-02-17
 */
public class PersonNeed extends DataEntity<PersonNeed> {
	
	private static final long serialVersionUID = 1L;
	private String description;		// 概要描述
	private Double weight;		// 需要程度
	private Person person;		// 关联的用户
	private Motivation need;		// 关联的动机
	private String expression;		// 满足度表达式
	private String sort="5";		// sort
	
	public PersonNeed() {
		super();
	}

	public PersonNeed(String id){
		super(id);
	}

	@Length(min=0, max=255, message="概要描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	@NotNull(message="关联的用户不能为空")
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
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
	
}