/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 动态表单Entity
 * @author chenci
 * @version 2022-10-29
 */
public class JsonForm extends DataEntity<JsonForm> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 名称
	private String description;		// 描述
	private String json;		// 表单JSON
	
	public JsonForm() {
		super();
	}

	public JsonForm(String id){
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
	
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
}