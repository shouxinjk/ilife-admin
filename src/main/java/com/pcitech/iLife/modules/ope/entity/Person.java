/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.entity;

import com.pcitech.iLife.modules.mod.entity.Hierarchy;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.LifeStyle;
import com.pcitech.iLife.modules.mod.entity.Persona;
import com.pcitech.iLife.modules.mod.entity.Phase;

/**
 * 用户Entity
 * @author chenci
 * @version 2017-09-27
 */
public class Person extends DataEntity<Person> {
	
	private static final long serialVersionUID = 1L;
	private Double score;		// 分数
	private Integer level;		// 等级
	private String nickname;		// 昵称
	private String economy;		// economy
	private String culture;		// culture
	private String society;		// society
	private String logo;		// 头像
	private String functionality;		// functionality
	private String security;		// security
	private String love;		// love
	private String respect;		// respect
	private String realization;		// realization
	private String lambda;		// 偏好模型
	private Double credit;		// 信用评价
	private Date lastAccess;		// 最后访问时间
	private Double offset;		// 阶段偏移量
	private Integer integrity;		// 信息健全度
	private Phase phase;		// 所属阶段
	private Persona persona;		// 用户分群
	private LifeStyle lifeStyle;		// 生活方式
	private Hierarchy hierarchy; //所属阶层
	
	public Person() {
		super();
	}

	public Person(String id){
		super(id);
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
	@Length(min=0, max=100, message="昵称长度必须介于 0 和 100 之间")
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	@Length(min=0, max=500, message="economy长度必须介于 0 和 500 之间")
	public String getEconomy() {
		return economy;
	}

	public void setEconomy(String economy) {
		this.economy = economy;
	}
	
	@Length(min=0, max=500, message="culture长度必须介于 0 和 500 之间")
	public String getCulture() {
		return culture;
	}

	public void setCulture(String culture) {
		this.culture = culture;
	}
	
	@Length(min=0, max=500, message="society长度必须介于 0 和 500 之间")
	public String getSociety() {
		return society;
	}

	public void setSociety(String society) {
		this.society = society;
	}
	
	@Length(min=0, max=1000, message="头像长度必须介于 0 和 1000 之间")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	@Length(min=0, max=500, message="functionality长度必须介于 0 和 500 之间")
	public String getFunctionality() {
		return functionality;
	}

	public void setFunctionality(String functionality) {
		this.functionality = functionality;
	}
	
	@Length(min=0, max=500, message="security长度必须介于 0 和 500 之间")
	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}
	
	@Length(min=0, max=500, message="love长度必须介于 0 和 500 之间")
	public String getLove() {
		return love;
	}

	public void setLove(String love) {
		this.love = love;
	}
	
	@Length(min=0, max=500, message="respect长度必须介于 0 和 500 之间")
	public String getRespect() {
		return respect;
	}

	public void setRespect(String respect) {
		this.respect = respect;
	}
	
	@Length(min=0, max=500, message="realization长度必须介于 0 和 500 之间")
	public String getRealization() {
		return realization;
	}

	public void setRealization(String realization) {
		this.realization = realization;
	}
	
	@Length(min=0, max=1000, message="偏好模型长度必须介于 0 和 1000 之间")
	public String getLambda() {
		return lambda;
	}

	public void setLambda(String lambda) {
		this.lambda = lambda;
	}
	
	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	public Double getOffset() {
		return offset;
	}

	public void setOffset(Double offset) {
		this.offset = offset;
	}
	
	public Integer getIntegrity() {
		return integrity;
	}

	public void setIntegrity(Integer integrity) {
		this.integrity = integrity;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public LifeStyle getLifeStyle() {
		return lifeStyle;
	}

	public void setLifeStyle(LifeStyle lifeStyle) {
		this.lifeStyle = lifeStyle;
	}

	public Hierarchy getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}
}