/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;

/**
 * 个性化定制指南条目Entity
 * @author chenci
 * @version 2022-10-29
 */
public class GuideTerm extends DataEntity<GuideTerm> {
	
	private static final long serialVersionUID = 1L;
	private GuideBook book;		// 指南
	private ProposalSection section;		// Section
	private String name;		// 名称
	private String description;		// 描述
	private ItemCategory category;	//类目：注意该类目在GuideTerm上不存储，直接引用来源于GuideBook的类目
	private String tips;		// 注意事项
	private String tags;		// 标签
	private String criteria;		// 适用条件
	private String criteriaDesc;		// 适用条件描述
	private String script;		// 脚本规则
	private String scriptDesc;		// 脚本描述
	private Integer priority=100;		// 排序先后
	
	public GuideTerm() {
		super();
	}

	public GuideTerm(String id){
		super(id);
	}
	
	public GuideBook getBook() {
		return book;
	}

	public void setBook(GuideBook book) {
		this.book = book;
	}
	
	public ProposalSection getSection() {
		return section;
	}

	public void setSection(ProposalSection section) {
		this.section = section;
	}
	
	@Length(min=1, max=50, message="名称长度必须介于 1 和 50 之间")
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

	@Length(min=0, max=512, message="描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	
	public String getCriteriaDesc() {
		return criteriaDesc;
	}

	public void setCriteriaDesc(String criteriaDesc) {
		this.criteriaDesc = criteriaDesc;
	}
	
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
	public String getScriptDesc() {
		return scriptDesc;
	}

	public void setScriptDesc(String scriptDesc) {
		this.scriptDesc = scriptDesc;
	}
	
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
}