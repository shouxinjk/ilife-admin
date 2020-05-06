/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 用户属性VALS标注Entity
 * @author qchzhu
 * @version 2020-05-06
 */
public class PersonaMeasure extends DataEntity<PersonaMeasure> {
	
	private static final long serialVersionUID = 1L;
	private String description;		// 概要描述
	private double weight=0.1;		// 占比
	private Persona persona;		// 关联的画像
	private Phase phase;		// 所属阶段
	private UserMeasure measure;		// 关联的属性
	private String expression;		// 脚本表达式
	private double alpha=0.2;		// 效益：生理
	private double beta=0.2;		// 效益：安全
	private double gamma=0.2;		// 效益：情感
	private double delte=0.2;		// 效益：尊重
	private double epsilon=0.2;		// 效益：价值
	private double zeta=0.5;		// 成本：经济
	private double eta=0.3;		// 成本：社会
	private double theta=0.2;		// 成本：文化
	private String lambda;		// 偏好表达式
	private int sort = 10;		// sort
	
	public PersonaMeasure() {
		super();
	}

	public PersonaMeasure(String id){
		super(id);
	}

	@Length(min=0, max=255, message="概要描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}
	
	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	
	public UserMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(UserMeasure measure) {
		this.measure = measure;
	}
	
	@Length(min=0, max=1024, message="脚本表达式长度必须介于 0 和 1024 之间")
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}
	
	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
	
	public double getDelte() {
		return delte;
	}

	public void setDelte(double delte) {
		this.delte = delte;
	}
	
	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}
	
	public double getZeta() {
		return zeta;
	}

	public void setZeta(double zeta) {
		this.zeta = zeta;
	}
	
	public double getEta() {
		return eta;
	}

	public void setEta(double eta) {
		this.eta = eta;
	}
	
	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}
	
	@Length(min=0, max=1024, message="偏好表达式长度必须介于 0 和 1024 之间")
	public String getLambda() {
		return lambda;
	}

	public void setLambda(String lambda) {
		this.lambda = lambda;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
	
}