/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.ProposalSection;
import com.pcitech.iLife.modules.diy.dao.ProposalSectionDao;

/**
 * 个性化定制章节Service
 * @author chenci
 * @version 2022-10-29
 */
@Service
@Transactional(readOnly = true)
public class ProposalSectionService extends CrudService<ProposalSectionDao, ProposalSection> {

	public ProposalSection get(String id) {
		return super.get(id);
	}
	
	public List<ProposalSection> findList(ProposalSection proposalSection) {
		return super.findList(proposalSection);
	}
	
	public Page<ProposalSection> findPage(Page<ProposalSection> page, ProposalSection proposalSection) {
		return super.findPage(page, proposalSection);
	}
	
	@Transactional(readOnly = false)
	public void save(ProposalSection proposalSection) {
		super.save(proposalSection);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProposalSection proposalSection) {
		super.delete(proposalSection);
	}
	
}