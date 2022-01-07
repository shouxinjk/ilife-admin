/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.TreeEntity;

/**
 * 电商平台类目映射Entity
 * @author ilife
 * @version 2022-01-07
 */
public class PlatformCategory extends TreeEntity<PlatformCategory> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 名称
	private String platform;		// 所属平台
	private PlatformCategory parent;		// 原始父ID
	private String parentIds;		// 所有父级编号，预留
	private ItemCategory category;		// 标准类目ID
	
	public PlatformCategory() {
		super();
	}

	public PlatformCategory(String id){
		super(id);
	}

	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=100, message="所属平台长度必须介于 1 和 100 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@JsonBackReference
	@NotNull(message="原始父ID不能为空")
	public PlatformCategory getParent() {
		return parent;
	}

	public void setParent(PlatformCategory parent) {
		this.parent = parent;
	}
	
	@Length(min=1, max=2000, message="所有父级编号，预留长度必须介于 1 和 2000 之间")
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	
	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}
}