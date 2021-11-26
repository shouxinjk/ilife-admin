/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.dict.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 材质字典管理Entity
 * @author ilife
 * @version 2021-11-26
 */
public class DictMaterial extends DataEntity<DictMaterial> {
	
	private static final long serialVersionUID = 1L;
	private String category;		// 所属类目
	private String label;		// 字典值
	private String score;		// 字典分
	private String description;		// 补充描述
	
	public DictMaterial() {
		super();
	}

	public DictMaterial(String id){
		super(id);
	}

	@Length(min=0, max=32, message="所属类目长度必须介于 0 和 32 之间")
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
	
	@Length(min=0, max=200, message="补充描述长度必须介于 0 和 200 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}