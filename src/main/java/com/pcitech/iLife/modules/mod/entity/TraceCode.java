/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 推广位Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class TraceCode extends DataEntity<TraceCode> {
	
	private static final long serialVersionUID = 1L;
	private Broker broker;		// 所属达人
	private String platform;		// 电商平台
	private String code;		// 跟踪码
	
	public TraceCode() {
		super();
	}

	public TraceCode(String id){
		super(id);
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
	@Length(min=0, max=50, message="电商平台长度必须介于 0 和 50 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=0, max=50, message="跟踪码长度必须介于 0 和 50 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}