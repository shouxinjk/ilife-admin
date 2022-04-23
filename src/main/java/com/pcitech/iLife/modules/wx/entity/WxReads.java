/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 阅读记录Entity
 * @author ilife
 * @version 2022-03-31
 */
public class WxReads extends DataEntity<WxReads> {
	
	private static final long serialVersionUID = 1L;
	private WxArticle article;		// 文章
	private Broker broker;		// 读者broker
	private String openid;		// 读者openid
	private String grouping;		// 互阅班车code
	private Integer readCount;		// 阅读报数
	
	public WxReads() {
		super();
	}

	public WxReads(String id){
		super(id);
	}


	
	public WxArticle getArticle() {
		return article;
	}

	public void setArticle(WxArticle article) {
		this.article = article;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	@Length(min=1, max=128, message="读者openid长度必须介于 1 和 128 之间")
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	public String getGrouping() {
		return grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
	}

	@NotNull(message="阅读报数不能为空")
	public Integer getReadCount() {
		return readCount;
	}

	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}
	
}