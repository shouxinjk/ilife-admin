/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 贡献度Entity
 * @author ilife
 * @version 2023-01-18
 */
public class Credit extends DataEntity<Credit> {
	
	private static final long serialVersionUID = 1L;
	private String key;		// KEY
	private String type;		// 类型
	private String name;		// 名称
	private String description;		// 描述
	private String icon;		// 图标
	private Integer priority; //排序优先级
	
	public Credit() {
		super();
	}

	public Credit(String id){
		super(id);
	}

	@Length(min=0, max=50, message="KEY长度必须介于 0 和 50 之间")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	@Length(min=0, max=10, message="类型长度必须介于 0 和 10 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
}