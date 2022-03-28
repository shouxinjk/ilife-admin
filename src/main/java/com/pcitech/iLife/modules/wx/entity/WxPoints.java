/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 微信虚拟豆管理Entity
 * @author ilife
 * @version 2022-03-28
 */
public class WxPoints extends DataEntity<WxPoints> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 产品名称
	private Integer points;		// 虚拟豆数量
	private String price;		// 价格
	private String discount;		// 优惠策略
	private String status;		// 状态
	
	public WxPoints() {
		super();
	}

	public WxPoints(String id){
		super(id);
	}

	@Length(min=1, max=255, message="产品名称长度必须介于 1 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@NotNull(message="虚拟豆数量不能为空")
	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}
	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}
	
	@Length(min=0, max=20, message="状态长度必须介于 0 和 20 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}