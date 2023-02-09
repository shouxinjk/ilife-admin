/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;

/**
 * 个性化定制小类Entity
 * @author chenci
 * @version 2022-10-31
 */
public class ProposalSubtype extends DataEntity<ProposalSubtype> {
	
	private static final long serialVersionUID = 1L;
	private ProposalScheme scheme;		// 主题
	private String name;		// 名称
	private ItemCategory category;		// 商品类目
	private String description;		// 描述
	private String logo;		// LOGO
	
	public ProposalSubtype() {
		super();
	}

	public ProposalSubtype(String id){
		super(id);
	}

	public ProposalScheme getScheme() {
		return scheme;
	}

	public void setScheme(ProposalScheme scheme) {
		this.scheme = scheme;
	}
	
	@Length(min=1, max=50, message="名称长度必须介于 1 和 50 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	@Length(min=0, max=512, message="描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=1024, message="LOGO长度必须介于 0 和 1024 之间")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
}