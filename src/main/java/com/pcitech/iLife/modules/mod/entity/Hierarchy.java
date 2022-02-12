/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 社会分层Entity
 * @author chenci
 * @version 2017-09-15
 */
public class Hierarchy extends DataEntity<Hierarchy> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// name
	private String displayName;		// 显示名称
	private Integer societyLevel;		// society_level
	private Double societyScoreMax;		// society_score
	private Double societyScoreMin;		// society_score
	private Integer cultureLevel;		// culture_level
	private Double cultureScoreMin;		// culture_score
	private Double cultureScoreMax;		// culture_score
	private String expression;		// expression
	private String matrix;		// matrix
	private Integer economyLevel;		// economy_level
	private Double economyScoreMin;		// economy_score
	private Double economyScoreMax;		// economy_score
	private String description;		// description
	
	public Hierarchy() {
		super();
	}

	public Hierarchy(String id){
		super(id);
	}

	@Length(min=0, max=100, message="name长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Integer getSocietyLevel() {
		return societyLevel;
	}

	public void setSocietyLevel(Integer societyLevel) {
		this.societyLevel = societyLevel;
	}

	public Integer getCultureLevel() {
		return cultureLevel;
	}

	public void setCultureLevel(Integer cultureLevel) {
		this.cultureLevel = cultureLevel;
	}
	
	@Length(min=0, max=1000, message="expression长度必须介于 0 和 1000 之间")
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Length(min=0, max=1000, message="matrix长度必须介于 0 和 1000 之间")
	public String getMatrix() {
		return matrix;
	}

	public void setMatrix(String matrix) {
		this.matrix = matrix;
	}
	
	public Integer getEconomyLevel() {
		return economyLevel;
	}

	public void setEconomyLevel(Integer economyLevel) {
		this.economyLevel = economyLevel;
	}
	
	@Length(min=0, max=1000, message="description长度必须介于 0 和 1000 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getSocietyScoreMax() {
		return societyScoreMax;
	}

	public void setSocietyScoreMax(Double societyScoreMax) {
		this.societyScoreMax = societyScoreMax;
	}

	public Double getSocietyScoreMin() {
		return societyScoreMin;
	}

	public void setSocietyScoreMin(Double societyScoreMin) {
		this.societyScoreMin = societyScoreMin;
	}

	public Double getCultureScoreMin() {
		return cultureScoreMin;
	}

	public void setCultureScoreMin(Double cultureScoreMin) {
		this.cultureScoreMin = cultureScoreMin;
	}

	public Double getCultureScoreMax() {
		return cultureScoreMax;
	}

	public void setCultureScoreMax(Double cultureScoreMax) {
		this.cultureScoreMax = cultureScoreMax;
	}

	public Double getEconomyScoreMin() {
		return economyScoreMin;
	}

	public void setEconomyScoreMin(Double economyScoreMin) {
		this.economyScoreMin = economyScoreMin;
	}

	public Double getEconomyScoreMax() {
		return economyScoreMax;
	}

	public void setEconomyScoreMax(Double economyScoreMax) {
		this.economyScoreMax = economyScoreMax;
	}
}