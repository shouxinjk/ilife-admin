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
	private Integer societyLevel;		// society_level
	private Double societyScore;		// society_score
	private Integer cultureLevel;		// culture_level
	private Double cultureScore;		// culture_score
	private String expression;		// expression
	private String matrix;		// matrix
	private Integer economyLevel;		// economy_level
	private Double economyScore;		// economy_score
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
	
	public Integer getSocietyLevel() {
		return societyLevel;
	}

	public void setSocietyLevel(Integer societyLevel) {
		this.societyLevel = societyLevel;
	}
	
	public Double getSocietyScore() {
		return societyScore;
	}

	public void setSocietyScore(Double societyScore) {
		this.societyScore = societyScore;
	}
	
	public Integer getCultureLevel() {
		return cultureLevel;
	}

	public void setCultureLevel(Integer cultureLevel) {
		this.cultureLevel = cultureLevel;
	}
	
	public Double getCultureScore() {
		return cultureScore;
	}

	public void setCultureScore(Double cultureScore) {
		this.cultureScore = cultureScore;
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
	
	public Double getEconomyScore() {
		return economyScore;
	}

	public void setEconomyScore(Double economyScore) {
		this.economyScore = economyScore;
	}
	
	@Length(min=0, max=1000, message="description长度必须介于 0 和 1000 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}