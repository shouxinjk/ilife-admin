/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 链接模板Entity
 * @author qchzhu
 * @version 2021-09-01
 */
public class LinkTemplate extends DataEntity<LinkTemplate> {
	
	private static final long serialVersionUID = 1L;
	private String platform;		// 第三方平台
	private String type;		// 链接类型，如普通链接、导购链接等
	private String name;		// 名称
	private String condition;		// 判定规则
	private String expression;		// 处理规则
	private String priority="10";		// 备用。优先级
	private String description;		// 描述
	
	public LinkTemplate() {
		super();
	}

	public LinkTemplate(String id){
		super(id);
	}

	@Length(min=1, max=64, message="第三方平台长度必须介于 1 和 64 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=1, max=64, message="链接类型，如普通链接、导购链接等长度必须介于 1 和 64 之间")
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
	
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Length(min=0, max=8, message="备用。优先级长度必须介于 0 和 8 之间")
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