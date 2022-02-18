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
	private String code;		// code
	private String exprPerson;		// 用户更新Expr
	private String exprItem;		// 商品更新Expr
	private String description;		// 说明
	private String exprUserNeed;		// 用户需要更新Expr
	private String exprItemNeed;		// 商品需要更新Expr
	private String status;		// 启用禁用
	private String type;		// 类型：具体操作枚举：click、view、buy、label等。来源于behavior_type
	private String category;		// 分类：行为对象类型：item、tag、channel等。来源于behavior_category
	
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
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getExprPerson() {
		return exprPerson;
	}

	public void setExprPerson(String exprPerson) {
		this.exprPerson = exprPerson;
	}
	
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
	

	
	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getExprUserNeed() {
		return exprUserNeed;
	}

	public void setExprUserNeed(String exprUserNeed) {
		this.exprUserNeed = exprUserNeed;
	}

	public String getExprItemNeed() {
		return exprItemNeed;
	}

	public void setExprItemNeed(String exprItemNeed) {
		this.exprItemNeed = exprItemNeed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}