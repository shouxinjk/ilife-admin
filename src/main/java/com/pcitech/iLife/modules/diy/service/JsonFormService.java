/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.diy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.diy.entity.JsonForm;
import com.pcitech.iLife.modules.diy.dao.JsonFormDao;

/**
 * 动态表单Service
 * @author chenci
 * @version 2022-10-29
 */
@Service
@Transactional(readOnly = true)
public class JsonFormService extends CrudService<JsonFormDao, JsonForm> {

	public JsonForm get(String id) {
		return super.get(id);
	}
	
	public List<JsonForm> findList(JsonForm jsonForm) {
		return super.findList(jsonForm);
	}
	
	public Page<JsonForm> findPage(Page<JsonForm> page, JsonForm jsonForm) {
		return super.findPage(page, jsonForm);
	}
	
	@Transactional(readOnly = false)
	public void save(JsonForm jsonForm) {
		super.save(jsonForm);
	}
	
	@Transactional(readOnly = false)
	public void delete(JsonForm jsonForm) {
		super.delete(jsonForm);
	}
	
}