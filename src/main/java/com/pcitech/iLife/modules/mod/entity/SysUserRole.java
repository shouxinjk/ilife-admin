/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 用户角色关联Entity
 * @author ilife
 * @version 2023-06-30
 */
public class SysUserRole extends DataEntity<SysUserRole> {
	
	private static final long serialVersionUID = 1L;
	private String userId;		// 用户id
	private String roleId;		// 角色id
	private String tenantId;		// 租户ID
	
	public SysUserRole() {
		super();
	}

	public SysUserRole(String id){
		super(id);
	}

	@Length(min=0, max=32, message="用户id长度必须介于 0 和 32 之间")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Length(min=0, max=32, message="角色id长度必须介于 0 和 32 之间")
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
}