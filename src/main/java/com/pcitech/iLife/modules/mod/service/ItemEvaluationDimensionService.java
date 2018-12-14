/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.ItemEvaluationDimension;
import com.pcitech.iLife.modules.mod.dao.ItemEvaluationDimensionDao;

/**
 * 主观评价-维度Service
 * @author qchzhu
 * @version 2018-12-14
 */
@Service
@Transactional(readOnly = true)
public class ItemEvaluationDimensionService extends CrudService<ItemEvaluationDimensionDao, ItemEvaluationDimension> {

	public ItemEvaluationDimension get(String id) {
		return super.get(id);
	}
	
	public List<ItemEvaluationDimension> findList(ItemEvaluationDimension itemEvaluationDimension) {
		return super.findList(itemEvaluationDimension);
	}
	
	public Page<ItemEvaluationDimension> findPage(Page<ItemEvaluationDimension> page, ItemEvaluationDimension itemEvaluationDimension) {
		return super.findPage(page, itemEvaluationDimension);
	}
	
	@Transactional(readOnly = false)
	public void save(ItemEvaluationDimension itemEvaluationDimension) {
		super.save(itemEvaluationDimension);
	}
	
	@Transactional(readOnly = false)
	public void delete(ItemEvaluationDimension itemEvaluationDimension) {
		super.delete(itemEvaluationDimension);
	}
	
}