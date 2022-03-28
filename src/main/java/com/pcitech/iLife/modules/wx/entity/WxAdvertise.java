/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 微信广告位管理Entity
 * @author ilife
 * @version 2022-03-28
 */
public class WxAdvertise extends DataEntity<WxAdvertise> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 广告位名称
	private String type;		// 广告类型
	private String timeSlot;		// 时间段名称
	private Date timeSlotFrom;		// 时间段开始时间
	private Date timeSlotTo;		// 时间段结束时间
	private Double price;		// 价格
	private String discount;		// 优惠策略
	private String status;		// 状态
	
	public WxAdvertise() {
		super();
	}

	public WxAdvertise(String id){
		super(id);
	}

	@Length(min=1, max=255, message="广告位名称长度必须介于 1 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=255, message="广告类型长度必须介于 1 和 255 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=1, max=512, message="时间段名称长度必须介于 1 和 512 之间")
	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="时间段开始时间不能为空")
	public Date getTimeSlotFrom() {
		return timeSlotFrom;
	}

	public void setTimeSlotFrom(Date timeSlotFrom) {
		this.timeSlotFrom = timeSlotFrom;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="时间段结束时间不能为空")
	public Date getTimeSlotTo() {
		return timeSlotTo;
	}

	public void setTimeSlotTo(Date timeSlotTo) {
		this.timeSlotTo = timeSlotTo;
	}
	
	@NotNull(message="价格不能为空")
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
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