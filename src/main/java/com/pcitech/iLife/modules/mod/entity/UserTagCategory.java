/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.common.persistence.TreeEntity;

/**
 * 用户标签分类Entity
 * @author qchzhu
 * @version 2020-04-30
 */
public class UserTagCategory extends TreeEntity<UserTagCategory> {

	private static final long serialVersionUID = 1L;
	private String isExclusive;		// 是否排斥
	private String type;		// 类型
	
	public UserTagCategory() {
		super();
	}

	public UserTagCategory(String id){
		super(id);
	}

	@Length(min=0, max=10, message="是否排斥长度必须介于 0 和 10 之间")
	public String getIsExclusive() {
		return isExclusive;
	}
	
	public void setIsExclusive(String isExclusive) {
		this.isExclusive = isExclusive;
	}
	
	@Length(min=0, max=100, message="类型长度必须介于 0 和 100 之间")
	public String getType() {

		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@JsonBackReference
	@NotNull(message="父级编号不能为空")
	public UserTagCategory getParent() {
		return parent;
	}

	public void setParent(UserTagCategory parent) {
		this.parent = parent;
	}
	
	public static void sortList(List<UserTagCategory> list, List<UserTagCategory> sourcelist, String parentId,boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			UserTagCategory e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						UserTagCategory child = sourcelist.get(j);
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