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
	private String name;		// 名称
	private String description;		// 描述
	private String type="free";		// 类型:free手动定制 guide指南生成 默认为free
	private JsonForm form;		// 表单
	private String category;		// 类别
	private Broker broker;		// 创建达人
	private String status="0";		// 状态
	private Integer priority=100;		// 优先级
	
	public ProposalScheme() {
		super();
	}

	public ProposalScheme(String id){
		super(id);
	}

	@Length(min=1, max=50, message="名称长度必须介于 1 和 50 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=512, message="描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=1, max=20, message="类型长度必须介于 1 和 20 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public JsonForm getForm() {
		return form;
	}

	public void setForm(JsonForm form) {
		this.form = form;
	}
	
	@Length(min=0, max=50, message="类别长度必须介于 0 和 50 之间")
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
	
	@Length(min=1, max=1, message="状态长度必须介于 1 和 1 之间")
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