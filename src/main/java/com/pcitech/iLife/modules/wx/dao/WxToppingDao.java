/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.entity.WxTopping;

/**
 * 置顶记录DAO接口
 * @author ilife
 * @version 2022-04-02
 */
@MyBatisDao
public interface WxToppingDao extends CrudDao<WxTopping> {
	//查询即将发生的展示列表 
	public List<WxTopping> findUpcomingList(Map<String,Object> param);
}