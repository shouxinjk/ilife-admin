/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 用户行为Entity
 * @author chenci
 * @version 2017-09-28
 */
public class Behavior extends DataEntity<Behavior> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 显示名称
	private String exprPerson;		// 用户更新Expr
	private String exprItem;		// 商品更新Expr
	private String description;		// 说明
	private String exprCredit;		// 用户可信度Expr
	private String type;		// 名称
	
	public Behavior() {
		super();
	}

	public Behavior(String id){
		super(id);
	}

	@Length(min=0, max=100, message="显示名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=500, message="用户更新Expr长度必须介于 0 和 500 之间")
	public String getExprPerson() {
		return exprPerson;
	}

	public void setExprPerson(String exprPerson) {
		this.exprPerson = exprPerson;
	}
	
	@Length(min=0, max=500, message="商品更新Expr长度必须介于 0 和 500 之间")
	public String getExprItem() {
		return exprItem;
	}

	public void setExprItem(String exprItem) {
		this.exprItem = exprItem;
	}
	
	@Length(min=0, max=1000, message="说明长度必须介于 0 和 1000 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=500, message="用户可信度Expr长度必须介于 0 和 500 之间")
	public String getExprCredit() {
		return exprCredit;
	}

	public void setExprCredit(String exprCredit) {
		this.exprCredit = exprCredit;
	}
	
	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}