/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 微信群任务Entity
 * @author ilife
 * @version 2022-06-11
 */
public class WxGroupTask extends DataEntity<WxGroupTask> {
	
	private static final long serialVersionUID = 1L;
	private Broker broker;		// 所属达人
	private WxGroup wxgroup;		// 所属微信群
	private String name;		// 任务名称
	private String type;		// 任务类型
	private String cron;		// 任务CRON
	private String tags;		// 任务关键字
	private String status;		// 任务状态
	
	public WxGroupTask() {
		super();
	}

	public WxGroupTask(String id){
		super(id);
	}


	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public WxGroup getWxgroup() {
		return wxgroup;
	}

	public void setWxgroup(WxGroup wxgroup) {
		this.wxgroup = wxgroup;
	}

	@Length(min=0, max=100, message="任务名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=100, message="任务类型长度必须介于 0 和 100 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=64, message="任务CRON长度必须介于 0 和 64 之间")
	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
	
	@Length(min=0, max=512, message="任务关键字长度必须介于 0 和 512 之间")
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	@Length(min=0, max=50, message="任务状态长度必须介于 0 和 50 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}