/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.pcitech.iLife.modules.sys.entity.User;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * SaaS用户Entity
 * @author ilife
 * @version 2023-06-30
 */
public class SysUser extends DataEntity<SysUser> {
	
	private static final long serialVersionUID = 1L;
	private String username;		// 登录账号
	private String realname;		// 真实姓名
	private String password;		// 密码
	private String salt;		// md5密码盐
	private String avatar;		// 头像
	private Date birthday;		// 生日
	private String sex;		// 性别(0-默认未知,1-男,2-女)
	private String email;		// 电子邮件
	private String phone;		// 电话
	private String orgCode;		// 登录会话的机构编码
	private String status;		// 性别(1-正常,2-冻结)
	private String thirdId;		// 第三方登录的唯一标识
	private String thirdType;		// 第三方类型
	private String activitiSync;		// 同步工作流引擎(1-同步,0-不同步)
	private String workNo;		// 工号，唯一键
	private String post;		// 职务，关联职务表
	private String telephone;		// 座机号
	private Date createTime;		// 创建时间
	private Date updateTime;		// 更新时间
	private User user;		// 身份（1普通成员 2上级）
	private String departIds;		// 负责部门
	private String clientId;		// 设备ID
	private Integer loginTenantId;		// 上次登录选择租户ID
	private String bpmStatus;		// 流程入职离职状态
	
	public SysUser() {
		super();
	}

	public SysUser(String id){
		super(id);
	}

	@Length(min=0, max=100, message="登录账号长度必须介于 0 和 100 之间")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	@Length(min=0, max=100, message="真实姓名长度必须介于 0 和 100 之间")
	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}
	
	@Length(min=0, max=255, message="密码长度必须介于 0 和 255 之间")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Length(min=0, max=45, message="md5密码盐长度必须介于 0 和 45 之间")
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	@Length(min=0, max=255, message="头像长度必须介于 0 和 255 之间")
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	@Length(min=0, max=1, message="性别(0-默认未知,1-男,2-女)长度必须介于 0 和 1 之间")
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	@Length(min=0, max=45, message="电子邮件长度必须介于 0 和 45 之间")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Length(min=0, max=45, message="电话长度必须介于 0 和 45 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=64, message="登录会话的机构编码长度必须介于 0 和 64 之间")
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	
	@Length(min=0, max=1, message="性别(1-正常,2-冻结)长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=0, max=100, message="第三方登录的唯一标识长度必须介于 0 和 100 之间")
	public String getThirdId() {
		return thirdId;
	}

	public void setThirdId(String thirdId) {
		this.thirdId = thirdId;
	}
	
	@Length(min=0, max=100, message="第三方类型长度必须介于 0 和 100 之间")
	public String getThirdType() {
		return thirdType;
	}

	public void setThirdType(String thirdType) {
		this.thirdType = thirdType;
	}
	
	@Length(min=0, max=1, message="同步工作流引擎(1-同步,0-不同步)长度必须介于 0 和 1 之间")
	public String getActivitiSync() {
		return activitiSync;
	}

	public void setActivitiSync(String activitiSync) {
		this.activitiSync = activitiSync;
	}
	
	@Length(min=0, max=100, message="工号，唯一键长度必须介于 0 和 100 之间")
	public String getWorkNo() {
		return workNo;
	}

	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}
	
	@Length(min=0, max=100, message="职务，关联职务表长度必须介于 0 和 100 之间")
	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}
	
	@Length(min=0, max=45, message="座机号长度必须介于 0 和 45 之间")
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=0, max=1000, message="负责部门长度必须介于 0 和 1000 之间")
	public String getDepartIds() {
		return departIds;
	}

	public void setDepartIds(String departIds) {
		this.departIds = departIds;
	}
	
	@Length(min=0, max=64, message="设备ID长度必须介于 0 和 64 之间")
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public Integer getLoginTenantId() {
		return loginTenantId;
	}

	public void setLoginTenantId(Integer loginTenantId) {
		this.loginTenantId = loginTenantId;
	}
	
	@Length(min=0, max=2, message="流程入职离职状态长度必须介于 0 和 2 之间")
	public String getBpmStatus() {
		return bpmStatus;
	}

	public void setBpmStatus(String bpmStatus) {
		this.bpmStatus = bpmStatus;
	}
	
}