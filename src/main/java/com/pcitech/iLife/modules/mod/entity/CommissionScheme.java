/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 佣金规则设置Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class CommissionScheme extends DataEntity<CommissionScheme> {
	
	private static final long serialVersionUID = 1L;
	private String platform;		// 电商平台
	private String category;		// 商品类别
	private String type;		// 佣金类型：固定金额或百分比
	private double amount;		// 佣金额
	private Date effectiveFrom;		// 生效时间
	private String status;		// 状态
	
	public CommissionScheme() {
		super();
	}

	public CommissionScheme(String id){
		super(id);
	}

	@Length(min=1, max=50, message="电商平台长度必须介于 1 和 50 之间")
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
	
	@Length(min=0, max=10, message="佣金类型：固定金额或百分比长度必须介于 0 和 10 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
	
	@Length(min=1, max=1, message="状态长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}