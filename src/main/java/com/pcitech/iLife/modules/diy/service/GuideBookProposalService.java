/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.GuideBookProposal;
import com.pcitech.iLife.modules.diy.dao.GuideBookProposalDao;

/**
 * 定制指南主题关联Service
 * @author chenci
 * @version 2022-10-29
 */
@Service
@Transactional(readOnly = true)
public class GuideBookProposalService extends CrudService<GuideBookProposalDao, GuideBookProposal> {

	public GuideBookProposal get(String id) {
		return super.get(id);
	}
	
	public List<GuideBookProposal> findList(GuideBookProposal guideBookProposal) {
		return super.findList(guideBookProposal);
	}
	
	public Page<GuideBookProposal> findPage(Page<GuideBookProposal> page, GuideBookProposal guideBookProposal) {
		return super.findPage(page, guideBookProposal);
	}
	
	@Transactional(readOnly = false)
	public void save(GuideBookProposal guideBookProposal) {
		super.save(guideBookProposal);
	}
	
	@Transactional(readOnly = false)
	public void delete(GuideBookProposal guideBookProposal) {
		super.delete(guideBookProposal);
	}
	
}