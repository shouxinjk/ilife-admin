/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 积分规则Entity
 * @author qchzhu
 * @version 2019-10-30
 */
public class CreditScheme extends DataEntity<CreditScheme> {
	
	private static final long serialVersionUID = 1L;
	private String platform;		// 所属平台
	private String category;		// 所属品类
	private String script;		// 积分规则
	
	public CreditScheme() {
		super();
	}

	public CreditScheme(String id){
		super(id);
	}

	@Length(min=0, max=64, message="所属平台长度必须介于 0 和 64 之间")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@Length(min=0, max=64, message="所属品类长度必须介于 0 和 64 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
}