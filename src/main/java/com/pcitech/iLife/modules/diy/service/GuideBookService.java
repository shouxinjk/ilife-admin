/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.GuideBook;
import com.pcitech.iLife.modules.diy.dao.GuideBookDao;

/**
 * 个性化定制指南Service
 * @author chenci
 * @version 2022-10-29
 */
@Service
@Transactional(readOnly = true)
public class GuideBookService extends CrudService<GuideBookDao, GuideBook> {

	public List<GuideBook> findPendingListForProposal(String schemeId){
		return dao.findPendingListForProposal(schemeId);
	}
	
	public GuideBook get(String id) {
		return super.get(id);
	}
	
	public List<GuideBook> findList(GuideBook guideBook) {
		return super.findList(guideBook);
	}
	
	public Page<GuideBook> findPage(Page<GuideBook> page, GuideBook guideBook) {
		return super.findPage(page, guideBook);
	}
	
	@Transactional(readOnly = false)
	public void save(GuideBook guideBook) {
		super.save(guideBook);
	}
	
	@Transactional(readOnly = false)
	public void delete(GuideBook guideBook) {
		super.delete(guideBook);
	}
	
}