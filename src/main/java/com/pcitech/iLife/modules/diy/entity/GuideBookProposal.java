/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 定制指南主题关联Entity
 * @author chenci
 * @version 2022-10-29
 */
public class GuideBookProposal extends DataEntity<GuideBookProposal> {
	
	private static final long serialVersionUID = 1L;
	private GuideBook guide;		// 指南
	private ProposalScheme proposal;		// 主题
	
	public GuideBookProposal() {
		super();
	}

	public GuideBookProposal(String id){
		super(id);
	}

	public GuideBook getGuide() {
		return guide;
	}

	public void setGuide(GuideBook guide) {
		this.guide = guide;
	}
	
	public ProposalScheme getProposal() {
		return proposal;
	}

	public void setProposal(ProposalScheme proposal) {
		this.proposal = proposal;
	}
	
}