/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 业务字典定义Entity
 * @author chenci
 * @version 2022-10-13
 */
public class DictMeta extends DataEntity<DictMeta> {
	
	private static final long serialVersionUID = 1L;
	private String type;		// 类型
	private String name;		// 名称
	private String dictKey;		// 键值
	private String description;		// 描述
	private Double defaultValue;		// 默认数值
	private Integer defaultLevel;		// 默认等级
	private Double controlValue;		// 控制数值
	private String controlDesc;		// 控制值描述
	private String ignoreCategory;	//是否类目无关
	
	public DictMeta() {
		super();
	}

	public DictMeta(String id){
		super(id);
	}

	@Length(min=1, max=10, message="类型长度必须介于 1 和 10 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=100, message="键值长度必须介于 1 和 100 之间")
	public String getDictKey() {
		return dictKey;
	}

	public void setDictKey(String dictKey) {
		this.dictKey = dictKey;
	}
	
	@Length(min=0, max=512, message="描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Double getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Double defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Integer getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(Integer defaultLevel) {
		this.defaultLevel = defaultLevel;
	}
	
	public Double getControlValue() {
		return controlValue;
	}

	public void setControlValue(Double controlValue) {
		this.controlValue = controlValue;
	}
	
	@Length(min=0, max=512, message="控制值描述长度必须介于 0 和 512 之间")
	public String getControlDesc() {
		return controlDesc;
	}

	public void setControlDesc(String controlDesc) {
		this.controlDesc = controlDesc;
	}

	public String getIgnoreCategory() {
		return ignoreCategory;
	}

	public void setIgnoreCategory(String ignoreCategory) {
		this.ignoreCategory = ignoreCategory;
	}
	
}