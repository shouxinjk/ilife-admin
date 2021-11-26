/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.dict.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;

/**
 * 品牌字典管理Entity
 * @author ilife
 * @version 2021-11-26
 */
public class DictBrand extends DataEntity<DictBrand> {
	
	private static final long serialVersionUID = 1L;
	private String logo;		// Logo URL
	private String country;		// 国家
	private String description;		// 等级描述
	private ItemCategory category;		// 所属类目
	private String label;		// 字典值，即品牌名称
	private Double score;		// 字典分
	
	public DictBrand() {
		super();
	}

	public DictBrand(String id){
		super(id);
	}

	@Length(min=0, max=255, message="Logo URL长度必须介于 0 和 255 之间")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	@Length(min=0, max=50, message="国家长度必须介于 0 和 50 之间")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@Length(min=0, max=100, message="等级描述长度必须介于 0 和 100 之间")
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
	
	@Length(min=1, max=50, message="字典值，即品牌名称长度必须介于 1 和 50 之间")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
	
}