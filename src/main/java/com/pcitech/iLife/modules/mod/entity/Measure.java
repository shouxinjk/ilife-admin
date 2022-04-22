/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.DataEntity;

/**
 * 商品属性Entity
 * @author chenci
 * @version 2017-09-22
 */
public class Measure extends DataEntity<Measure> {
	
	private static final long serialVersionUID = 1L;
	private ItemCategory category;		// 类别
	private ItemDimension itemDimension;		// 维度
	private String property;		// 属性定义
	private String name;		// 属性名称
	private Double percentage;		// 属性占比
	private Double controlValue;		// 归一化参照值
	private Double defaultScore=5.0;		// 默认评分
	private Integer defaultLevel;		// 默认等级
	private String type;		// 类型
	private String tags;		// 标签
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
	private ItemCategory autoLabelCategory;		// 自动标注引用类别
	private TagCategory autoLabelTagCategory;		// 自动标注标签类别：将自动把tagging、tags打散写入该属性下
	private String autoLabelDict;		// 自动标注引用字典
	private String autoLabelType="manual";		// 自动标注引用字典
	private String normalizeType;		// 归一化类型，支持min-max、max-min、logx、logx-reverse、exp、exp-reverse
	private String multiValueFunc;		// 多值处理方法，支持min、max、sum、avg
	
	public Measure() {
		super();
	}

	public Measure(String id){
		super(id);
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	public ItemDimension getItemDimension() {
		return itemDimension;
	}

	public void setItemDimension(ItemDimension itemDimension) {
		this.itemDimension = itemDimension;
	}

	@Length(min=0, max=100, message="属性定义长度必须介于 0 和 100 之间")
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	@Length(min=0, max=100, message="属性名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public Double getControlValue() {
		return controlValue;
	}

	public void setControlValue(Double controlValue) {
		this.controlValue = controlValue;
	}

	public Double getDefaultScore() {
		return defaultScore;
	}

	public void setDefaultScore(Double defaultScore) {
		this.defaultScore = defaultScore;
	}

	public Integer getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(Integer defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	public ItemCategory getAutoLabelCategory() {
		return autoLabelCategory;
	}

	public void setAutoLabelCategory(ItemCategory autoLabelCategory) {
		this.autoLabelCategory = autoLabelCategory;
	}

	public TagCategory getAutoLabelTagCategory() {
		return autoLabelTagCategory;
	}

	public void setAutoLabelTagCategory(TagCategory autoLabelTagCategory) {
		this.autoLabelTagCategory = autoLabelTagCategory;
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

	@Length(min=0, max=100, message="类型长度必须介于 0 和 100 之间")
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
	
}