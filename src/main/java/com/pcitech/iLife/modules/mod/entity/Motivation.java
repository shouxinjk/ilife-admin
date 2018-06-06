/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 内部动机Entity
 * @author chenci
 * @version 2017-09-15
 */
public class Motivation extends DataEntity<Motivation> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// name
	private MotivationCategory motivationCategory;		// category_id
	private Phase phase;		// phase_id
	private Double percentage;		// percentage
	private String exprTrigger;		// expr_trigger
	private String exprDuration;		// expr_duration
	private String exprItem;		// expr_item
	private String expression;		// expression
	
	public Motivation() {
		super();
	}

	public Motivation(String id){
		super(id);
	}

	@Length(min=0, max=100, message="name长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public MotivationCategory getMotivationCategory() {
		return motivationCategory;
	}

	public void setMotivationCategory(MotivationCategory motivationCategory) {
		this.motivationCategory = motivationCategory;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
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
	
	@Length(min=0, max=500, message="expr_item长度必须介于 0 和 500 之间")
	public String getExprItem() {
		return exprItem;
	}

	public void setExprItem(String exprItem) {
		this.exprItem = exprItem;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
}