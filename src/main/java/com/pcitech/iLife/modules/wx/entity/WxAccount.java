/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 微信公众号管理Entity
 * @author ilife
 * @version 2022-03-28
 */
public class WxAccount extends DataEntity<WxAccount> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 公众号名称
	private String originalId;		// 公众号原始ID
	private String description;		// 公众号描述
	private String qrcodeImg;		// 二维码图片
	private String status="active";		// 状态
	private Broker broker;		// 达人ID
	
	public WxAccount() {
		super();
	}

	public WxAccount(String id){
		super(id);
	}

	@Length(min=1, max=255, message="公众号名称长度必须介于 1 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=255, message="公众号原始ID长度必须介于 1 和 255 之间")
	public String getOriginalId() {
		return originalId;
	}

	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}
	
	@Length(min=1, max=512, message="公众号描述长度必须介于 1 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=1, max=512, message="二维码图片长度必须介于 1 和 512 之间")
	public String getQrcodeImg() {
		return qrcodeImg;
	}

	public void setQrcodeImg(String qrcodeImg) {
		this.qrcodeImg = qrcodeImg;
	}
	
	@Length(min=0, max=20, message="状态长度必须介于 0 和 20 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
}