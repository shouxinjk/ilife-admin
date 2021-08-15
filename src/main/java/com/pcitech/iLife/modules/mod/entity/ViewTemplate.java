/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 模型展示模板Entity
 * @author qchzhu
 * @version 2021-08-15
 */
public class ViewTemplate extends DataEntity<ViewTemplate> {
	
	private static final long serialVersionUID = 1L;
	private String type;		// 模板适用类型字典定义，如persona、item-article
	private String name;		// 名称
	private String subType;		// 预留。子类型
	private String expression;		// 模板表达式
	private String priority;		// 预留。优先级
	private String description;		// 描述
	
	public ViewTemplate() {
		super();
	}

	public ViewTemplate(String id){
		super(id);
	}

	@Length(min=0, max=64, message="模板适用类型字典定义，如persona、item-article长度必须介于 0 和 64 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=64, message="预留。子类型长度必须介于 0 和 64 之间")
	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Length(min=0, max=8, message="预留。优先级长度必须介于 0 和 8 之间")
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	@Length(min=0, max=1024, message="描述长度必须介于 0 和 1024 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}