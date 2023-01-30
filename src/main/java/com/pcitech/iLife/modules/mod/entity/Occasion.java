/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 外部诱因Entity
 * @author chenci
 * @version 2017-09-15
 */
public class Occasion extends DataEntity<Occasion> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// name
	private OccasionCategory occasionCategory;		// category_id
	private ItemCategory category;		// itemCategory
	private String exprUser;		// expr_user
	private String exprItem;		// expr_item
	private String exprTrigger;		// expr_trigger
	private String exprDuration;		// expr_duration
	private String triggerDirection; //内在/外在
	private String triggerType;//主动被动
	private String triggerActions;//事件列表JSON
	private String expression;		// expression
	
	public Occasion() {
		super();
	}

	public Occasion(String id){
		super(id);
	}

	@Length(min=0, max=100, message="name长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public OccasionCategory getOccasionCategory() {
		return occasionCategory;
	}

	public void setOccasionCategory(OccasionCategory occasionCategory) {
		this.occasionCategory = occasionCategory;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	@Length(min=0, max=500, message="expr_user长度必须介于 0 和 500 之间")
	public String getExprUser() {
		return exprUser;
	}

	public void setExprUser(String exprUser) {
		this.exprUser = exprUser;
	}
	
	@Length(min=0, max=500, message="expr_item长度必须介于 0 和 500 之间")
	public String getExprItem() {
		return exprItem;
	}

	public void setExprItem(String exprItem) {
		this.exprItem = exprItem;
	}
	
	@Length(min=0, max=500, message="expr_trigger长度必须介于 0 和 500 之间")
	public String getExprTrigger() {
		return exprTrigger;
	}

	public void setExprTrigger(String exprTrigger) {
		this.exprTrigger = exprTrigger;
	}
	
	@Length(min=0, max=100, message="expr_duration长度必须介于 0 和 100 之间")
	public String getExprDuration() {
		return exprDuration;
	}

	public void setExprDuration(String exprDuration) {
		this.exprDuration = exprDuration;
	}

	public String getTriggerDirection() {
		return triggerDirection;
	}

	public void setTriggerDirection(String triggerDirection) {
		this.triggerDirection = triggerDirection;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public String getTriggerActions() {
		return triggerActions;
	}

	public void setTriggerActions(String triggerActions) {
		this.triggerActions = triggerActions;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
}