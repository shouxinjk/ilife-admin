/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.ope.entity.Item;
import com.pcitech.iLife.modules.ope.dao.ItemDao;

/**
 * 商品Service
 * @author chenci
 * @version 2017-09-28
 */
@Service
@Transactional(readOnly = true)
public class ItemService extends CrudService<ItemDao, Item> {

	public Item get(String id) {
		return super.get(id);
	}
	
	public List<Item> findList(Item item) {
		return super.findList(item);
	}
	
	public Page<Item> findPage(Page<Item> page, Item item) {
		return super.findPage(page, item);
	}
	
	@Transactional(readOnly = false)
	public void save(Item item) {
		super.save(item);
	}
	
	@Transactional(readOnly = false)
	public void delete(Item item) {
		super.delete(item);
	}
	
}