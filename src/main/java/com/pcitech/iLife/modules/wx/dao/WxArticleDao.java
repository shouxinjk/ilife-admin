/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.dao;

import java.util.List;
import java.util.Map;

import com.pcitech.iLife.common.persistence.CrudDao;
import com.pcitech.iLife.common.persistence.annotation.MyBatisDao;
import com.pcitech.iLife.modules.wx.entity.WxArticle;

/**
 * 微信文章管理DAO接口
 * @author ilife
 * @version 2022-03-28
 */
@MyBatisDao
public interface WxArticleDao extends CrudDao<WxArticle> {
	//根据openid获取待阅读普通文章列表
	public List<WxArticle> findPendingList(Map<String,Object> param);
	//根据openid获取待阅读置顶文章列表
	public List<WxArticle> findToppingList(String openid);
	//根据openid获取已发布文章列表 
	public List<WxArticle> findMyArticles(Map<String,Object> param);
	//根据openid获取已发布文章总数
	public int countMyArticles(String openid);
}