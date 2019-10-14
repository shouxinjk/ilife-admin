/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 分润规则Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class ProfitShareScheme extends DataEntity<ProfitShareScheme> {
	
	private static final long serialVersionUID = 1L;
	private String type;		// 分润类型，包括订单、意向、积分
	private String platform;		// 电商平台
	private String category;		// 商品类别
	private String status;		// 是否激活
	private String priority;		// 优先级
	
	public ProfitShareScheme() {
		super();
	}

	public ProfitShareScheme(String id){
		super(id);
	}

	@Length(min=1, max=20, message="分润类型，包括订单、意向、积分长度必须介于 1 和 20 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=50, message="电商平台长度必须介于 0 和 50 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=0, max=50, message="商品类别长度必须介于 0 和 50 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Length(min=1, max=1, message="是否激活长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=0, max=11, message="优先级长度必须介于 0 和 11 之间")
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
}