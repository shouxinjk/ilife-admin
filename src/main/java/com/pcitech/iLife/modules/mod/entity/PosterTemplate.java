/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 海报模板管理Entity
 * @author ilife
 * @version 2021-12-28
 */
public class PosterTemplate extends DataEntity<PosterTemplate> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 名称
	private String logo;		// 缩略图
	private String type;		// 类型：item单品，board列表
	private String description;		// 描述
	private ItemCategory category;		// 适用类目，仅对单品有效
	private String code;		// 海报编号。对应设计器编码
	private String status;		// 状态：active启用，inactive禁用
	private String condition;		// 适用条件
	private String options;		// 参数配置json
	private Integer priority;		// 优先级
	
	public PosterTemplate() {
		super();
	}

	public PosterTemplate(String id){
		super(id);
	}

	@Length(min=1, max=255, message="名称长度必须介于 1 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=512, message="缩略图长度必须介于 1 和 512 之间")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	@Length(min=0, max=20, message="类型：item单品，board列表长度必须介于 0 和 20 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	@Length(min=0, max=512, message="海报编号。对应设计器编码长度必须介于 0 和 512 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Length(min=0, max=20, message="状态：active启用，inactive禁用长度必须介于 0 和 20 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}
	
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
}