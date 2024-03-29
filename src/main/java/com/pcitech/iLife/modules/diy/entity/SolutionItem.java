/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 个性定制方案条目Entity
 * @author chenci
 * @version 2022-10-29
 */
public class SolutionItem extends DataEntity<SolutionItem> {
	
	private static final long serialVersionUID = 1L;
	private Solution solution;		// 方案
	private GuideTermItem guideTermItem; //引用的指南规则：对于自动指南将生成该字段
	private String name;		// 名称
	private String description;		// 描述
	private String tags;		// 标签
	private ProposalSubtype type;		// 类型
	private String itemIds;		// 关联条目
	private double priority=100;		// 排序

	
	public SolutionItem() {
		super();
	}

	public SolutionItem(String id){
		super(id);
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}
	
	public GuideTermItem getGuideTermItem() {
		return guideTermItem;
	}

	public void setGuideTermItem(GuideTermItem guideTermItem) {
		this.guideTermItem = guideTermItem;
	}

	@Length(min=1, max=50, message="名称长度必须介于 1 和 50 之间")
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
	
	@Length(min=0, max=512, message="标签长度必须介于 0 和 512 之间")
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public ProposalSubtype getType() {
		return type;
	}

	public void setType(ProposalSubtype type) {
		this.type = type;
	}
	
	public String getItemIds() {
		return itemIds;
	}

	public void setItemIds(String itemIds) {
		this.itemIds = itemIds;
	}
	
	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}
	
}