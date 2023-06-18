/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 订阅套餐明细Entity
 * @author ilife
 * @version 2023-06-18
 */
public class IntPackagePricePlan extends DataEntity<IntPackagePricePlan> {
	
	private static final long serialVersionUID = 1L;
	private Date createTime;		// 创建日期
	private Date updateTime;		// 更新日期
	private String sysOrgCode;		// 所属部门
	private StoSalePackage salePackage;		// 产品套餐
	private StoSoftware software;		// 订阅产品
	private StoPricePlan pricePlan;		// 订阅计划
	
	public IntPackagePricePlan() {
		super();
	}

	public IntPackagePricePlan(String id){
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
	
	public StoSalePackage getSalePackage() {
		return salePackage;
	}

	public void setSalePackage(StoSalePackage salePackage) {
		this.salePackage = salePackage;
	}
	
	public StoSoftware getSoftware() {
		return software;
	}

	public void setSoftware(StoSoftware software) {
		this.software = software;
	}
	
	public StoPricePlan getPricePlan() {
		return pricePlan;
	}

	public void setPricePlan(StoPricePlan pricePlan) {
		this.pricePlan = pricePlan;
	}
	
}