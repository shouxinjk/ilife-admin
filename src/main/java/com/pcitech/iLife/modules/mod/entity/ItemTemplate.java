/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 类目推广文案Entity
 * @author qchzhu
 * @version 2021-07-21
 */
public class ItemTemplate extends DataEntity<ItemTemplate> {
	
	private static final long serialVersionUID = 1L;
	private ItemCategory category;		// 所属类目
	private String name;		// 名称
	private String condition;		// 适用条件表达式
	private String expression;		// 规则脚本表达式
	private String priority;		// 优先级
	private String description;		// 描述
	
	public ItemTemplate() {
		super();
	}

	public ItemTemplate(String id){
		super(id);
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=1024, message="适用条件表达式长度必须介于 0 和 1024 之间")
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	@Length(min=0, max=1024, message="规则脚本表达式长度必须介于 0 和 1024 之间")
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Length(min=0, max=8, message="优先级长度必须介于 0 和 8 之间")
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