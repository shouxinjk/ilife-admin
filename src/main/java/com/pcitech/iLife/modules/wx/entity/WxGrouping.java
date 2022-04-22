/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 互助班车Entity
 * @author ilife
 * @version 2022-04-22
 */
public class WxGrouping extends DataEntity<WxGrouping> {
	
	private static final long serialVersionUID = 1L;
	private String code;		// 班次
	private String oid;		// 群编号
	private String name;		// 群名称
	private Date eventDate;		// 开车日期
	private Date eventTimeFrom;		// 发车时间
	private Date eventTimeTo;		// 截止时间
	private String subjectType;		// 内容类型:article,account
	private String subjectId;		// 内容ID
	
	public WxGrouping() {
		super();
	}

	public WxGrouping(String id){
		super(id);
	}

	@Length(min=1, max=32, message="班次长度必须介于 1 和 32 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Length(min=0, max=32, message="群编号长度必须介于 0 和 32 之间")
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@Length(min=0, max=32, message="群名称长度必须介于 0 和 32 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEventTimeFrom() {
		return eventTimeFrom;
	}

	public void setEventTimeFrom(Date eventTimeFrom) {
		this.eventTimeFrom = eventTimeFrom;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEventTimeTo() {
		return eventTimeTo;
	}

	public void setEventTimeTo(Date eventTimeTo) {
		this.eventTimeTo = eventTimeTo;
	}
	
	@Length(min=1, max=32, message="内容类型:article,account长度必须介于 1 和 32 之间")
	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	
	@Length(min=1, max=32, message="内容ID长度必须介于 1 和 32 之间")
	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	
}