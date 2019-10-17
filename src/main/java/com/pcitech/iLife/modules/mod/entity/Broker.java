/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 达人管理Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class Broker extends DataEntity<Broker> {
	
	private static final long serialVersionUID = 1L;
	private Broker parent;		// 上级达人
	private String openid;		// openid
	private String name;		// 真实姓名
	private String phone;		// 电话号码
	private String email;		// 邮件
	private String hierarchy;		// 层级
	private String securityNo;		// 身份证号码
	private String level;		// 等级
	private String alipayAccount;		// 支付宝账号
	private String alipayAccountName;		// 支付宝账户名
	private String status;		// 账户状态
	private String upgrade;		// 升级状态
	
	public Broker() {
		super();
	}

	public Broker(String id){
		super(id);
	}

	@JsonBackReference
	public Broker getParent() {
		return parent;
	}

	public void setParent(Broker parent) {
		this.parent = parent;
	}
	
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	@Length(min=0, max=20, message="真实姓名长度必须介于 0 和 20 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=20, message="电话号码长度必须介于 0 和 20 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=50, message="邮件长度必须介于 0 和 50 之间")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Length(min=0, max=11, message="层级长度必须介于 0 和 11 之间")
	public String getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	@Length(min=0, max=50, message="身份证号码长度必须介于 0 和 50 之间")
	public String getSecurityNo() {
		return securityNo;
	}

	public void setSecurityNo(String securityNo) {
		this.securityNo = securityNo;
	}
	
	@Length(min=0, max=50, message="等级长度必须介于 0 和 50 之间")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	@Length(min=0, max=50, message="支付宝账号长度必须介于 0 和 50 之间")
	public String getAlipayAccount() {
		return alipayAccount;
	}

	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}
	
	@Length(min=0, max=50, message="支付宝账户名长度必须介于 0 和 50 之间")
	public String getAlipayAccountName() {
		return alipayAccountName;
	}

	public void setAlipayAccountName(String alipayAccountName) {
		this.alipayAccountName = alipayAccountName;
	}
	
	@Length(min=0, max=20, message="账户状态长度必须介于 0 和 20 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=0, max=20, message="升级状态长度必须介于 0 和 20 之间")
	public String getUpgrade() {
		return upgrade;
	}

	public void setUpgrade(String upgrade) {
		this.upgrade = upgrade;
	}
	
}