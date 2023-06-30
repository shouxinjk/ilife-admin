/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 租户部门Entity
 * @author ilife
 * @version 2023-06-30
 */
public class SysDepart extends DataEntity<SysDepart> {
	
	private static final long serialVersionUID = 1L;
	private SysDepart parent;		// 父机构ID
	private String departName;		// 机构/部门名称
	private String departNameEn;		// 英文名
	private String departNameAbbr;		// 缩写
	private String departOrder;		// 排序
	private String description;		// 描述
	private String orgCategory;		// 机构类别 1公司，2组织机构，2岗位
	private String orgType;		// 机构类型 1一级部门 2子部门
	private String orgCode;		// 机构编码
	private String mobile;		// 手机号
	private String fax;		// 传真
	private String address;		// 地址
	private String memo;		// 备注
	private String status;		// 状态（1启用，0不启用）
	private String qywxIdentifier;		// 对接企业微信的ID
	private Date createTime;		// 创建日期
	private Date updateTime;		// 更新日期
	private String tenantId;		// 租户ID
	private String izLeaf;		// 是否有叶子节点: 1是0否
	
	public SysDepart() {
		super();
	}

	public SysDepart(String id){
		super(id);
	}

	@JsonBackReference
	public SysDepart getParent() {
		return parent;
	}

	public void setParent(SysDepart parent) {
		this.parent = parent;
	}
	
	@Length(min=1, max=100, message="机构/部门名称长度必须介于 1 和 100 之间")
	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}
	
	@Length(min=0, max=500, message="英文名长度必须介于 0 和 500 之间")
	public String getDepartNameEn() {
		return departNameEn;
	}

	public void setDepartNameEn(String departNameEn) {
		this.departNameEn = departNameEn;
	}
	
	@Length(min=0, max=500, message="缩写长度必须介于 0 和 500 之间")
	public String getDepartNameAbbr() {
		return departNameAbbr;
	}

	public void setDepartNameAbbr(String departNameAbbr) {
		this.departNameAbbr = departNameAbbr;
	}
	
	public String getDepartOrder() {
		return departOrder;
	}

	public void setDepartOrder(String departOrder) {
		this.departOrder = departOrder;
	}
	
	@Length(min=0, max=500, message="描述长度必须介于 0 和 500 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=1, max=10, message="机构类别 1公司，2组织机构，2岗位长度必须介于 1 和 10 之间")
	public String getOrgCategory() {
		return orgCategory;
	}

	public void setOrgCategory(String orgCategory) {
		this.orgCategory = orgCategory;
	}
	
	@Length(min=0, max=10, message="机构类型 1一级部门 2子部门长度必须介于 0 和 10 之间")
	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	
	@Length(min=1, max=64, message="机构编码长度必须介于 1 和 64 之间")
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	
	@Length(min=0, max=32, message="手机号长度必须介于 0 和 32 之间")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Length(min=0, max=32, message="传真长度必须介于 0 和 32 之间")
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}
	
	@Length(min=0, max=100, message="地址长度必须介于 0 和 100 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Length(min=0, max=500, message="备注长度必须介于 0 和 500 之间")
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	@Length(min=0, max=1, message="状态（1启用，0不启用）长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=0, max=100, message="对接企业微信的ID长度必须介于 0 和 100 之间")
	public String getQywxIdentifier() {
		return qywxIdentifier;
	}

	public void setQywxIdentifier(String qywxIdentifier) {
		this.qywxIdentifier = qywxIdentifier;
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
	
	@Length(min=0, max=1, message="是否有叶子节点: 1是0否长度必须介于 0 和 1 之间")
	public String getIzLeaf() {
		return izLeaf;
	}

	public void setIzLeaf(String izLeaf) {
		this.izLeaf = izLeaf;
	}
	
}