/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.service;

import java.util.List;

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