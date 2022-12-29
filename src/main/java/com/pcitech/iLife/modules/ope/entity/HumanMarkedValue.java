/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;

/**
 * 数据标注Entity
 * @author chenci
 * @version 2017-09-28
 */
public class HumanMarkedValue extends DataEntity<HumanMarkedValue> {
	
	private static final long serialVersionUID = 1L;
	private Performance performance; //属性值
	private Double score;		// 标注打分
	private String originalValue;		// 原始值
	private String openid;		// 标注用户openid
	private String nickname;		// 标注用户昵称
	private ItemCategory category; //类目
	private Measure measure;		// 关键属性
	
	public HumanMarkedValue() {
		super();
	}

	public HumanMarkedValue(String id){
		super(id);
	}

	public Performance getPerformance() {
		return performance;
	}

	public void setPerformance(Performance performance) {
		this.performance = performance;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}

	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	
}