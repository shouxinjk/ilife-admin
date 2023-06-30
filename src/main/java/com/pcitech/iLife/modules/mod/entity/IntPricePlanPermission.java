/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 订阅计划授权Entity
 * @author ilife
 * @version 2023-06-30
 */
public class IntPricePlanPermission extends DataEntity<IntPricePlanPermission> {
	
	private static final long serialVersionUID = 1L;
	private Date createTime;		// 创建日期
	private Date updateTime;		// 更新日期
	private String sysOrgCode;		// 所属部门
	private String pricePlanId;		// 订阅计划
	private String roleId;		// 授权模板
	private String salePackageId;		// 产品套餐
	
	public IntPricePlanPermission() {
		super();
	}

	public IntPricePlanPermission(String id){
		super(id);
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
	
	@Length(min=0, max=64, message="所属部门长度必须介于 0 和 64 之间")
	public String getSysOrgCode() {
		return sysOrgCode;
	}

	public void setSysOrgCode(String sysOrgCode) {
		this.sysOrgCode = sysOrgCode;
	}
	
	@Length(min=0, max=32, message="订阅计划长度必须介于 0 和 32 之间")
	public String getPricePlanId() {
		return pricePlanId;
	}

	public void setPricePlanId(String pricePlanId) {
		this.pricePlanId = pricePlanId;
	}
	
	@Length(min=0, max=32, message="授权模板长度必须介于 0 和 32 之间")
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	@Length(min=0, max=32, message="产品套餐长度必须介于 0 和 32 之间")
	public String getSalePackageId() {
		return salePackageId;
	}

	public void setSalePackageId(String salePackageId) {
		this.salePackageId = salePackageId;
	}
	
}