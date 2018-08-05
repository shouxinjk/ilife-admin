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
 * vals分类Entity
 * @author chenci
 * @version 2018-08-05
 */
public class LifeStyleCategory extends TreeEntity<LifeStyleCategory> {
	
	private static final long serialVersionUID = 1L;

	private String logo;		// logo
	private String description;		// 描述
	
	public LifeStyleCategory() {
		super();
	}

	public LifeStyleCategory(String id){
		super(id);
	}

	@JsonBackReference
	@NotNull(message="父级编号不能为空")
	public LifeStyleCategory getParent() {
		return parent;
	}

	public void setParent(LifeStyleCategory parent) {
		this.parent = parent;
	}
	
	@Length(min=1, max=2000, message="所有父级编号长度必须介于 1 和 2000 之间")
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	
	@Length(min=0, max=2000, message="logo长度必须介于 0 和 2000 之间")
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

	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}

	public static void sortList(List<LifeStyleCategory> list, List<LifeStyleCategory> sourcelist, String parentId, boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			LifeStyleCategory e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						LifeStyleCategory child = sourcelist.get(j);
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