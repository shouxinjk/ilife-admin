/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 微信机器人Entity
 * @author ilife
 * @version 2022-06-07
 */
public class WxBot extends DataEntity<WxBot> {
	
	private static final long serialVersionUID = 1L;
	private Broker broker;		// 所属达人
	private String name;		// 机器人名称
	private String type;		// 协议类型
	private String token;		// 协议token
	private String wechatyId;		// wechaty id
	private String qrcodeUrl;		// 二维码URL
	private String status;		// 状态
	private Date heartBeat;		// 心跳时间
	private String errMsg;		// 错误信息
	private Date effectFrom;		// 生效时间
	private Date expireOn;		// 截止时间
	
	public WxBot() {
		super();
	}

	public WxBot(String id){
		super(id);
	}
	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	@Length(min=0, max=255, message="机器人名称长度必须介于 0 和 255 之间")
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Length(min=0, max=512, message="wechaty id长度必须介于 0 和 512 之间")
	public String getWechatyId() {
		return wechatyId;
	}

	public void setWechatyId(String wechatyId) {
		this.wechatyId = wechatyId;
	}
	
	@Length(min=0, max=512, message="二维码URL长度必须介于 0 和 512 之间")
	public String getQrcodeUrl() {
		return qrcodeUrl;
	}

	public void setQrcodeUrl(String qrcodeUrl) {
		this.qrcodeUrl = qrcodeUrl;
	}
	
	@Length(min=0, max=20, message="状态长度必须介于 0 和 20 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getHeartBeat() {
		return heartBeat;
	}

	public void setHeartBeat(Date heartBeat) {
		this.heartBeat = heartBeat;
	}
	
	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEffectFrom() {
		return effectFrom;
	}

	public void setEffectFrom(Date effectFrom) {
		this.effectFrom = effectFrom;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getExpireOn() {
		return expireOn;
	}

	public void setExpireOn(Date expireOn) {
		this.expireOn = expireOn;
	}
	
}