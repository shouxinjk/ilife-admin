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
public class UserTag extends DataEntity<UserTag> {
	
	private static final long serialVersionUID = 1L;
	private UserTagCategory userTagCategory;
	private String name;		// 名称
	private String expression;		// 表达式
	private String type;		// 类型
	private String ruleOfJudgment;		// 判定规则
	private String tagKey;		//Key
	private int sort=100;//排序
	private UserMeasure userMeasure;//关联的属性
	
	public UserTag() {
		super();
	}

	public UserTag(String id){
		super(id);
	}

	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public UserTagCategory getUserTagCategory() {
		return userTagCategory;
	}

	public void setUserTagCategory(UserTagCategory userTagCategory) {
		this.userTagCategory = userTagCategory;
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

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public UserMeasure getUserMeasure() {
		return userMeasure;
	}

	public void setUserMeasure(UserMeasure userMeasure) {
		this.userMeasure = userMeasure;
	}
}