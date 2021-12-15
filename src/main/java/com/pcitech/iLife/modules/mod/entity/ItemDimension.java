/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.TreeEntity;

import java.util.List;

/**
 * 商品维度Entity
 * @author chenci
 * @version 2018-06-22
 */
public class ItemDimension extends TreeEntity<ItemDimension> {
	
	private static final long serialVersionUID = 1L;
	private ItemCategory category;		// 类别
	private String name;		// 名称
	private String description;		// 描述
	private double weight;		// 占比
	private String script;		// script：评价脚本
	private boolean featured;		// featured
	private String propKey;		// 键名，用于计算引用
	
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public String getPropKey() {
		return propKey;
	}

	public void setPropKey(String propKey) {
		this.propKey = propKey;
	}

	public ItemDimension() {
		super();
	}

	public ItemDimension(String id){
		super(id);
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
	@JsonBackReference
	@NotNull(message="父级编号不能为空")
	public ItemDimension getParent() {
		return parent;
	}

	public void setParent(ItemDimension parent) {
		this.parent = parent;
	}
	
	@Length(min=1, max=2000, message="所有父级编号长度必须介于 1 和 2000 之间")
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	
	@Length(min=0, max=20, message="名称长度必须介于 0 和 20 之间")
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
	
	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}

	public static void sortList(List<ItemDimension> list, List<ItemDimension> sourcelist, String parentId, boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			ItemDimension e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						ItemDimension child = sourcelist.get(j);
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