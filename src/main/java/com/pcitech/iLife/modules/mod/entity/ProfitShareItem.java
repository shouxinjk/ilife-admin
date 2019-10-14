/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 分润规则明细Entity
 * @author qchzhu
 * @version 2019-10-14
 */
public class ProfitShareItem extends DataEntity<ProfitShareItem> {
	
	private static final long serialVersionUID = 1L;
	private String schemeId;		// 所属分润规则
	private String beneficiary;		// 受益方
	private String beneficiaryType;		// 受益方类型：个人、整体
	private String share;		// 所占份额
	
	public ProfitShareItem() {
		super();
	}

	public ProfitShareItem(String id){
		super(id);
	}

	@Length(min=1, max=20, message="所属分润规则长度必须介于 1 和 20 之间")
	public String getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(String schemeId) {
		this.schemeId = schemeId;
	}
	
	@Length(min=0, max=50, message="受益方长度必须介于 0 和 50 之间")
	public String getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}
	
	@Length(min=0, max=50, message="受益方类型：个人、整体长度必须介于 0 和 50 之间")
	public String getBeneficiaryType() {
		return beneficiaryType;
	}

	public void setBeneficiaryType(String beneficiaryType) {
		this.beneficiaryType = beneficiaryType;
	}
	
	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}
	
}