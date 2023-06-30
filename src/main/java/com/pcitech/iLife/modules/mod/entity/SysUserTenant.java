/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 用户租户关联Entity
 * @author ilife
 * @version 2023-06-30
 */
public class SysUserTenant extends DataEntity<SysUserTenant> {
	
	private static final long serialVersionUID = 1L;
	private String userId;		// 用户id
	private String tenantId;		// 租户id
	private String status;		// 状态(1 正常 2 离职 3 待审核 4 审核未通过)
	private Date createTime;		// 创建日期
	private Date updateTime;		// 更新日期
	
	public SysUserTenant() {
		super();
	}

	public SysUserTenant(String id){
		super(id);
	}

	@Length(min=0, max=32, message="用户id长度必须介于 0 和 32 之间")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	@Length(min=0, max=1, message="状态(1 正常 2 离职 3 待审核 4 审核未通过)长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
	
}