/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.dict.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 电商平台字典管理Entity
 * @author ilife
 * @version 2021-11-26
 */
public class DictPlatform extends DataEntity<DictPlatform> {
	
	private static final long serialVersionUID = 1L;
	private String category;		// 类别
	private String label;		// 字典值
	private String score;		// 字典分
	private String logo;		// Logo URL
	private String description;		// 描述
	
	public DictPlatform() {
		super();
	}

	public DictPlatform(String id){
		super(id);
	}

	@Length(min=0, max=32, message="类别长度必须介于 0 和 32 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Length(min=1, max=50, message="字典值长度必须介于 1 和 50 之间")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
	
	@Length(min=0, max=255, message="Logo URL长度必须介于 0 和 255 之间")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	@Length(min=0, max=100, message="描述长度必须介于 0 和 100 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}