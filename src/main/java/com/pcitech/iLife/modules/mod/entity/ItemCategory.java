/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.common.persistence.TreeEntity;

/**
 * 商品分类Entity
 * @author chenci
 * @version 2017-09-22
 */
public class ItemCategory extends TreeEntity<ItemCategory> {
	
	private static final long serialVersionUID = 1L;
	private String alias; //别名
	private String logo; //logo
	private String description; //描述
	private String motivationIds;		// 满足动机id
	private String occasionIds;		// 满足诱因id
	private String motivationNames;		// 满足动机
	private String occasionNames;		// 满足诱因
	private String expressionDesc;		// 适用条件
	private String expression;		// 适用条件量化
	private String outlineTemplate;		// 摘要模版
	private String scenarioId;		// 场景标签
	private String tags; //该标准目录的标签 ，主要用于映射其他电商平台的目录
	
	public ItemCategory() {
		super();
	}

	public ItemCategory(String id){
		super(id);
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@JsonBackReference
	@NotNull(message="父级编号不能为空")
	public ItemCategory getParent() {
		return parent;
	}

	public void setParent(ItemCategory parent) {
		this.parent = parent;
	}
	
	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public String getMotivationIds() {
		return motivationIds;
	}

	public void setMotivationIds(String motivationIds) {
		this.motivationIds = motivationIds;
	}

	public String getOccasionIds() {
		return occasionIds;
	}

	public void setOccasionIds(String occasionIds) {
		this.occasionIds = occasionIds;
	}

	public String getMotivationNames() {
		return motivationNames;
	}

	public void setMotivationNames(String motivationNames) {
		this.motivationNames = motivationNames;
	}

	public String getOccasionNames() {
		return occasionNames;
	}

	public void setOccasionNames(String occasionNames) {
		this.occasionNames = occasionNames;
	}

	@Length(min=0, max=500, message="适用条件长度必须介于 0 和 500 之间")
	public String getExpressionDesc() {
		return expressionDesc;
	}

	public void setExpressionDesc(String expressionDesc) {
		this.expressionDesc = expressionDesc;
	}
	
	@Length(min=0, max=500, message="适用条件量化长度必须介于 0 和 500 之间")
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Length(min=0, max=1000, message="摘要模版长度必须介于 0 和 1000 之间")
	public String getOutlineTemplate() {
		return outlineTemplate;
	}

	public void setOutlineTemplate(String outlineTemplate) {
		this.outlineTemplate = outlineTemplate;
	}
	
	@Length(min=0, max=100, message="场景标签长度必须介于 0 和 100 之间")
	public String getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}
	
	
	public static void sortList(List<ItemCategory> list, List<ItemCategory> sourcelist, String parentId,boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			ItemCategory e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						ItemCategory child = sourcelist.get(j);
						if (child.getParent()!=null && child.getParent().getId()!=null
								&& child.getParent().getId().equals(e.getId())){
							sortList(list, sourcelist, e.getId(),true);
							break;
						}
					}
				}
			}
		}
	}
	
	public static boolean isRoot(String id){
		return id != null && id.equals("1");
	}
}