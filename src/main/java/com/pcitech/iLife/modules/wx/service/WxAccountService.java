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
import com.pcitech.iLife.modules.wx.entity.WxAccount;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.dao.WxAccountDao;

/**
 * 微信公众号管理Service
 * @author ilife
 * @version 2022-03-28
 */
@Service
@Transactional(readOnly = true)
public class WxAccountService extends CrudService<WxAccountDao, WxAccount> {

	//根据openid获取待阅读普通公众号列表
	public List<WxAccount> findPendingList(Map<String,Object> param){
		return dao.findPendingList(param);
	}
	//根据openid获取待阅读置顶公众号列表
	public List<WxAccount> findToppingList(String openid){
		return dao.findToppingList(openid);
	}
	//根据openid获取已发布公众号列表
	public List<WxAccount> findMyAccounts(Map<String,Object> param){
		return dao.findMyAccounts(param);
	}
	//根据openid获取已发布公众号总数
	public int countMyAccounts(String openid){
		return dao.countMyAccounts(openid);
	}
	
	public WxAccount get(String id) {
		return super.get(id);
	}
	
	public List<WxAccount> findList(WxAccount wxAccount) {
		return super.findList(wxAccount);
	}
	
	public Page<WxAccount> findPage(Page<WxAccount> page, WxAccount wxAccount) {
		return super.findPage(page, wxAccount);
	}
	
	@Transactional(readOnly = false)
	public void save(WxAccount wxAccount) {
		super.save(wxAccount);
	}
	
	@Transactional(readOnly = false)
	public void delete(WxAccount wxAccount) {
		super.delete(wxAccount);
	}
	
}