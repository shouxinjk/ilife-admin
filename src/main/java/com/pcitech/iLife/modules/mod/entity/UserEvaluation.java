/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.TreeEntity;

/**
 * 用户主观评价Entity
 * @author qchzhu
 * @version 2019-03-13
 */
public class UserEvaluation extends TreeEntity<UserEvaluation> {
	
	private static final long serialVersionUID = 1L;
	private UserEvaluation parent;		// 父级编号
	private String parentIds;		// 所有父级编号
	private String name;		// name
	private String description;		// description
	private double weight;		// weight
	private String type;		// type
	private String script;		// script
	private String scriptType="auto";		// 脚本类型：auto manual
	private String scriptMemo;		// 可读脚本
	private boolean featured;		// featured
	private String sort;		// sort
	private String category;		// category
	private String propKey;		// 键名，用于计算引用
	
	public UserEvaluation() {
		super();
	}

	public UserEvaluation(String id){
		super(id);
	}

	@JsonBackReference
	@NotNull(message="父级编号不能为空")
	public UserEvaluation getParent() {
		return parent;
	}

	public void setParent(UserEvaluation parent) {
		this.parent = parent;
	}
	
	@Length(min=1, max=2000, message="所有父级编号长度必须介于 1 和 2000 之间")
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	
	@Length(min=0, max=20, message="name长度必须介于 0 和 20 之间")
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
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	@Length(min=1, max=64, message="type长度必须介于 1 和 64 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public String getScriptMemo() {
		return scriptMemo;
	}

	public void setScriptMemo(String scriptMemo) {
		this.scriptMemo = scriptMemo;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
	
	@Length(min=0, max=64, message="category长度必须介于 0 和 64 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getPropKey() {
		return propKey;
	}

	public void setPropKey(String propKey) {
		this.propKey = propKey;
	}

	public static void sortList(List<UserEvaluation> list, List<UserEvaluation> sourcelist, String parentId,boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			UserEvaluation e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						UserEvaluation child = sourcelist.get(j);
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
	
	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}
}