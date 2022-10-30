/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 个性化定制指南Entity
 * @author chenci
 * @version 2022-10-29
 */
public class GuideBook extends DataEntity<GuideBook> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 全称
	private String alias;		// 简称
	private String origin;		// 来源
	private String revision;		// 版本
	private String tags;		// 标签
	private String description;		// 描述
	private String type;		// 类型
	private String url;		// URL
	private Integer status;		// 状态
	
	public GuideBook() {
		super();
	}

	public GuideBook(String id){
		super(id);
	}

	@Length(min=1, max=512, message="全称长度必须介于 1 和 512 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=512, message="简称长度必须介于 0 和 512 之间")
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Length(min=0, max=512, message="来源长度必须介于 0 和 512 之间")
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	@Length(min=0, max=512, message="版本长度必须介于 0 和 512 之间")
	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	@Length(min=0, max=512, message="标签长度必须介于 0 和 512 之间")
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	@Length(min=0, max=512, message="描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=20, message="类型长度必须介于 0 和 20 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=1024, message="URL长度必须介于 0 和 1024 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@NotNull(message="状态不能为空")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}