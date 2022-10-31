/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.ProposalSubtype;
import com.pcitech.iLife.modules.diy.dao.ProposalSubtypeDao;

/**
 * 个性化定制小类Service
 * @author chenci
 * @version 2022-10-31
 */
@Service
@Transactional(readOnly = true)
public class ProposalSubtypeService extends CrudService<ProposalSubtypeDao, ProposalSubtype> {

	public ProposalSubtype get(String id) {
		return super.get(id);
	}
	
	public List<ProposalSubtype> findList(ProposalSubtype proposalSubtype) {
		return super.findList(proposalSubtype);
	}
	
	public Page<ProposalSubtype> findPage(Page<ProposalSubtype> page, ProposalSubtype proposalSubtype) {
		return super.findPage(page, proposalSubtype);
	}
	
	@Transactional(readOnly = false)
	public void save(ProposalSubtype proposalSubtype) {
		super.save(proposalSubtype);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProposalSubtype proposalSubtype) {
		super.delete(proposalSubtype);
	}
	
}