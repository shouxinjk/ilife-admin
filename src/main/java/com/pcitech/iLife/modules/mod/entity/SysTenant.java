/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 系统租户Entity
 * @author ilife
 * @version 2023-06-30
 */
public class SysTenant extends DataEntity<SysTenant> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 租户名称
	private Date createTime;		// 创建时间
	private Date beginDate;		// 开始时间
	private Date endDate;		// 结束时间
	private String status;		// 状态 1正常 0冻结
	private String trade;		// 所属行业
	private String companySize;		// 公司规模
	private String companyAddress;		// 公司地址
	private String companyLogo;		// 公司logo
	private String houseNumber;		// 门牌号
	private String workPlace;		// 工作地点
	private String secondaryDomain;		// 二级域名
	private String loginBkgdImg;		// 登录背景图片
	private String position;		// 职级
	private String department;		// 部门
	private Date updateTime;		// 更新时间
	private Integer applyStatus;		// 允许申请管理员 1允许 0不允许
	
	public SysTenant() {
		super();
	}

	public SysTenant(String id){
		super(id);
	}

	@Length(min=0, max=100, message="租户名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=0, max=10, message="所属行业长度必须介于 0 和 10 之间")
	public String getTrade() {
		return trade;
	}

	public void setTrade(String trade) {
		this.trade = trade;
	}
	
	@Length(min=0, max=10, message="公司规模长度必须介于 0 和 10 之间")
	public String getCompanySize() {
		return companySize;
	}

	public void setCompanySize(String companySize) {
		this.companySize = companySize;
	}
	
	@Length(min=0, max=100, message="公司地址长度必须介于 0 和 100 之间")
	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}
	
	@Length(min=0, max=200, message="公司logo长度必须介于 0 和 200 之间")
	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}
	
	@Length(min=0, max=10, message="门牌号长度必须介于 0 和 10 之间")
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	
	@Length(min=0, max=100, message="工作地点长度必须介于 0 和 100 之间")
	public String getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}
	
	@Length(min=0, max=50, message="二级域名长度必须介于 0 和 50 之间")
	public String getSecondaryDomain() {
		return secondaryDomain;
	}

	public void setSecondaryDomain(String secondaryDomain) {
		this.secondaryDomain = secondaryDomain;
	}
	
	@Length(min=0, max=200, message="登录背景图片长度必须介于 0 和 200 之间")
	public String getLoginBkgdImg() {
		return loginBkgdImg;
	}

	public void setLoginBkgdImg(String loginBkgdImg) {
		this.loginBkgdImg = loginBkgdImg;
	}
	
	@Length(min=0, max=10, message="职级长度必须介于 0 和 10 之间")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
	@Length(min=0, max=10, message="部门长度必须介于 0 和 10 之间")
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public Integer getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(Integer applyStatus) {
		this.applyStatus = applyStatus;
	}
	
}