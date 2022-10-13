/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 业务字典Entity
 * @author chenci
 * @version 2022-10-13
 */
public class DictValue extends DataEntity<DictValue> {
	
	private static final long serialVersionUID = 1L;
	private DictMeta dictMeta;		// 字典
	private ItemCategory category;		// 应用类目
	private String originalValue;		// 原始数值
	private Double markedValue;		// 标注值
	private Double score;		// 预留，标注得分，实际是1-10分级值
	private int isMarked = 1; //是否已标注：0待标注，1已标注；默认为已标注。通过管理系统建立或修改均认为已标注。直接通过SQL操作认为未标注
	
	public DictValue() {
		super();
	}

	public DictValue(String id){
		super(id);
	}

	public DictMeta getDictMeta() {
		return dictMeta;
	}

	public void setDictMeta(DictMeta dictMeta) {
		this.dictMeta = dictMeta;
	}
	
	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
	@Length(min=1, max=100, message="原始数值长度必须介于 1 和 100 之间")
	public String getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}
	
	public Double getMarkedValue() {
		return markedValue;
	}

	public void setMarkedValue(Double markedValue) {
		this.markedValue = markedValue;
	}
	
	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public boolean isMarked() {
		return isMarked>0;
	}
	
	public int getIsMarked() {
		return isMarked;
	}

	public void setIsMarked(int isMarked) {
		this.isMarked = isMarked;
	}
	
}