/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 频道管理Entity
 * @author ilife
 * @version 2022-02-17
 */
public class Channel extends DataEntity<Channel> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 关联的频道
	private String code;		// 识别CODE
	private String description;		// 概要描述
	private String tagging;		// 关键字列表
	private Integer sort;		// sort
	
	public Channel() {
		super();
	}

	public Channel(String id){
		super(id);
	}

	@Length(min=1, max=50, message="关联的频道长度必须介于 1 和 50 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=50, message="识别CODE长度必须介于 1 和 50 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Length(min=0, max=255, message="概要描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=1024, message="关键字列表长度必须介于 0 和 1024 之间")
	public String getTagging() {
		return tagging;
	}

	public void setTagging(String tagging) {
		this.tagging = tagging;
	}
	
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
}