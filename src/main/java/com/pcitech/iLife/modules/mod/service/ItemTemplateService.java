/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.ItemTemplate;
import com.pcitech.iLife.modules.mod.dao.ItemTemplateDao;

/**
 * 类目推广文案Service
 * @author qchzhu
 * @version 2021-07-21
 */
@Service
@Transactional(readOnly = true)
public class ItemTemplateService extends CrudService<ItemTemplateDao, ItemTemplate> {

	public ItemTemplate get(String id) {
		return super.get(id);
	}
	
	public List<ItemTemplate> findList(ItemTemplate template) {
		return super.findList(template);
	}
	
	public List<ItemTemplate> findByCategory(String category) {
		return dao.findByCategoryId(category);
	}
	
	public Page<ItemTemplate> findPage(Page<ItemTemplate> page, ItemTemplate template) {
		return super.findPage(page, template);
	}
	
	@Transactional(readOnly = false)
	public void save(ItemTemplate template) {
		super.save(template);
	}
	
	@Transactional(readOnly = false)
	public void delete(ItemTemplate template) {
		super.delete(template);
	}
	
}