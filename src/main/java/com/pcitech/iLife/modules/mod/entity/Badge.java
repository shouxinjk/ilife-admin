/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 勋章Entity
 * @author ilife
 * @version 2022-12-27
 */
public class Badge extends DataEntity<Badge> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 名称
	private String description;		// 描述
	private String icon;		// 图标
	private String key;		// KEY
	private String level;		// 等级
	
	public Badge() {
		super();
	}

	public Badge(String id){
		super(id);
	}

	@Length(min=1, max=50, message="名称长度必须介于 1 和 50 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=512, message="描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=1024, message="图标长度必须介于 0 和 1024 之间")
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Length(min=0, max=50, message="KEY长度必须介于 0 和 50 之间")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	@Length(min=0, max=4, message="等级长度必须介于 0 和 4 之间")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
}