/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.sys.entity.Office;
import com.pcitech.iLife.modules.sys.entity.User;

/**
 * 达人管理Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class Broker extends DataEntity<Broker> {
	
	private static final long serialVersionUID = 1L;
	private Office orgnization;	// 归属公司
	private User sysUser; //对应系统账户
	private Broker parent;		// 上级达人
	private String openid;		// openid
	private String nickname;		// 昵称：从微信获取
	private String avatarUrl;		//头像：从微信获取
	private String name;		// 真实姓名
	private String phone;		// 电话号码
	private String email;		// 邮件
	private int hierarchy;		// 层级
	private String securityNo;		// 身份证号码
	private String level;		// 等级
	private String alipayAccount;		// 支付宝账号
	private String alipayAccountName;		// 支付宝账户名
	private String status;		// 账户状态
	private String upgrade;		// 升级状态
	private String qrcodeUrl;		// 二维码地址
	private int points=20;		// 虚拟豆数量:默认设为20
	private int coins=0;//默认金币为0
	private String token;//机器人口令
	
	private String wechatId; //微信ID
	private String companyName; //公司全称
	private String companyBank; //公司开户行
	private String companyAccount; //公司账户
	private String companyContact; //公司联系人
	private String companyTelephone;//公司联系人电话
	
	private String accountType = "person"; //结算账户类型
	
	private List<CategoryBroker> badges; //徽章列表：包含已经获得授权的徽章：仅用于读取
	
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
	public User getSysUser() {
		return sysUser;
	}

	public void setSysUser(User sysUser) {
		this.sysUser = sysUser;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
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
	
	public int getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(int hierarchy) {
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

	public String getQrcodeUrl() {
		return qrcodeUrl;
	}

	public void setQrcodeUrl(String qrcodeUrl) {
		this.qrcodeUrl = qrcodeUrl;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getWechatId() {
		return wechatId;
	}

	public void setWechatId(String wechatId) {
		this.wechatId = wechatId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyBank() {
		return companyBank;
	}

	public void setCompanyBank(String companyBank) {
		this.companyBank = companyBank;
	}

	public String getCompanyAccount() {
		return companyAccount;
	}

	public void setCompanyAccount(String companyAccount) {
		this.companyAccount = companyAccount;
	}

	public String getCompanyContact() {
		return companyContact;
	}

	public void setCompanyContact(String companyContact) {
		this.companyContact = companyContact;
	}

	public String getCompanyTelephone() {
		return companyTelephone;
	}

	public void setCompanyTelephone(String companyTelephone) {
		this.companyTelephone = companyTelephone;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Office getOrgnization() {
		return orgnization;
	}

	public void setOrgnization(Office orgnization) {
		this.orgnization = orgnization;
	}

	public List<CategoryBroker> getBadges() {
		return badges;
	}

	public void setBadges(List<CategoryBroker> badges) {
		this.badges = badges;
	}
	
}