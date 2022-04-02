/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.Broker;

/**
 * 置顶记录Entity
 * @author ilife
 * @version 2022-04-02
 */
public class WxTopping extends DataEntity<WxTopping> {
	
	private static final long serialVersionUID = 1L;
	private Broker broker;		// 达人
	private String advertiseType;		// 购买类型
	private Date advertiseDate;		// 展示日期
	private Date advertiseTimeFrom;		// 展示开始时间
	private Date advertiseTimeTo;		// 展示结束时间
	private int advertiseWeight;		// 展示权重
	private int advertisePrice;		// 价格(分)
	private WxArticle  article; //展示主题：文章
	private WxAccount account;	//展示主题：公众号
	private WxAdvertise advertise;		// 广告位
	private String subjectType;		// 展示内容类型
	private String subjectId;		// 展示内容ID
	
	public WxTopping() {
		super();
	}

	public WxTopping(String id){
		super(id);
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

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public WxAdvertise getAdvertise() {
		return advertise;
	}

	public void setAdvertise(WxAdvertise advertise) {
		this.advertise = advertise;
	}

	@Length(min=0, max=32, message="购买类型长度必须介于 0 和 32 之间")
	public String getAdvertiseType() {
		return advertiseType;
	}

	public void setAdvertiseType(String advertiseType) {
		this.advertiseType = advertiseType;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getAdvertiseDate() {
		return advertiseDate;
	}

	public void setAdvertiseDate(Date advertiseDate) {
		this.advertiseDate = advertiseDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getAdvertiseTimeFrom() {
		return advertiseTimeFrom;
	}

	public void setAdvertiseTimeFrom(Date advertiseTimeFrom) {
		this.advertiseTimeFrom = advertiseTimeFrom;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getAdvertiseTimeTo() {
		return advertiseTimeTo;
	}

	public void setAdvertiseTimeTo(Date advertiseTimeTo) {
		this.advertiseTimeTo = advertiseTimeTo;
	}
	
	public Integer getAdvertiseWeight() {
		return advertiseWeight;
	}

	public void setAdvertiseWeight(Integer advertiseWeight) {
		this.advertiseWeight = advertiseWeight;
	}
	
	public int getAdvertisePrice() {
		return advertisePrice;
	}

	public void setAdvertisePrice(int advertisePrice) {
		this.advertisePrice = advertisePrice;
	}
	
	@Length(min=1, max=32, message="展示内容类型长度必须介于 1 和 32 之间")
	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	
	@Length(min=1, max=32, message="展示内容ID长度必须介于 1 和 32 之间")
	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	
}