/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.ProposalScheme;
import com.pcitech.iLife.modules.diy.dao.ProposalSchemeDao;

/**
 * 个性化定制模板Service
 * @author chenci
 * @version 2022-10-29
 */
@Service
@Transactional(readOnly = true)
public class ProposalSchemeService extends CrudService<ProposalSchemeDao, ProposalScheme> {
	//获取分页列表
	public List<ProposalScheme> findPagedList(Map<String,Object> param){
		return dao.findPagedList(param);
	}
	
	public ProposalScheme get(String id) {
		return super.get(id);
	}
	
	public List<ProposalScheme> findList(ProposalScheme proposalScheme) {
		return super.findList(proposalScheme);
	}
	
	public Page<ProposalScheme> findPage(Page<ProposalScheme> page, ProposalScheme proposalScheme) {
		return super.findPage(page, proposalScheme);
	}
	
	@Transactional(readOnly = false)
	public void save(ProposalScheme proposalScheme) {
		super.save(proposalScheme);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProposalScheme proposalScheme) {
		super.delete(proposalScheme);
	}
	
}