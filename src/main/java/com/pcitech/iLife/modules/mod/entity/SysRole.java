/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 系统角色Entity
 * @author ilife
 * @version 2023-06-30
 */
public class SysRole extends DataEntity<SysRole> {
	
	private static final long serialVersionUID = 1L;
	private String roleName;		// 角色名称
	private String roleCode;		// 角色编码
	private String description;		// 描述
	private Date createTime;		// 创建时间
	private Date updateTime;		// 更新时间
	private String tenantId;		// 租户ID
	
	public SysRole() {
		super();
	}

	public SysRole(String id){
		super(id);
	}

	@Length(min=0, max=200, message="角色名称长度必须介于 0 和 200 之间")
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	@Length(min=1, max=100, message="角色编码长度必须介于 1 和 100 之间")
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	@Length(min=0, max=255, message="描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
}