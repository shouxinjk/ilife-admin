/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.persistence.TreeNode;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.common.utils.CacheUtils;
import com.pcitech.iLife.modules.mod.entity.DictMeta;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.ope.dao.PerformanceDao;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.modules.sys.utils.DictUtils;
import com.pcitech.iLife.util.DataUtils;
import com.pcitech.iLife.modules.mod.dao.DictMetaDao;

/**
 * 业务字典定义Service
 * @author chenci
 * @version 2022-10-13
 */
@Service
@Transactional(readOnly = true)
public class DictMetaService extends CrudService<DictMetaDao, DictMeta> {
	@Autowired
	DictService dictService;
	
	public DictMeta get(String id) {
		return super.get(id);
	}
	
	public List<DictMeta> findList(DictMeta dictMeta) {
		return super.findList(dictMeta);
	}
	
	public Page<DictMeta> findPage(Page<DictMeta> page, DictMeta dictMeta) {
		return super.findPage(page, dictMeta);
	}
	
	@Transactional(readOnly = false)
	public void save(DictMeta dictMeta) {
		super.save(dictMeta);
		CacheUtils.remove(DataUtils.CACHE_DICT_MAP);
	}
	
	@Transactional(readOnly = false)
	public void delete(DictMeta dictMeta) {
		super.delete(dictMeta);
		CacheUtils.remove(DataUtils.CACHE_DICT_MAP);
	}
	
}