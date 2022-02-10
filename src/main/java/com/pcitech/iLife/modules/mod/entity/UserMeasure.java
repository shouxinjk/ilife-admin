/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 用户属性定义Entity
 * @author qchzhu
 * @version 2019-03-13
 */
public class UserMeasure extends DataEntity<UserMeasure> {
	
	private static final long serialVersionUID = 1L;
	private UserCategory category;		// category
	private String dimension;		// dimension
	private String property;		// property
	private String name;		// name
	private String weight;		// weight
	private String controlValue;		// control_value
	private String defaultScore;		// default_score
	private String defaultRank;		// default_rank
	private String type;		// type
	private String tags;		// tags
	
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
	private String normalizeType;		// 归一化类型，支持min-max、max-min、logx、logx-reverse、exp、exp-reverse
	private String multiValueFunc;		// 多值处理方法，支持min、max、sum、avg
	private UserTagCategory autoLabelTagCategory;		// 自动标注标签类别：将自动把tagging、tags打散写入该属性下
	
	private UserCategory autoLabelCategory;		// 自动标注引用类别
	private String autoLabelDict;		// 自动标注引用字典
	private String autoLabelType;		// 自动标注引用字典
	
	public UserMeasure() {
		super();
	}

	public UserMeasure(String id){
		super(id);
	}

	public UserCategory getCategory() {
		return category;
	}

	public void setCategory(UserCategory category) {
		this.category = category;
	}
	
	@Length(min=0, max=100, message="dimension长度必须介于 0 和 100 之间")
	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
	
	@Length(min=0, max=100, message="property长度必须介于 0 和 100 之间")
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	@Length(min=0, max=100, message="name长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	public String getControlValue() {
		return controlValue;
	}

	public void setControlValue(String controlValue) {
		this.controlValue = controlValue;
	}
	
	public String getDefaultScore() {
		return defaultScore;
	}

	public void setDefaultScore(String defaultScore) {
		this.defaultScore = defaultScore;
	}
	
	@Length(min=0, max=8, message="default_rank长度必须介于 0 和 8 之间")
	public String getDefaultRank() {
		return defaultRank;
	}

	public void setDefaultRank(String defaultRank) {
		this.defaultRank = defaultRank;
	}
	
	public UserCategory getAutoLabelCategory() {
		return autoLabelCategory;
	}

	public void setAutoLabelCategory(UserCategory autoLabelCategory) {
		this.autoLabelCategory = autoLabelCategory;
	}

	public String getAutoLabelDict() {
		return autoLabelDict;
	}

	public void setAutoLabelDict(String autoLabelDict) {
		this.autoLabelDict = autoLabelDict;
	}

	public String getAutoLabelType() {
		return autoLabelType;
	}

	public void setAutoLabelType(String autoLabelType) {
		this.autoLabelType = autoLabelType;
	}

	@Length(min=0, max=100, message="type长度必须介于 0 和 100 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

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

	public String getLambda() {
		return lambda;
	}

	public void setLambda(String lambda) {
		this.lambda = lambda;
	}

	public String getNormalizeType() {
		return normalizeType;
	}

	public void setNormalizeType(String normalizeType) {
		this.normalizeType = normalizeType;
	}

	public String getMultiValueFunc() {
		return multiValueFunc;
	}

	public void setMultiValueFunc(String multiValueFunc) {
		this.multiValueFunc = multiValueFunc;
	}

	public UserTagCategory getAutoLabelTagCategory() {
		return autoLabelTagCategory;
	}

	public void setAutoLabelTagCategory(UserTagCategory autoLabelTagCategory) {
		this.autoLabelTagCategory = autoLabelTagCategory;
	}
	
}