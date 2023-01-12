/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 排行榜Entity
 * @author ilife
 * @version 2023-01-11
 */
public class Rank extends DataEntity<Rank> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 名称
	private ItemCategory category;		// 类目
	private String keywords;	//主题关键字：用于搜索使用，能够限制排行榜条目
	private String logo;		// LOGO，预留
	private String description;		// 描述
	private String openid;		// 创建者openid
	private String nickname;		// 创建者昵称
	
	public Rank() {
		super();
	}

	public Rank(String id){
		super(id);
	}

	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@Length(min=0, max=512, message="LOGO，预留长度必须介于 0 和 512 之间")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	@Length(min=0, max=512, message="描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=64, message="创建者openid长度必须介于 0 和 64 之间")
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	@Length(min=0, max=256, message="创建者昵称长度必须介于 0 和 256 之间")
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
}