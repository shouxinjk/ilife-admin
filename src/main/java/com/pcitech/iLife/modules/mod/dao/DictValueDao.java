/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.mod.entity.DictValue;

/**
 * 业务字典DAO接口
 * @author chenci
 * @version 2022-10-13
 */
@MyBatisDao
public interface DictValueDao extends CrudDao<DictValue> {
	//批量忽略/启用类目相关
	public void batchUpdateCategoryInfo(Map<String,Object> params);
	
	//根据字典获取标注值列表，已排重
	public List<Map<String,Object>> findDistinctValuesByDict(String dictId);
	
	//严格类目检查：根据类目（支持null检查）获取指定字典、指定标注值记录是否存在
	public List<DictValue> getByCategoryCheck(DictValue dictValue);
	
	//根据openid获取待标注字典值列表
	public List<DictValue> findPendingList(Map<String,Object> param);
}