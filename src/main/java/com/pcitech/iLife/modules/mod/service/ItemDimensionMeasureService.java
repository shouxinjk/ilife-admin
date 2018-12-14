/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.ItemDimensionMeasure;
import com.pcitech.iLife.modules.mod.dao.ItemDimensionMeasureDao;

/**
 * 客观评价明细Service
 * @author qchzhu
 * @version 2018-12-12
 */
@Service
@Transactional(readOnly = true)
public class ItemDimensionMeasureService extends CrudService<ItemDimensionMeasureDao, ItemDimensionMeasure> {

	public ItemDimensionMeasure get(String id) {
		return super.get(id);
	}
	
	public List<ItemDimensionMeasure> findList(ItemDimensionMeasure itemDimensionMeasure) {
		return super.findList(itemDimensionMeasure);
	}
	
	public Page<ItemDimensionMeasure> findPage(Page<ItemDimensionMeasure> page, ItemDimensionMeasure itemDimensionMeasure) {
		return super.findPage(page, itemDimensionMeasure);
	}
	
	@Transactional(readOnly = false)
	public void save(ItemDimensionMeasure itemDimensionMeasure) {
		super.save(itemDimensionMeasure);
	}
	
	@Transactional(readOnly = false)
	public void delete(ItemDimensionMeasure itemDimensionMeasure) {
		super.delete(itemDimensionMeasure);
	}
	
}