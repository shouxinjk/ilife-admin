/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 电商平台属性映射Entity
 * @author ilife
 * @version 2022-01-07
 */
public class PlatformProperty extends DataEntity<PlatformProperty> {
	
	private static final long serialVersionUID = 1L;
	private PlatformCategory platformCategory;		// 原始类目
	private String cname;		// 原始类目名称
	private String name;		// 名称
	private String platform;		// 所属平台
	private ItemCategory category;		// 标准类目ID
	private Measure measure;		// 标准属性ID
	
	public PlatformProperty() {
		super();
	}

	public PlatformProperty(String id){
		super(id);
	}
	
	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	@Length(min=1, max=100, message="所属平台长度必须介于 1 和 100 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	public PlatformCategory getPlatformCategory() {
		return platformCategory;
	}

	public void setPlatformCategory(PlatformCategory platformCategory) {
		this.platformCategory = platformCategory;
	}
	
}