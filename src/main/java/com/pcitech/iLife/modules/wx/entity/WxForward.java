/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 开白请求Entity
 * @author ilife
 * @version 2022-05-11
 */
public class WxForward extends DataEntity<WxForward> {
	
	private static final long serialVersionUID = 1L;
	private Broker requester;		// 请求达人
	private Broker responder;		// 被请求达人
	private String subjectType;		// 开白内容类型
	private String subjectId;		// 开白内容ID
	private String type;		// 开白类别
	private String status;		// 开白状态
	private WxArticle article;	//开白指定的文章
	private WxAccount account;	//开白指定的公众号
	private WxAccount requestAccount;	//请求者公众号：因为支持多个公众号，每一个请求需要指定公众号
	
	public WxForward() {
		super();
	}

	public WxForward(String id){
		super(id);
	}

	public Broker getRequester() {
		return requester;
	}

	public void setRequester(Broker requester) {
		this.requester = requester;
	}
	
	public Broker getResponder() {
		return responder;
	}

	public void setResponder(Broker responder) {
		this.responder = responder;
	}
	
	@Length(min=1, max=20, message="开白内容类型长度必须介于 1 和 20 之间")
	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	
	@Length(min=1, max=32, message="开白内容ID长度必须介于 1 和 32 之间")
	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	
	@Length(min=1, max=20, message="开白类别长度必须介于 1 和 20 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=1, max=20, message="开白状态长度必须介于 1 和 20 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public WxArticle getArticle() {
		return article;
	}

	public void setArticle(WxArticle article) {
		this.article = article;
	}

	public WxAccount getAccount() {
		return account;
	}

	public void setAccount(WxAccount account) {
		this.account = account;
	}

	public WxAccount getRequestAccount() {
		return requestAccount;
	}

	public void setRequestAccount(WxAccount requestAccount) {
		this.requestAccount = requestAccount;
	}
	
}