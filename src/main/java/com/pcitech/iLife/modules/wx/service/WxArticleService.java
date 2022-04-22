/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.dao.WxArticleDao;

/**
 * 微信文章管理Service
 * @author ilife
 * @version 2022-03-28
 */
@Service
@Transactional(readOnly = true)
public class WxArticleService extends CrudService<WxArticleDao, WxArticle> {
	//根据openid获取待阅班车文章列表
	public List<WxArticle> findPendingGroupingList(Map<String,Object> param){
		return dao.findPendingGroupingList(param);
	}
	//根据openid获取待阅读普通文章列表
	public List<WxArticle> findPendingList(Map<String,Object> param){
		return dao.findPendingList(param);
	}
	//根据openid获取待阅读置顶文章列表
	public List<WxArticle> findToppingList(Map<String,Object> param){
		return dao.findToppingList(param);
	}
	//根据openid获取已发布文章列表
	public List<WxArticle> findMyArticles(Map<String,Object> param){
		return dao.findMyArticles(param);
	}
	//根据openid获取已发布文章总数
	public int countMyArticles(String openid){
		return dao.countMyArticles(openid);
	}
	
	public WxArticle get(String id) {
		return super.get(id);
	}
	
	public List<WxArticle> findList(WxArticle wxArticle) {
		return super.findList(wxArticle);
	}
	
	public Page<WxArticle> findPage(Page<WxArticle> page, WxArticle wxArticle) {
		return super.findPage(page, wxArticle);
	}
	
	@Transactional(readOnly = false)
	public void save(WxArticle wxArticle) {
		super.save(wxArticle);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxArticle wxArticle) {
		super.delete(wxArticle);
	}
	
}