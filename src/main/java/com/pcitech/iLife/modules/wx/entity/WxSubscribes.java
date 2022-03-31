/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 关注记录Entity
 * @author ilife
 * @version 2022-03-31
 */
public class WxSubscribes extends DataEntity<WxSubscribes> {
	
	private static final long serialVersionUID = 1L;
	private WxAccount account;		// 公众号
	private Broker broker;		// 关注者broker
	private String openid;		// 关注者openid
	
	public WxSubscribes() {
		super();
	}

	public WxSubscribes(String id){
		super(id);
	}

	
	public WxAccount getAccount() {
		return account;
	}

	public void setAccount(WxAccount account) {
		this.account = account;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	@Length(min=1, max=128, message="关注者openid长度必须介于 1 和 128 之间")
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
}