/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pcitech.iLife.common.persistence.DataEntity;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;

/**
 * 商品Entity
 * @author chenci
 * @version 2017-09-28
 */
public class Item extends DataEntity<Item> {
	
	private static final long serialVersionUID = 1L;
	private ItemCategory itemCategory;		// 商品类别
	private String keywords;		// 搜索关键字
	private String source;		// 来源
	private String name;		// 名称
	private String url;		// 导购连接
	private String logo;		// 标致图片
	private String summary;		// 摘要
	private Double alpha;		// alpha
	private Double beta;		// beta
	private Double gamma;		// gamma
	private Double delte;		// delte
	private Double epsilon;		// epsilon
	private String cost;		// 成本模型
	private String style;		// 风格模型
	private Date lastAccess;		// 最后浏览
	private Double score;		// 得分
	private String outline;		// 概要
	private Double credit;		// 可信度
	private String status;		// 状态
	
	public Item() {
		super();
	}

	public Item(String id){
		super(id);
	}
	
	public ItemCategory getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(ItemCategory itemCategory) {
		this.itemCategory = itemCategory;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@Length(min=0, max=100, message="来源长度必须介于 0 和 100 之间")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	@Length(min=0, max=100, message="名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=1000, message="导购连接长度必须介于 0 和 1000 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Length(min=0, max=1000, message="标致图片长度必须介于 0 和 1000 之间")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	@Length(min=0, max=1000, message="摘要长度必须介于 0 和 1000 之间")
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public Double getAlpha() {
		return alpha;
	}

	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}
	
	public Double getBeta() {
		return beta;
	}

	public void setBeta(Double beta) {
		this.beta = beta;
	}
	
	public Double getGamma() {
		return gamma;
	}

	public void setGamma(Double gamma) {
		this.gamma = gamma;
	}
	
	public Double getDelte() {
		return delte;
	}

	public void setDelte(Double delte) {
		this.delte = delte;
	}
	
	public Double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(Double epsilon) {
		this.epsilon = epsilon;
	}
	
	@Length(min=0, max=1000, message="成本模型长度必须介于 0 和 1000 之间")
	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}
	
	@Length(min=0, max=1000, message="风格模型长度必须介于 0 和 1000 之间")
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
	
	@Length(min=0, max=500, message="概要长度必须介于 0 和 500 之间")
	public String getOutline() {
		return outline;
	}

	public void setOutline(String outline) {
		this.outline = outline;
	}
	
	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}
	
	@Length(min=0, max=10, message="状态长度必须介于 0 和 10 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}