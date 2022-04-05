/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxAccount;

/**
 * 微信公众号管理DAO接口
 * @author ilife
 * @version 2022-03-28
 */
@MyBatisDao
public interface WxAccountDao extends CrudDao<WxAccount> {
	//根据openid获取待阅读普通公众号列表
	public List<WxAccount> findPendingList(Map<String,Object> param);
	//根据openid获取待阅读置顶公众号列表
	public List<WxAccount> findToppingList(Map<String,Object> param);
	//根据openid获取已发布公众号列表 
	public List<WxAccount> findMyAccounts(Map<String,Object> param);
	//根据openid获取已发布公众号总数
	public int countMyAccounts(String openid);
}