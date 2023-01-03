/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.ope.entity.Item;

/**
 * 指南规则条目关联Entity
 * @author chenci
 * @version 2022-10-29
 */
public class GuideTermItem extends DataEntity<GuideTermItem> {
	
	private static final long serialVersionUID = 1L;
	private String name; //名称
	private String description;		// 描述
	private Double weight=1.0;		// 权重：默认为100%
	private GuideTerm term;		// 指南条目
	private Item item;		// 关联的条目
	private String script;		// 匹配规则
	private String scriptDesc;		// 规则描述
	private String tips;		// 注意事项
	private String tags;		// 标签
	private ProposalSubtype type;	 	//subtype类型
	private Integer sort=100;		// sort
	
	public GuideTermItem() {
		super();
	}

	public GuideTermItem(String id){
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=0, max=255, message="描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@NotNull(message="权重不能为空")
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	public GuideTerm getTerm() {
		return term;
	}

	public void setTerm(GuideTerm term) {
		this.term = term;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
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

	public ProposalSubtype getType() {
		return type;
	}

	public void setType(ProposalSubtype type) {
		this.type = type;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
}