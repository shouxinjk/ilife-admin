/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 标签Entity
 * @author chenci
 * @version 2017-09-27
 */
public class Tags extends DataEntity<Tags> {
	
	private static final long serialVersionUID = 1L;
	private TagCategory tagCategory;
	private String name;		// 名称
	private String expression;		// 表达式
	private String type;		// 类型
	private String ruleOfJudgment;		// 判定规则
	private String tagKey;		//Key
	private Measure measure;//关联的属性
	
	public Tags() {
		super();
	}

	public Tags(String id){
		super(id);
	}

	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=500, message="表达式长度必须介于 0 和 500 之间")
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Length(min=0, max=10, message="类型长度必须介于 0 和 10 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TagCategory getTagCategory() {
		return tagCategory;
	}

	public void setTagCategory(TagCategory tagCategory) {
		this.tagCategory = tagCategory;
	}

	public String getRuleOfJudgment() {
		return ruleOfJudgment;
	}

	public void setRuleOfJudgment(String ruleOfJudgment) {
		this.ruleOfJudgment = ruleOfJudgment;
	}

	public String getTagKey() {
		return tagKey;
	}

	public void setTagKey(String tagKey) {
		this.tagKey = tagKey;
	}

	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}
}