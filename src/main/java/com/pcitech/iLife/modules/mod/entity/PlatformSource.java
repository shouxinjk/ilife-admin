/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 商品数据来源Entity
 * @author ilife
 * @version 2022-03-03
 */
public class PlatformSource extends DataEntity<PlatformSource> {
	
	private static final long serialVersionUID = 1L;
	private String platform;		// 所属平台
	private String platformName;		// 所属平台名称：来源于字典表
	private String category;		// 类别名称
	private String type;		// 收益类型
	private String description;		// 描述
	private String url;		// 入口URL地址
	private String userscript;		// UserScript脚本地址
	private String matchExpr;		// 支持的URL规则
	private String sort;		// 排序
	private String status;		// 状态
	
	public PlatformSource() {
		super();
	}

	public PlatformSource(String id){
		super(id);
	}

	@Length(min=1, max=32, message="所属平台长度必须介于 1 和 32 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	@Length(min=0, max=100, message="类别名称长度必须介于 0 和 100 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Length(min=1, max=100, message="收益类型长度必须介于 1 和 100 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=100, message="描述长度必须介于 0 和 100 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=512, message="入口URL地址长度必须介于 0 和 512 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Length(min=0, max=512, message="UserScript脚本地址长度必须介于 0 和 512 之间")
	public String getUserscript() {
		return userscript;
	}

	public void setUserscript(String userscript) {
		this.userscript = userscript;
	}
	
	public String getMatchExpr() {
		return matchExpr;
	}

	public void setMatchExpr(String matchExpr) {
		this.matchExpr = matchExpr;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
	@Length(min=0, max=64, message="状态长度必须介于 0 和 64 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}