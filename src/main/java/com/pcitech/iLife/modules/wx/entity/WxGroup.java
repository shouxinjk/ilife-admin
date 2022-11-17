/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.Persona;

/**
 * 微信群Entity
 * @author ilife
 * @version 2022-06-07
 */
public class WxGroup extends DataEntity<WxGroup> {
	
	private static final long serialVersionUID = 1L;
	private Broker broker;		// 所属达人
	private Persona persona;		// 对应分群
	private String schedule;		// 推送规则CRON
	private String name;		// 微信群名称
	private String type;		// 微信群类型：合作、自建、非合作
	private String owner;		// 群主
	private String gid;		// 微信群ID
	private Integer members;		// 成员人数
	private String token;		// 激活码
	private String status="active";		// 状态
	
	public WxGroup() {
		super();
	}

	public WxGroup(String id){
		super(id);
	}


	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Length(min=0, max=64, message="推送规则CRON长度必须介于 0 和 64 之间")
	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	@Length(min=0, max=512, message="微信群名称长度必须介于 0 和 512 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=0, max=512, message="群主长度必须介于 0 和 512 之间")
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@Length(min=0, max=512, message="微信群ID长度必须介于 0 和 512 之间")
	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}
	
	@NotNull(message="成员人数不能为空")
	public Integer getMembers() {
		return members;
	}

	public void setMembers(Integer members) {
		this.members = members;
	}
	
	@Length(min=0, max=50, message="激活码长度必须介于 0 和 50 之间")
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	@Length(min=0, max=20, message="状态长度必须介于 0 和 20 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}