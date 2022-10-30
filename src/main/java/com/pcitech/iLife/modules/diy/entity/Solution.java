/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 个性定制方案Entity
 * @author chenci
 * @version 2022-10-29
 */
public class Solution extends DataEntity<Solution> {
	
	private static final long serialVersionUID = 1L;
	private ProposalScheme scheme;		// 所属主题
	private Solution refer;		// 引用方案
	private String name;		// 名称
	private String forOpenid;		// 目标用户
	private String byOpenid;		// 定制用户
	private Integer status;		// 状态
	private String description;		// 描述
	
	public Solution() {
		super();
	}

	public Solution(String id){
		super(id);
	}

	public ProposalScheme getScheme() {
		return scheme;
	}

	public void setScheme(ProposalScheme scheme) {
		this.scheme = scheme;
	}
	
	public Solution getRefer() {
		return refer;
	}

	public void setRefer(Solution refer) {
		this.refer = refer;
	}
	
	@Length(min=1, max=50, message="名称长度必须介于 1 和 50 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=64, message="目标用户长度必须介于 0 和 64 之间")
	public String getForOpenid() {
		return forOpenid;
	}

	public void setForOpenid(String forOpenid) {
		this.forOpenid = forOpenid;
	}
	
	@Length(min=0, max=64, message="定制用户长度必须介于 0 和 64 之间")
	public String getByOpenid() {
		return byOpenid;
	}

	public void setByOpenid(String byOpenid) {
		this.byOpenid = byOpenid;
	}
	
	@NotNull(message="状态不能为空")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Length(min=0, max=512, message="描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}