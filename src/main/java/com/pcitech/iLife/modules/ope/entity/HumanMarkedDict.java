/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.DictMeta;
import com.pcitech.iLife.modules.mod.entity.DictValue;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;

/**
 * 用户标注字典Entity
 * @author ilife
 * @version 2022-12-29
 */
public class HumanMarkedDict extends DataEntity<HumanMarkedDict> {
	
	private static final long serialVersionUID = 1L;
	private DictMeta dictMeta;		// 所属字典
	private ItemCategory category;		// 所属类目
	private DictValue dictValue;		// 字典值ID
	private String originalValue;		// 原始值
	private double score=0;		// score
	private String openid;		// 标注者openid
	private String nickname;		// 标注者昵称
	
	public HumanMarkedDict() {
		super();
	}

	public HumanMarkedDict(String id){
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
	
	public DictValue getDictValue() {
		return dictValue;
	}

	public void setDictValue(DictValue dictValue) {
		this.dictValue = dictValue;
	}
	
	@Length(min=0, max=255, message="原始值长度必须介于 0 和 255 之间")
	public String getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	@Length(min=0, max=64, message="标注者openid长度必须介于 0 和 64 之间")
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	@Length(min=0, max=512, message="标注者昵称长度必须介于 0 和 512 之间")
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
}