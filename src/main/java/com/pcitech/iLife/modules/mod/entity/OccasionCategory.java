/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;


import java.util.List;

import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.TreeEntity;

/**
 * 外部诱因类别Entity
 * @author chenci
 * @version 2017-09-15
 */
public class OccasionCategory extends TreeEntity<OccasionCategory> {
	
	private static final long serialVersionUID = 1L;
	private String module="occasionCategory";		// name
	private String triggerDirection; //内在/外在
	private String triggerType;//主动被动
	
	public OccasionCategory() {
		super();
	}

	public OccasionCategory(String id){
		super(id);
	}

	@NotNull(message="父级编号不能为空")
	public OccasionCategory getParent() {
		return parent;
	}

	public void setParent(OccasionCategory parent) {
		this.parent = parent;
	}
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getTriggerDirection() {
		return triggerDirection;
	}

	public void setTriggerDirection(String triggerDirection) {
		this.triggerDirection = triggerDirection;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public static void sortList(List<OccasionCategory> list, List<OccasionCategory> sourcelist, String parentId, boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			OccasionCategory e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						OccasionCategory child = sourcelist.get(j);
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