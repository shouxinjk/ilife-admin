/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 个性化定制模板Entity
 * @author chenci
 * @version 2022-10-29
 */
public class ProposalScheme extends DataEntity<ProposalScheme> {
	
	private static final long serialVersionUID = 1L;
	private ProposalScheme parent;
	private String name;		// 名称
	private String description;		// 描述
	private String type="free";		// 类型:free手动定制 guide指南生成 默认为free
	private String logo; //logo
	private JsonForm form;		// 表单
	private String category;		// 类别
	private Broker broker;		// 创建达人
	private String status;		// 状态
	private Integer priority=100;		// 优先级
	
	public ProposalScheme() {
		super();
	}

	public ProposalScheme(String id){
		super(id);
	}

	public ProposalScheme getParent() {
		return parent;
	}

	public void setParent(ProposalScheme parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public JsonForm getForm() {
		return form;
	}

	public void setForm(JsonForm form) {
		this.form = form;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
}